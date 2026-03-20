package org.ziskadev.stronger.data.repository

import kotlinx.coroutines.flow.Flow
import org.ziskadev.stronger.data.local.WorkoutPlanLocalDataSource
import org.ziskadev.stronger.domain.model.WorkoutPlan
import org.ziskadev.stronger.domain.model.WorkoutPlanExercise
import org.ziskadev.stronger.domain.repository.WorkoutPlanRepository

/**
 * Delegates all workout plan operations to the local data source.
 * No remote sync needed – plans are user-created and stored locally only.
 */
class WorkoutPlanRepositoryImpl(
    private val localDataSource: WorkoutPlanLocalDataSource,
) : WorkoutPlanRepository {

    override fun getPlans(): Flow<List<WorkoutPlan>> =
        localDataSource.getPlans()

    override fun getExercisesForPlan(planId: Long): Flow<List<WorkoutPlanExercise>> =
        localDataSource.getExercisesForPlan(planId)

    override suspend fun insertPlan(plan: WorkoutPlan): Long =
        localDataSource.insertPlan(plan)

    override suspend fun updatePlan(plan: WorkoutPlan) =
        localDataSource.updatePlan(plan)

    override suspend fun deletePlan(planId: Long) =
        localDataSource.deletePlan(planId)

    override suspend fun insertPlanExercise(exercise: WorkoutPlanExercise) =
        localDataSource.insertPlanExercise(exercise)

    override suspend fun updatePlanExercise(exercise: WorkoutPlanExercise) =
        localDataSource.updatePlanExercise(exercise)

    override suspend fun deletePlanExercise(exerciseId: Long) =
        localDataSource.deletePlanExercise(exerciseId)

    override suspend fun deleteAllExercisesForPlan(planId: Long) =
        localDataSource.deleteAllExercisesForPlan(planId)

    override suspend fun reorderExercises(planId: Long, orderedIds: List<Long>) =
        localDataSource.reorderExercises(orderedIds)
}