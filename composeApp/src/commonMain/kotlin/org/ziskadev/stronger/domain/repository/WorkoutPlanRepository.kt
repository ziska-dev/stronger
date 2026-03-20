package org.ziskadev.stronger.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ziskadev.stronger.domain.model.WorkoutPlan
import org.ziskadev.stronger.domain.model.WorkoutPlanExercise

/**
 * Interface for workout plan data access (domain layer).
 * Plans and their exercises are loaded separately for efficiency.
 */
interface WorkoutPlanRepository {

    /** Emits all plans ordered by last update, without exercises. */
    fun getPlans(): Flow<List<WorkoutPlan>>

    /** Emits exercises for a specific plan, ordered by position. */
    fun getExercisesForPlan(planId: Long): Flow<List<WorkoutPlanExercise>>

    suspend fun insertPlan(plan: WorkoutPlan): Long

    suspend fun updatePlan(plan: WorkoutPlan)

    suspend fun deletePlan(planId: Long)

    suspend fun insertPlanExercise(exercise: WorkoutPlanExercise)

    suspend fun updatePlanExercise(exercise: WorkoutPlanExercise)

    suspend fun deletePlanExercise(exerciseId: Long)

    suspend fun deleteAllExercisesForPlan(planId: Long)

    /**
     * Updates position for all exercises in a plan.
     * [orderedIds] = list of WorkoutPlanExercise.id values in desired order.
     * New position = index in list.
     */
    suspend fun reorderExercises(planId: Long, orderedIds: List<Long>)
}