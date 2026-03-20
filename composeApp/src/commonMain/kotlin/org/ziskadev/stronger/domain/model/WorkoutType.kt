package org.ziskadev.stronger.domain.model

enum class WorkoutType {
    STRENGTH,
    CIRCUIT;

    val dbValue: String get() = name

    companion object {
        fun fromDb(value: String): WorkoutType = enumValueOf(value)
    }
}