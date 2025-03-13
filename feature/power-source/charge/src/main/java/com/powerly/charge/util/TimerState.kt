package com.powerly.charge.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Creates and remembers a [ChargingTimerState] instance.
 *
 * This composable function provides a convenient way to create and remember a state object
 * for tracking the elapsed and remaining time in a charging session. The returned
 * [ChargingTimerState] instance can be used to observe and update the timer values.
 *
 * @param elapsedTime The initial value for the elapsed time (in seconds). Defaults to 0.
 * @param remainingTime The initial value for the remaining time (in seconds). Defaults to 0.
 * @return A [ChargingTimerState] instance that can be used to track the charging timer.
 */
@Composable
fun rememberChargingTimerState(
    elapsedTime: Int = 0,
    remainingTime: Int = 0
) = remember { ChargingTimerState(elapsedTime, remainingTime) }

class ChargingTimerState(
    elapsedTimeValue: Int,
    remainingTimeValue: Int,
) {
    var elapsedTime by mutableIntStateOf(elapsedTimeValue)
        private set
    var remainingTime by mutableIntStateOf(remainingTimeValue)
        private set

    fun update(elapsedTime: Int, remainingTime: Int) {
        this.elapsedTime = elapsedTime
        this.remainingTime = remainingTime
    }

    val elapsedMinutes: Double get() = elapsedTime / 60.0
}