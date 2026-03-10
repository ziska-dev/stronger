package org.ziskadev.stronger.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.ziskadev.stronger.domain.model.AppLanguage
import org.ziskadev.stronger.domain.model.AppSettings
import org.ziskadev.stronger.domain.model.WeightUnit

/**
 * Interacts with the SQLDelight database for AppSettings.
 * Translates between database world and domain world.
 */
class AppSettingsLocalDataSource(private val db: StrongerDatabase) {

    /**
     * Returns the app settings as a flow (reactive data stream)
     * Map converts SQLDelight row into domain model AppSettings
     */
    fun getSettings(): Flow<AppSettings> =
        db.appSettingsQueries
            .getSettings()
            .asFlow()
            .mapToOne(Dispatchers.IO)
            .map { row ->
                AppSettings(
                    weightUnit = WeightUnit.valueOf(row.weightUnit),
                    language = AppLanguage.valueOf(row.language),
                    defaultPauseBetweenSetsSeconds = row.defaultPauseBetweenSetsSeconds.toInt(),
                    defaultPauseBetweenExercisesSeconds = row.defaultPauseBetweenExercisesSeconds.toInt(),
                    defaultPauseBetweenRoundsSeconds = row.defaultPauseBetweenRoundsSeconds.toInt(),
                    autoStartTimers = row.autoStartTimers == 1L,
                    autoAdvanceExercises = row.autoAdvanceExercises == 1L,
                )
            }

    /**
     * Write functions to return Unit for repository interface:
     * Problem: SQLDelight does not generate suspend functions for UPDATE / INSERT --> code is synchronous
     * Solution: Defer call to the I/O thread withContext(Dispatchers.IO)
     */
    suspend fun updateWeightUnit(unit: WeightUnit): Unit =
        withContext(Dispatchers.IO) {
         db.appSettingsQueries.updateWeightUnit(unit.name)
        }

    suspend fun updateLanguage(language: AppLanguage): Unit =
        withContext(Dispatchers.IO) {
            db.appSettingsQueries.updateLanguage(language.name)
        }

    suspend fun updateDefaultPauses(sets: Int, exercises: Int, rounds: Int): Unit =
        withContext(Dispatchers.IO) {
            db.appSettingsQueries.updateDefaultPauses(
                sets = sets.toLong(),
                exercises = exercises.toLong(),
                rounds = rounds.toLong(),
            )
        }

    suspend fun updateAutoStartTimers(enabled: Boolean): Unit =
        withContext(Dispatchers.IO) {
            db.appSettingsQueries.updateAutoStartTimers(if (enabled) 1L else 0L)
        }

    suspend fun updateAutoAdvanceExercises(enabled: Boolean): Unit =
        withContext(Dispatchers.IO) {
            db.appSettingsQueries.updateAutoAdvanceExercises(if (enabled) 1L else 0L)
        }
}