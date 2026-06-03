package com.powerly.powersource.charging.timer

import kotlinx.coroutines.delay

internal fun Int.asFormattedTime(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    return if (hours > 0) "${hours}h"
    else if (minutes > 0) "${minutes}m"
    else "${seconds}s"
}

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
