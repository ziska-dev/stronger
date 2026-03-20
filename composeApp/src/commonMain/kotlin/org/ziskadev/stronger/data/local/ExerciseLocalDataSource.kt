package org.ziskadev.stronger.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.ziskadev.stronger.data.local.Exercise as ExerciseEntity
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
            .getAllExercises()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    fun searchExercises(query: String): Flow<List<Exercise>> =
        db.exerciseQueries
            .searchExercises(query)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    fun getExercisesByMuscle(muscle: String): Flow<List<Exercise>> =
        db.exerciseQueries
            .getExercisesByMuscle(muscle)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    fun getExercisesByEquipment(equipment: String): Flow<List<Exercise>> =
        db.exerciseQueries
            .getExercisesByEquipment(equipment)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    fun getExercisesByMuscleAndEquipment(muscle: String, equipment: String): Flow<List<Exercise>> =
        db.exerciseQueries
            .getExercisesByMuscleAndEquipment(muscle, equipment)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    suspend fun softDeleteExercise(exerciseId: String): Unit =
        withContext(Dispatchers.IO) {
            db.exerciseQueries.softDeleteExercise(exerciseId)
        }

    fun getFavoriteExercises(): Flow<List<Exercise>> =
        db.exerciseQueries
            .getFavoriteExercises()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

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

    /** Maps a DB row to domain model. Fetches muscles and equipment synchronously within IO context.
     * Note: executes 2 additional queries per exercise (muscles + equipment).
     * Acceptable for Phase 1, but may need optimization (e.g. JOIN query) if list performance degrades.
     * */
    private fun ExerciseEntity.toDomain(): Exercise {
        val muscles = db.exerciseQueries.getMusclesForExercise(id).executeAsList()
        val equipmentList = db.exerciseQueries.getEquipmentForExercise(id).executeAsList()
        return Exercise(
            id = id,
            nameDe = nameDe,
            nameEn = nameEn,
            descriptionDe = descriptionDe,
            descriptionEn = descriptionEn,
            videoUrl = videoUrl,
            thumbnailUrl = thumbnailUrl,
            exerciseType = exerciseType,
            isCustom = isCustom == 1L,
            primaryMuscles = muscles.filter { it.isPrimary == 1L }.map { it.muscle },
            secondaryMuscles = muscles.filter { it.isPrimary == 0L }.map { it.muscle },
            equipment = equipmentList.map { it.equipment },
        )
    }
}