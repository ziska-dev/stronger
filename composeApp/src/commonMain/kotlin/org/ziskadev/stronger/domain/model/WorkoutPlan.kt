package org.ziskadev.stronger.domain.model

/**
 * Domain model for a workout plan (metadata only, no exercises).
 */
data class WorkoutPlan(
    val id: Long,
    val name: String,
    val description: String?,
    val workoutType: String,
    val rounds: Int?,
    val pauseBetweenRoundsSeconds: Int?,
    val pauseBetweenSetsSeconds: Int?,
    val pauseBetweenExercisesSeconds: Int?,
    val createdAt: Long,
    val updatedAt: Long,
)