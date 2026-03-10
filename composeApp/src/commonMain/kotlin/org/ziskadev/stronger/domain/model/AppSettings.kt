package org.ziskadev.stronger.domain.model

/**
 * Domain model for global app settings with default values
 */

data class AppSettings(
    val weightUnit: WeightUnit = WeightUnit.KG,
    val language: AppLanguage = AppLanguage.DE,
    val defaultPauseBetweenSetsSeconds: Int = 30,
    val defaultPauseBetweenExercisesSeconds: Int = 15,
    val defaultPauseBetweenRoundsSeconds: Int = 45,
    val autoStartTimers: Boolean = false,
    val autoAdvanceExercises: Boolean = false,
)

enum class WeightUnit { KG, LBS }

enum class AppLanguage { EN, DE }