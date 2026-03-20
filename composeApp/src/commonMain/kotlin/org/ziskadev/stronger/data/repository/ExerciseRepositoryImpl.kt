package org.ziskadev.stronger.data.repository

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import org.ziskadev.stronger.data.local.ExerciseLocalDataSource
import org.ziskadev.stronger.data.remote.ExerciseApiService
import org.ziskadev.stronger.data.remote.dto.ExerciseDto
import org.ziskadev.stronger.domain.model.ApiResult
import org.ziskadev.stronger.domain.model.Exercise
import org.ziskadev.stronger.domain.model.SyncResult
import org.ziskadev.stronger.domain.repository.ExerciseRepository
import kotlin.time.Clock

/**
 * Coordinates local DB and remote API for exercises.
 * Strategy: read always from DB, sync from API if DB empty or manually triggered.
 */
class ExerciseRepositoryImpl(
    private val localDataSource: ExerciseLocalDataSource,
    private val apiService: ExerciseApiService,
) : ExerciseRepository {

    companion object {
        private const val TAG = "ExerciseRepository"
    }

    override fun getExercises(): Flow<List<Exercise>> =
        localDataSource.getAllExercises()

    override suspend fun isDatabaseEmpty(): Boolean =
        localDataSource.getExerciseCountOnce() == 0L

    override fun searchExercises(
        query: String,
        muscle: String?,
        equipment: String?,
    ): Flow<List<Exercise>> = when {
        muscle != null && equipment != null ->
            localDataSource.getExercisesByMuscleAndEquipment(muscle, equipment)
        muscle != null ->
            localDataSource.getExercisesByMuscle(muscle)
        equipment != null ->
            localDataSource.getExercisesByEquipment(equipment)
        else ->
            localDataSource.searchExercises(query)
    }

    override suspend fun syncExercises(): SyncResult {
        val countBefore = localDataSource.getExerciseCountOnce()

        return when (val result = apiService.fetchAllExercises()) {
            is ApiResult.Success -> {
                val now = Clock.System.now().toEpochMilliseconds()
                result.data.forEach { dto ->
                    localDataSource.upsertExercise(dto.toDomain(), now)
                }
                val countAfter = localDataSource.getExerciseCountOnce()
                val newCount = (countAfter - countBefore).toInt().coerceAtLeast(0)
                Napier.i("Sync successful. $newCount new exercises.", tag = TAG)
                SyncResult.Success(newCount)
            }
            is ApiResult.Error -> {
                Napier.e("Sync failed: ${result.message}", tag = TAG)
                SyncResult.Error(result.message)
            }
            is ApiResult.Loading -> SyncResult.Error("Unexpected loading state.")
        }
    }

    private fun ExerciseDto.toDomain(): Exercise {
        val primaryMuscles = attributes
            .filter { it.attributeName.name == "PRIMARY_MUSCLE" }
            .map { it.attributeValue.value }
        val secondaryMuscles = attributes
            .filter { it.attributeName.name == "SECONDARY_MUSCLE" }
            .map { it.attributeValue.value }
        val equipment = attributes
            .filter { it.attributeName.name == "EQUIPMENT" }
            .map { it.attributeValue.value }
        val exerciseType = attributes
            .firstOrNull { it.attributeName.name == "TYPE" }
            ?.attributeValue?.value

        return Exercise(
            id = id,
            nameDe = name,       // API field "name" = German
            nameEn = nameEn,
            descriptionDe = description,
            descriptionEn = descriptionEn,
            videoUrl = videoUrl,
            thumbnailUrl = thumbnailUrl,
            exerciseType = exerciseType,
            isCustom = false,
            primaryMuscles = primaryMuscles,
            secondaryMuscles = secondaryMuscles,
            equipment = equipment,
        )
    }
}