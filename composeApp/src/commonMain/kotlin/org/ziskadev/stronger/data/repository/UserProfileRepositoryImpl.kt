package org.ziskadev.stronger.data.repository

import kotlinx.coroutines.flow.Flow
import org.ziskadev.stronger.data.local.UserProfileLocalDataSource
import org.ziskadev.stronger.domain.model.UserProfile
import org.ziskadev.stronger.domain.repository.UserProfileRepository

/**
 * Delegates all profile operations to the local data source.
 * No remote sync — profile is local only.
 */
class UserProfileRepositoryImpl(
    private val localDataSource: UserProfileLocalDataSource,
) : UserProfileRepository {

    override fun getProfile(): Flow<UserProfile?> =
        localDataSource.getProfile()

    override suspend fun insertProfile(name: String?) =
        localDataSource.insertProfile(name)

    override suspend fun updateName(id: Long, name: String?) =
        localDataSource.updateName(id, name)

    override suspend fun deleteProfile(id: Long) =
        localDataSource.deleteProfile(id)
}