package org.ziskadev.stronger.data.repository

import kotlinx.coroutines.flow.Flow
import org.ziskadev.stronger.data.local.AppSettingsLocalDataSource
import org.ziskadev.stronger.domain.model.AppLanguage
import org.ziskadev.stronger.domain.model.AppSettings
import org.ziskadev.stronger.domain.model.WeightUnit
import org.ziskadev.stronger.domain.repository.AppSettingsRepository

/**
 * Implementation for AppSettingsRepository (data layer)
 */
class AppSettingsRepositoryImpl(
    private val localDataSource: AppSettingsLocalDataSource,
) : AppSettingsRepository {

    override fun getSettings(): Flow<AppSettings> =
        localDataSource.getSettings()

    override suspend fun updateWeightUnit(unit: WeightUnit) =
        localDataSource.updateWeightUnit(unit)

    override suspend fun updateLanguage(language: AppLanguage) =
        localDataSource.updateLanguage(language)

    override suspend fun updateDefaultPauses(setsPause: Int, exercisesPause: Int, roundsPause: Int) =
        localDataSource.updateDefaultPauses(setsPause, exercisesPause, roundsPause)

    override suspend fun updateAutoStartTimers(enabled: Boolean) =
        localDataSource.updateAutoStartTimers(enabled)

    override suspend fun updateAutoAdvanceExercises(enabled: Boolean) =
        localDataSource.updateAutoAdvanceExercises(enabled)
}