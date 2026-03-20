package org.ziskadev.stronger.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.ziskadev.stronger.domain.model.UserProfile
import kotlin.time.Clock
import org.ziskadev.stronger.data.local.UserProfile as UserProfileEntity

/**
 * Local data source for user profile via SQLDelight.
 */
class UserProfileLocalDataSource(private val db: StrongerDatabase) {

    fun getProfile(): Flow<UserProfile?> =
        db.userProfileQueries
            .getProfile()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toDomain() }

    suspend fun insertProfile(name: String?): Unit =
        withContext(Dispatchers.IO) {
            db.userProfileQueries.insertProfile(
                name = name,
                createdAt = Clock.System.now().toEpochMilliseconds(),
            )
        }

    suspend fun updateName(id: Long, name: String?): Unit =
        withContext(Dispatchers.IO) {
            db.userProfileQueries.updateName(name = name, id = id)
        }

    suspend fun deleteProfile(id: Long): Unit =
        withContext(Dispatchers.IO) {
            db.userProfileQueries.deleteProfile(id)
        }

    private fun UserProfileEntity.toDomain() = UserProfile(
        id = id,
        name = name,
        createdAt = createdAt,
    )
}