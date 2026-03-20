package org.ziskadev.stronger.domain.model

/**
 * Domain model for a single exercise.
 * Muscles and equipment are resolved from their respective relation tables.
 */
data class Exercise(
    val id: String,
    val nameDe: String?,
    val nameEn: String?,
    val descriptionDe: String?,
    val descriptionEn: String?,
    val videoUrl: String?,
    val thumbnailUrl: String?,
    val exerciseType: String?,
    val isCustom: Boolean,
    val primaryMuscles: List<String>,
    val secondaryMuscles: List<String>,
    val equipment: List<String>,
)