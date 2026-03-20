package org.ziskadev.stronger.domain.model

/**
 * A recorded workout session. Snapshot of a plan at start time.
 */
data class WorkoutSession(
    val id: Long,
    val planId: Long?,
    val planNameSnapshot: String,
    val workoutType: WorkoutType,
    val status: SessionStatus,
    val rounds: Int?,
    val startedAt: Long,
    val finishedAt: Long?,
    val notes: String?,
)