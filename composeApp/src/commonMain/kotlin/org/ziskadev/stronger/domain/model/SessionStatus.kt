package org.ziskadev.stronger.domain.model

/**
 * Status of a workout session.
 * Stored as string in DB; mapped via name property.
 */
sealed class SessionStatus {
    object Running : SessionStatus()
    object Completed : SessionStatus()
    object Aborted : SessionStatus()

    val dbValue: String get() = when (this) {
        is Running   -> "running"
        is Completed -> "completed"
        is Aborted   -> "aborted"
    }

    companion object {
        fun fromDb(value: String): SessionStatus = when (value) {
            "running"   -> Running
            "completed" -> Completed
            "aborted"   -> Aborted
            else        -> throw IllegalArgumentException("Unknown session status: $value")
        }
    }
}