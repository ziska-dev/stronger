package org.ziskadev.stronger.domain.util

/**
 * Resolves the effective pause duration based on a 3-level settings hierarchy:
 * Exercise-Override --> Plan-Override --> App-Default
 * NULL means: inherit from the level above
 */
fun resolveEffectivePause(
    exercisePause: Int?,
    planPause: Int?,
    appDefault: Int,
): Int = exercisePause ?: planPause ?: appDefault