package com.powerly.powersource.charging.timer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

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
