package org.ziskadev.stronger.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ziskadev.stronger.domain.model.SessionExercisePlan
import org.ziskadev.stronger.domain.model.SessionExerciseResult
import org.ziskadev.stronger.domain.model.WorkoutPlan
import org.ziskadev.stronger.domain.model.WorkoutPlanExercise
import org.ziskadev.stronger.domain.model.WorkoutSession

/**
 * Interface for workout session data access (domain layer).
 */
interface WorkoutSessionRepository {

    /**
     * Creates a new session as a snapshot of the given plan + exercises.
     * [exercises] may differ from the plan's original exercises (Quick-Adjust).
     * Returns the generated session ID.
     */
    suspend fun createSession(
        plan: WorkoutPlan,
        exercises: List<WorkoutPlanExercise>,
    ): Long

    /** Returns the currently running session, or null if none exists. */
    suspend fun getRunningSession(): WorkoutSession?

    /** Emits recent completed/aborted sessions, newest first. */
    fun getRecentSessions(limit: Long): Flow<List<WorkoutSession>>

    /** Emits all exercise plan snapshots for a session, ordered by position. */
    fun getExercisePlansForSession(sessionId: Long): Flow<List<SessionExercisePlan>>

    /** Saves a set result. Creates or updates depending on whether id == 0L. */
    suspend fun saveExerciseResult(result: SessionExerciseResult): Long

    /** Updates an existing result (e.g. user corrects reps after the set). */
    suspend fun updateExerciseResult(result: SessionExerciseResult)

    /** Returns the last completed result for an exercise across all sessions. Used for pre-filling input fields. */
    suspend fun getLastResultForExercise(exerciseId: String): SessionExerciseResult?

    /** Marks a session as completed. */
    suspend fun completeSession(sessionId: Long, finishedAt: Long)

    /** Marks a session as aborted, saving progress up to this point. */
    suspend fun abortSession(sessionId: Long, finishedAt: Long)

    /** Updates the notes for a finished session. */
    suspend fun updateSessionNotes(sessionId: Long, notes: String)

    /** Marks an exercise as skipped. Results are retained but exercise is excluded from active flow. */
    suspend fun skipExercise(sessionExercisePlanId: Long)

    /**
     * Reorders exercises within a running session.
     * [orderedIds] = list of SessionExercisePlan.id values in desired order.
     */
    suspend fun reorderSessionExercises(sessionId: Long, orderedIds: List<Long>)
}