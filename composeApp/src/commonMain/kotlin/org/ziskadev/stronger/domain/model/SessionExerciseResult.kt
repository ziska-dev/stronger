package org.ziskadev.stronger.domain.model

/**
 * The actual result of a single set within a session.
 * types/valuesInt/valuesSec/units mirror workout.cool's array-based schema (JSON strings).
 */
data class SessionExerciseResult(
    val id: Long,
    val sessionId: Long,
    val sessionExercisePlanId: Long,
    val setIndex: Int,
    val types: String,
    val valuesInt: String,
    val valuesSec: String,
    val units: String,
    val completed: Boolean,
    val startedAt: Long,
    val completedAt: Long,
)