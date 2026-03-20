package org.ziskadev.stronger.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ziskadev.stronger.domain.model.UserProfile

/**
 * Interface for user profile data access (domain layer).
 */
interface UserProfileRepository {

    /** Emits the current profile, or null if no profile exists (guest mode). */
    fun getProfile(): Flow<UserProfile?>

    /** Creates a new profile. Should only be called once. */
    suspend fun insertProfile(name: String?)

    /** Updates the display name of the existing profile. */
    suspend fun updateName(id: Long, name: String?)

    /** Deletes the profile — resets app to guest mode. */
    suspend fun deleteProfile(id: Long)
}