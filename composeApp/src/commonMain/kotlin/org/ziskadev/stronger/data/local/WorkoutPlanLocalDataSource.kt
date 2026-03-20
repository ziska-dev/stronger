package org.ziskadev.stronger.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.ziskadev.stronger.domain.model.WorkoutPlan
import org.ziskadev.stronger.domain.model.WorkoutPlanExercise
import org.ziskadev.stronger.data.local.WorkoutPlan as WorkoutPlanEntity

/**
 * Local data source for workout plans via SQLDelight.
 * Translates between DB rows and domain models.
 */
class WorkoutPlanLocalDataSource(private val db: StrongerDatabase) {

    fun getPlans(): Flow<List<WorkoutPlan>> =
        db.workoutPlanQueries
            .getAllPlans()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    fun getExercisesForPlan(planId: Long): Flow<List<WorkoutPlanExercise>> =
        db.workoutPlanQueries
            .getExercisesForPlan(planId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    /** Inserts a plan and returns the generated ID. */
    suspend fun insertPlan(plan: WorkoutPlan): Long =
        withContext(Dispatchers.IO) {
            db.transactionWithResult {
                db.workoutPlanQueries.insertPlan(
                    name = plan.name,
                    description = plan.description,
                    workoutType = plan.workoutType,
                    rounds = plan.rounds?.toLong(),
                    pauseBetweenRoundsSeconds = plan.pauseBetweenRoundsSeconds?.toLong(),
                    pauseBetweenSetsSeconds = plan.pauseBetweenSetsSeconds?.toLong(),
                    pauseBetweenExercisesSeconds = plan.pauseBetweenExercisesSeconds?.toLong(),
                    createdAt = plan.createdAt,
                    updatedAt = plan.updatedAt,
                )
                db.workoutPlanQueries.lastInsertRowId().executeAsOne()
            }
        }

    suspend fun updatePlan(plan: WorkoutPlan): Unit =
        withContext(Dispatchers.IO) {
            db.workoutPlanQueries.updatePlan(
                id = plan.id,
                name = plan.name,
                description = plan.description,
                workoutType = plan.workoutType,
                rounds = plan.rounds?.toLong(),
                pauseBetweenRoundsSeconds = plan.pauseBetweenRoundsSeconds?.toLong(),
                pauseBetweenSetsSeconds = plan.pauseBetweenSetsSeconds?.toLong(),
                pauseBetweenExercisesSeconds = plan.pauseBetweenExercisesSeconds?.toLong(),
                updatedAt = plan.updatedAt,
            )
        }

    suspend fun deletePlan(planId: Long): Unit =
        withContext(Dispatchers.IO) {
            db.workoutPlanQueries.deletePlan(planId)
        }

    suspend fun insertPlanExercise(exercise: WorkoutPlanExercise): Unit =
        withContext(Dispatchers.IO) {
            db.workoutPlanQueries.insertPlanExercise(
                planId = exercise.planId,
                exerciseId = exercise.exerciseId,
                position = exercise.position.toLong(),
                sets = exercise.sets?.toLong(),
                pauseAfterSetSeconds = exercise.pauseAfterSetSeconds?.toLong(),
                pauseAfterExerciseSeconds = exercise.pauseAfterExerciseSeconds?.toLong(),
            )
        }

    suspend fun updatePlanExercise(exercise: WorkoutPlanExercise): Unit =
        withContext(Dispatchers.IO) {
            db.workoutPlanQueries.updatePlanExercise(
                id = exercise.id,
                position = exercise.position.toLong(),
                sets = exercise.sets?.toLong(),
                pauseAfterSetSeconds = exercise.pauseAfterSetSeconds?.toLong(),
                pauseAfterExerciseSeconds = exercise.pauseAfterExerciseSeconds?.toLong(),
            )
        }

    suspend fun deletePlanExercise(exerciseId: Long): Unit =
        withContext(Dispatchers.IO) {
            db.workoutPlanQueries.deletePlanExercise(exerciseId)
        }

    suspend fun deleteAllExercisesForPlan(planId: Long): Unit =
        withContext(Dispatchers.IO) {
            db.workoutPlanQueries.deleteAllExercisesForPlan(planId)
        }

    suspend fun reorderExercises(orderedIds: List<Long>): Unit =
        withContext(Dispatchers.IO) {
            db.transaction {
                orderedIds.forEachIndexed { index, id ->
                    db.workoutPlanQueries.updatePlanExercisePosition(
                        position = index.toLong(),
                        id = id,
                    )
                }
            }
        }

    private fun WorkoutPlanEntity.toDomain(): WorkoutPlan = WorkoutPlan(
        id = id,
        name = name,
        description = description,
        workoutType = workoutType,
        rounds = rounds?.toInt(),
        pauseBetweenRoundsSeconds = pauseBetweenRoundsSeconds?.toInt(),
        pauseBetweenSetsSeconds = pauseBetweenSetsSeconds?.toInt(),
        pauseBetweenExercisesSeconds = pauseBetweenExercisesSeconds?.toInt(),
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    private fun GetExercisesForPlan.toDomain(): WorkoutPlanExercise = WorkoutPlanExercise(
        id = id,
        planId = planId,
        exerciseId = exerciseId,
        position = position.toInt(),
        sets = sets?.toInt(),
        pauseAfterSetSeconds = pauseAfterSetSeconds?.toInt(),
        pauseAfterExerciseSeconds = pauseAfterExerciseSeconds?.toInt(),
        nameEn = nameEn,
        nameDe = nameDe,
        thumbnailUrl = thumbnailUrl,
        videoUrl = videoUrl,
        exerciseType = exerciseType,
    )
}