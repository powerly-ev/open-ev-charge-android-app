package com.powerly.charging.util

import kotlinx.coroutines.delay


/**
 * Converts an integer representing seconds to a human-readable time string.
 *
 * This function takes an integer value representing a duration in seconds and returns a
 * formatted string representing the time in hours, minutes, or seconds. If the duration
 * is more than an hour, it returns the time in the format "xh" (e.g., "2h"). If the
 * duration is less than an hour but more than a minute, it returns the time in the
 * format "xm" (e.g., "35m"). Otherwise, it returns the time in seconds as "xs" (e.g., "59s").
 *
 * @return A formatted string representing the time in hours, minutes, or seconds.
 */
internal fun Int.asFormattedTime(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    return if (hours > 0) "${hours}h"
    else if (minutes > 0) "${minutes}m"
    else "${seconds}s"
}

/**
 * Executes a charging animation by incrementing progress and invoking callbacks.
 *
 * This function simulates a charging animation by gradually increasing the progress value
 * from 0 to 1 in 10 steps. For each step, it invokes the `onProgress` callback with the
 * current progress value and then delays for a specified duration. Upon reaching full
 * progress, it invokes the `onComplete` callback.
 *
 * @param onProgress A callback function that is invoked for each progress update,
 *                   receiving the current progress value (between 0 and 1).
 * @param onComplete A callback function that is invoked when the animation completes
 *                   (when progress reaches 1).
 */
internal suspend fun chargingAnimation(
    onProgress: (Float) -> Unit,
    onComplete: () -> Unit,
) {
    val progressDelay = 100L
    for (i in 0..10) {
        val progress = 0.1f * i
        onProgress(progress)
        delay(progressDelay)
    }
    onComplete()
}