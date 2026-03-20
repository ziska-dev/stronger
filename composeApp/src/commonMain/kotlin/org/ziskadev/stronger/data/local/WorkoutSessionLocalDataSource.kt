package org.ziskadev.stronger.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.ziskadev.stronger.domain.model.SessionExercisePlan
import org.ziskadev.stronger.domain.model.SessionExerciseResult
import org.ziskadev.stronger.domain.model.SessionStatus
import org.ziskadev.stronger.domain.model.WorkoutSession
import org.ziskadev.stronger.domain.model.WorkoutType
import org.ziskadev.stronger.data.local.SessionExercisePlan as SessionExercisePlanEntity
import org.ziskadev.stronger.data.local.SessionExerciseResult as SessionExerciseResultEntity
import org.ziskadev.stronger.data.local.WorkoutSession as WorkoutSessionEntity

/**
 * Local data source for workout sessions via SQLDelight.
 * Translates between DB rows and domain models.
 */
class WorkoutSessionLocalDataSource(private val db: StrongerDatabase) {

    suspend fun insertSession(
        planId: Long?,
        planNameSnapshot: String,
        workoutType: WorkoutType,
        rounds: Int?,
        startedAt: Long,
    ): Long = withContext(Dispatchers.IO) {
        db.transactionWithResult {
            db.workoutSessionQueries.insertSession(
                planId = planId,
                planNameSnapshot = planNameSnapshot,
                workoutType = workoutType.dbValue,
                rounds = rounds?.toLong(),
                startedAt = startedAt,
            )
            db.workoutSessionQueries.lastInsertRowId().executeAsOne()
        }
    }

    suspend fun insertSessionExercisePlan(
        sessionId: Long,
        exerciseId: String,
        position: Int,
        sets: Int?,
        pauseAfterSetSeconds: Int?,
        pauseAfterExerciseSeconds: Int?,
    ): Unit = withContext(Dispatchers.IO) {
        db.workoutSessionQueries.insertSessionExercisePlan(
            sessionId = sessionId,
            exerciseId = exerciseId,
            position = position.toLong(),
            sets = sets?.toLong(),
            pauseAfterSetSeconds = pauseAfterSetSeconds?.toLong(),
            pauseAfterExerciseSeconds = pauseAfterExerciseSeconds?.toLong()
        )
    }

    suspend fun getRunningSession(): WorkoutSession? =
        withContext(Dispatchers.IO) {
            db.workoutSessionQueries.getRunningSession().executeAsOneOrNull()?.toDomain()
        }

    fun getRecentSessions(limit: Long): Flow<List<WorkoutSession>> =
        db.workoutSessionQueries
            .getRecentSessions(limit)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    fun getExercisePlansForSession(sessionId: Long): Flow<List<SessionExercisePlan>> =
        db.workoutSessionQueries
            .getExercisePlansForSession(sessionId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    suspend fun insertResult(result: SessionExerciseResult): Long =
        withContext(Dispatchers.IO) {
            db.workoutSessionQueries.insertResult(
                sessionId = result.sessionId,
                sessionExercisePlanId = result.sessionExercisePlanId,
                setIndex = result.setIndex.toLong(),
                types = result.types,
                valuesInt = result.valuesInt,
                valuesSec = result.valuesSec,
                units = result.units,
                completed = if (result.completed) 1L else 0L,
                startedAt = result.startedAt,
                completedAt = result.completedAt,
            )
            db.workoutSessionQueries.lastInsertRowId().executeAsOne()
        }

    suspend fun updateResultCompleted(result: SessionExerciseResult): Unit =
        withContext(Dispatchers.IO) {
            db.workoutSessionQueries.updateResultCompleted(
                id = result.id,
                completed = if (result.completed) 1L else 0L,
                valuesInt = result.valuesInt,
                valuesSec = result.valuesSec,
                units = result.units,
                completedAt = result.completedAt,
            )
        }

    suspend fun getLastResultForExercise(exerciseId: String): SessionExerciseResult? =
        withContext(Dispatchers.IO) {
            db.workoutSessionQueries
                .getLastResultForExercise(exerciseId)
                .executeAsOneOrNull()
                ?.toDomain()
        }

    suspend fun updateSessionStatus(sessionId: Long, status: SessionStatus, finishedAt: Long): Unit =
        withContext(Dispatchers.IO) {
            db.workoutSessionQueries.updateSessionStatus(
                id = sessionId,
                status = status.dbValue,
                finishedAt = finishedAt,
            )
        }

    suspend fun updateSessionNotes(sessionId: Long, notes: String): Unit =
        withContext(Dispatchers.IO) {
            db.workoutSessionQueries.updateSessionNotes(id = sessionId, notes = notes)
        }

    suspend fun skipExercise(sessionExercisePlanId: Long): Unit =
        withContext(Dispatchers.IO) {
            db.workoutSessionQueries.skipExercise(sessionExercisePlanId)
        }

    suspend fun reorderSessionExercises(orderedIds: List<Long>): Unit =
        withContext(Dispatchers.IO) {
            db.transaction {
                orderedIds.forEachIndexed { index, id ->
                    db.workoutSessionQueries.reorderSessionExercises(
                        position = index.toLong(),
                        id = id,
                    )
                }
            }
        }

    // ── Mapping ──────────────────────────────────────────────────────────────

    private fun WorkoutSessionEntity.toDomain() = WorkoutSession(
        id = id,
        planId = planId,
        planNameSnapshot = planNameSnapshot,
        workoutType = WorkoutType.fromDb(workoutType),
        status = SessionStatus.fromDb(status),
        rounds = rounds?.toInt(),
        startedAt = startedAt,
        finishedAt = finishedAt,
        notes = notes,
    )

    private fun SessionExercisePlanEntity.toDomain() = SessionExercisePlan(
        id = id,
        sessionId = sessionId,
        exerciseId = exerciseId,
        position = position.toInt(),
        sets = sets?.toInt(),
        pauseAfterSetSeconds = pauseAfterSetSeconds?.toInt(),
        pauseAfterExerciseSeconds = pauseAfterExerciseSeconds?.toInt(),
        skipped = skipped == 1L,
    )

    private fun SessionExerciseResultEntity.toDomain() = SessionExerciseResult(
        id = id,
        sessionId = sessionId,
        sessionExercisePlanId = sessionExercisePlanId,
        setIndex = setIndex.toInt(),
        types = types,
        valuesInt = valuesInt,
        valuesSec = valuesSec,
        units = units,
        completed = completed == 1L,
        startedAt = startedAt,
        completedAt = completedAt,
    )
}