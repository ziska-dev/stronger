package org.ziskadev.stronger.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.ziskadev.stronger.domain.model.Exercise

/**
 * Local data source for exercises via SQLDelight.
 * Translates between DB rows and domain model.
 */
class ExerciseLocalDataSource(private val db: StrongerDatabase) {

    fun getExerciseCount(): Flow<Long> =
        db.exerciseQueries
            .countExercises()
            .asFlow()
            .mapToOne(Dispatchers.IO)

    fun getAllExercises(): Flow<List<Exercise>> =
        db.exerciseQueries
            .getAllExercisesWithDetails()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { row ->
                mapRowToExercise(row.id, row.nameDe, row.nameEn, row.descriptionDe,
                    row.descriptionEn, row.videoUrl, row.thumbnailUrl, row.exerciseType,
                    row.isCustom, row.primaryMuscles, row.secondaryMuscles, row.equipment)
            }}

    fun searchExercises(query: String): Flow<List<Exercise>> =
        db.exerciseQueries
            .searchExercisesWithDetails(query)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { row ->
                mapRowToExercise(row.id, row.nameDe, row.nameEn, row.descriptionDe,
                    row.descriptionEn, row.videoUrl, row.thumbnailUrl, row.exerciseType,
                    row.isCustom, row.primaryMuscles, row.secondaryMuscles, row.equipment)
            }}

    fun getExercisesByMuscle(muscle: String): Flow<List<Exercise>> =
        db.exerciseQueries
            .getExercisesByMuscleWithDetails(muscle)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { row ->
                mapRowToExercise(row.id, row.nameDe, row.nameEn, row.descriptionDe,
                    row.descriptionEn, row.videoUrl, row.thumbnailUrl, row.exerciseType,
                    row.isCustom, row.primaryMuscles, row.secondaryMuscles, row.equipment)
            }}

    fun getExercisesByEquipment(equipment: String): Flow<List<Exercise>> =
        db.exerciseQueries
            .getExercisesByEquipmentWithDetails(equipment)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { row ->
                mapRowToExercise(row.id, row.nameDe, row.nameEn, row.descriptionDe,
                    row.descriptionEn, row.videoUrl, row.thumbnailUrl, row.exerciseType,
                    row.isCustom, row.primaryMuscles, row.secondaryMuscles, row.equipment)
            }}

    fun getExercisesByMuscleAndEquipment(muscle: String, equipment: String): Flow<List<Exercise>> =
        db.exerciseQueries
            .getExercisesByMuscleAndEquipmentWithDetails(muscle, equipment)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { row ->
                mapRowToExercise(row.id, row.nameDe, row.nameEn, row.descriptionDe,
                    row.descriptionEn, row.videoUrl, row.thumbnailUrl, row.exerciseType,
                    row.isCustom, row.primaryMuscles, row.secondaryMuscles, row.equipment)
            }}

    fun getFavoriteExercises(): Flow<List<Exercise>> =
        db.exerciseQueries
            .getFavoriteExercisesWithDetails()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { row ->
                mapRowToExercise(row.id, row.nameDe, row.nameEn, row.descriptionDe,
                    row.descriptionEn, row.videoUrl, row.thumbnailUrl, row.exerciseType,
                    row.isCustom, row.primaryMuscles, row.secondaryMuscles, row.equipment)
            }}

    suspend fun softDeleteExercise(exerciseId: String): Unit =
        withContext(Dispatchers.IO) {
            db.exerciseQueries.softDeleteExercise(exerciseId)
        }

    /** Toggles favorite status; not affected by sync due to upsert strategy. */
    suspend fun toggleFavorite(exerciseId: String, isFavorite: Boolean): Unit =
        withContext(Dispatchers.IO) {
            db.exerciseQueries.toggleFavorite(
                id = exerciseId,
                isFavorite = if (isFavorite) 1L else 0L,
            )
        }

    suspend fun getExerciseCountOnce(): Long =
        withContext(Dispatchers.IO) {
            db.exerciseQueries.countExercises().executeAsOne()
        }

    /** Upserts a single exercise with its muscles and equipment atomically. */
    suspend fun upsertExercise(exercise: Exercise, cachedAt: Long): Unit =
        withContext(Dispatchers.IO) {
            db.transaction {
                // Insert if not exists (preserves isDeleted + isFavorite for existing rows)
                db.exerciseQueries.upsertExercise(
                    id = exercise.id,
                    nameEn = exercise.nameEn,
                    nameDe = exercise.nameDe,
                    descriptionEn = exercise.descriptionEn,
                    descriptionDe = exercise.descriptionDe,
                    videoUrl = exercise.videoUrl,
                    thumbnailUrl = exercise.thumbnailUrl,
                    exerciseType = exercise.exerciseType,
                    isCustom = if (exercise.isCustom) 1L else 0L,
                    cachedAt = cachedAt,
                )
                // Update data fields for existing rows (leaves isDeleted + isFavorite untouched)
                db.exerciseQueries.updateExerciseData(
                    id = exercise.id,
                    nameEn = exercise.nameEn,
                    nameDe = exercise.nameDe,
                    descriptionEn = exercise.descriptionEn,
                    descriptionDe = exercise.descriptionDe,
                    videoUrl = exercise.videoUrl,
                    thumbnailUrl = exercise.thumbnailUrl,
                    exerciseType = exercise.exerciseType,
                    cachedAt = cachedAt,
                )
                // Delete and reinsert muscles and equipment to stay in sync with backend
                db.exerciseQueries.deleteMusclesForExercise(exercise.id)
                db.exerciseQueries.deleteEquipmentForExercise(exercise.id)

                exercise.primaryMuscles.forEach { muscle ->
                    db.exerciseQueries.insertMuscle(exercise.id, muscle, 1L)
                }
                exercise.secondaryMuscles.forEach { muscle ->
                    db.exerciseQueries.insertMuscle(exercise.id, muscle, 0L)
                }
                exercise.equipment.forEach { equipment ->
                    db.exerciseQueries.insertEquipment(exercise.id, equipment)
                }
            }
        }

    /** Maps JOIN query result fields to domain model; shared across all exercise list queries. */
    private fun mapRowToExercise(
        id: String,
        nameDe: String?,
        nameEn: String?,
        descriptionDe: String?,
        descriptionEn: String?,
        videoUrl: String?,
        thumbnailUrl: String?,
        exerciseType: String?,
        isCustom: Long,
        primaryMuscles: String?,
        secondaryMuscles: String?,
        equipment: String?,
    ): Exercise = Exercise(
        id = id,
        nameDe = nameDe,
        nameEn = nameEn,
        descriptionDe = descriptionDe,
        descriptionEn = descriptionEn,
        videoUrl = videoUrl,
        thumbnailUrl = thumbnailUrl,
        exerciseType = exerciseType,
        isCustom = isCustom == 1L,
        primaryMuscles = primaryMuscles?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        secondaryMuscles = secondaryMuscles?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
        equipment = equipment?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
    )
}