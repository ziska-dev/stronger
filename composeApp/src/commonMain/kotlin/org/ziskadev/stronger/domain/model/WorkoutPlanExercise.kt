package org.ziskadev.stronger.domain.model

/**
 * A single exercise slot within a workout plan.
 * Exercise name/thumbnail fields are denormalized from the Exercise table (via JOIN query).
 * Pause fields are nullable: NULL = inherit from plan or app default.
 */
data class WorkoutPlanExercise(
    val id: Long,
    val planId: Long,
    val exerciseId: String,
    val position: Int,
    val sets: Int?,
    val pauseAfterSetSeconds: Int?,
    val pauseAfterExerciseSeconds: Int?,
    // Denormalized from Exercise for display without extra lookup
    val nameEn: String?,
    val nameDe: String?,
    val thumbnailUrl: String?,
    val videoUrl: String?,
    val exerciseType: String?,
)