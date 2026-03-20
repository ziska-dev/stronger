package org.ziskadev.stronger.domain.model

/**
 * Domain model for the user profile.
 * Optional — app works without a profile (guest mode).
 */
data class UserProfile(
    val id: Long,
    val name: String?,
    val createdAt: Long,
)