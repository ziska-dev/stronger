package org.ziskadev.stronger.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ziskadev.stronger.domain.model.Exercise
import org.ziskadev.stronger.domain.model.SyncResult

/**
 * Interface for exercise data access (domain layer).
 * Defines WHAT can be done, not HOW.
 */
interface ExerciseRepository {

    /** Emits the full exercise list from local DB. Triggers sync if DB is empty. */
    fun getExercises(): Flow<List<Exercise>>

    /** Filters exercises by query, muscle, and/or equipment. Empty string = no filter. */
    fun searchExercises(
        query: String,
        muscle: String?,
        equipment: String?,
    ): Flow<List<Exercise>>

    /** Fetches all exercises from backend and saves to local DB. Returns sync result. */
    suspend fun syncExercises(): SyncResult

    /** Returns true if no exercises are cached locally. */
    suspend fun isDatabaseEmpty(): Boolean
}