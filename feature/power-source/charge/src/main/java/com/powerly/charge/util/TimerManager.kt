package com.powerly.charge.util

import android.util.Log
import org.koin.core.annotation.Single
import java.util.Timer
import java.util.TimerTask


@Single
class ChargingTimerManager() {

    private var timer: Timer? = null

    fun release() {
        timer?.cancel()
        timer = null
    }

    /**
     * Initializes and starts a charging timer.
     *
     * This function manages a timer that tracks the progress of a charging session. It handles
     * both full charging (where the timer counts up) and timed charging (where the timer counts
     * down). The timer updates at a specified interval and invokes callbacks to provide progress
     * information.
     *
     * @param isFull Indicates whether the charging session is a full charge (true) or a timed charge (false).
     * @param chargingTime The total charging time in seconds (for timed charging).
     * @param timeShift The elapsed time in seconds since the charging session started.
     * @param interval The update interval for the timer in seconds. Defaults to 5 seconds.
     * @param onUpdate A callback function invoked at each timer update, providing the elapsed and remaining time in seconds.
     * @param onDone A callback function invoked when the timer completes (either when the time runs out or is canceled).
     */
    fun initTimer(
        isFull: Boolean,
        chargingTime: Int,
        timeShift: Int,
        interval: Int = 5,
        onUpdate: (Int, Int) -> Unit,
        onDone: () -> Unit
    ) {
        Log.i(TAG, "time = $chargingTime s , elapsed = $timeShift s")

        // Reset the timer if it's already running
        if (timer != null) timer?.cancel()
        timer = Timer()

        /**
         * Handles the completion of the timer, invoking the onDone callback and resetting the timer.
         */
        fun onFinish() {
            onDone()
            timer?.cancel()
            timer = null
        }

        // Calculate the initial time value based on charging type
        // For full charging, use the elapsed time since the session started
        var time = if (isFull) timeShift
        // For timed charging, calculate the remaining time
        else chargingTime - timeShift
        // Adjust the time to the nearest 5-second interval
        time = (time + 2) / 5 * 5

        // Set the timer period (interval in milliseconds)
        val period = interval * 1000L

        // Handle invalid time values
        if (time < 0 || time > chargingTime) {
            onFinish()
            return
        }

        /**
         * Timer task for counting down the remaining time (used for timed charging).
         */
        val countDownTimerTask = object : TimerTask() {
            override fun run() {
                if (time >= 0) {
                    val elapsedTime = chargingTime - time
                    val remainingTime = time
                    onUpdate(elapsedTime, remainingTime)
                    time -= interval
                } else onFinish()
            }
        }

        /**
         * Timer task for counting up the elapsed time (used for full charging).
         */
        val countUpTimerTask = object : TimerTask() {
            override fun run() {
                if (time <= chargingTime) {
                    val elapsedTime = time
                    onUpdate(elapsedTime, elapsedTime)
                    time += interval
                } else onFinish()
            }
        }

        // Select the appropriate timer task based on charging type
        val task = if (isFull) countUpTimerTask else countDownTimerTask
        // Schedule the timer task
        timer?.schedule(task, 0, period)
    }

    companion object {
        private const val TAG = "ChargingTimerManager"
    }
}