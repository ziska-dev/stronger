package org.ziskadev.stronger.data.repository

import kotlinx.coroutines.flow.Flow
import org.ziskadev.stronger.data.local.WorkoutSessionLocalDataSource
import org.ziskadev.stronger.domain.model.SessionExercisePlan
import org.ziskadev.stronger.domain.model.SessionExerciseResult
import org.ziskadev.stronger.domain.model.SessionStatus
import org.ziskadev.stronger.domain.model.WorkoutPlan
import org.ziskadev.stronger.domain.model.WorkoutPlanExercise
import org.ziskadev.stronger.domain.model.WorkoutSession
import org.ziskadev.stronger.domain.repository.WorkoutSessionRepository
import kotlin.time.Clock

/**
 * Coordinates session lifecycle: creation, tracking, completion.
 * Sessions are snapshots — changes to the original plan have no effect.
 */
class WorkoutSessionRepositoryImpl(
    private val localDataSource: WorkoutSessionLocalDataSource,
) : WorkoutSessionRepository {

    override suspend fun createSession(
        plan: WorkoutPlan,
        exercises: List<WorkoutPlanExercise>,
    ): Long {
        val now = Clock.System.now().toEpochMilliseconds()
        // Insert session header first to get the generated ID
        val sessionId = localDataSource.insertSession(
            planId = plan.id,
            planNameSnapshot = plan.name,
            workoutType = plan.workoutType,
            rounds = plan.rounds,
            startedAt = now,
        )
        // Insert snapshot of each exercise in the adjusted order
        exercises.forEachIndexed { index, exercise ->
            localDataSource.insertSessionExercisePlan(
                sessionId = sessionId,
                exerciseId = exercise.exerciseId,
                // index from list not from original workout plan - for quick adjustments
                position = index,
                sets = exercise.sets,
                pauseAfterSetSeconds = exercise.pauseAfterSetSeconds,
                pauseAfterExerciseSeconds = exercise.pauseAfterExerciseSeconds,
            )
        }
        return sessionId
    }

    override suspend fun getRunningSession(): WorkoutSession? =
        localDataSource.getRunningSession()

    override fun getRecentSessions(limit: Long): Flow<List<WorkoutSession>> =
        localDataSource.getRecentSessions(limit)

    override fun getExercisePlansForSession(sessionId: Long): Flow<List<SessionExercisePlan>> =
        localDataSource.getExercisePlansForSession(sessionId)

    override suspend fun saveExerciseResult(result: SessionExerciseResult) =
        localDataSource.insertResult(result)

    override suspend fun updateExerciseResult(result: SessionExerciseResult) =
        localDataSource.updateResultCompleted(result)

    override suspend fun getLastResultForExercise(exerciseId: String): SessionExerciseResult? =
        localDataSource.getLastResultForExercise(exerciseId)

    override suspend fun completeSession(sessionId: Long, finishedAt: Long) =
        localDataSource.updateSessionStatus(sessionId, SessionStatus.Completed, finishedAt)

    override suspend fun abortSession(sessionId: Long, finishedAt: Long) =
        localDataSource.updateSessionStatus(sessionId, SessionStatus.Aborted, finishedAt)

    override suspend fun updateSessionNotes(sessionId: Long, notes: String) =
        localDataSource.updateSessionNotes(sessionId, notes)

    override suspend fun skipExercise(sessionExercisePlanId: Long) =
        localDataSource.skipExercise(sessionExercisePlanId)

    override suspend fun reorderSessionExercises(sessionId: Long, orderedIds: List<Long>) =
        localDataSource.reorderSessionExercises(orderedIds)
}