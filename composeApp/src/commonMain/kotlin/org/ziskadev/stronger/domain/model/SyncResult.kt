package org.ziskadev.stronger.domain.model

/**
 * Result of a manual exercise sync.
 * Added = exercises in DB after sync minus exercises before sync.
 */
sealed class SyncResult {
    data class Success(val newCount: Int) : SyncResult()
    data class Error(val message: String) : SyncResult()
}