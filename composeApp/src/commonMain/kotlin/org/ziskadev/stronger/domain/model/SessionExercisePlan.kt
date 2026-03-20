package org.ziskadev.stronger.domain.model

/**
 * Snapshot of a single exercise slot within a session.
 * Decoupled from WorkoutPlanExercise — changes to the plan do not affect this.
 */
data class SessionExercisePlan(
    val id: Long,
    val sessionId: Long,
    val exerciseId: String,
    val position: Int,
    val sets: Int?,
    val pauseAfterSetSeconds: Int?,
    val pauseAfterExerciseSeconds: Int?,
    val skipped: Boolean,
)