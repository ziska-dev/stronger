package org.ziskadev.stronger.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ziskadev.stronger.domain.model.AppLanguage
import org.ziskadev.stronger.domain.model.AppSettings
import org.ziskadev.stronger.domain.model.WeightUnit

/**
 * Interface for the AppSettings (domain layer).
 * Defines WHAT can be done with AppSettings but not HOW
 */
interface AppSettingsRepository {

    /** Emits the current settings and updates on every change. */
    fun getSettings(): Flow<AppSettings>

    suspend fun updateWeightUnit(unit: WeightUnit)

    suspend fun updateLanguage(language: AppLanguage)

    suspend fun updateDefaultPauses(
        setsPause: Int,
        exercisesPause: Int,
        roundsPause: Int,
    )

    suspend fun updateAutoStartTimers(enabled: Boolean)

    suspend fun updateAutoAdvanceExercises(enabled: Boolean)
}