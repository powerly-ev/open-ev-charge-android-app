package com.powerly.powersource.charging.timer

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

    fun initTimer(
        isFull: Boolean,
        chargingTime: Int,
        timeShift: Int,
        interval: Int = 5,
        onUpdate: (Int, Int) -> Unit,
        onDone: () -> Unit
    ) {
        Log.i(TAG, "time = $chargingTime s , elapsed = $timeShift s")

        if (timer != null) timer?.cancel()
        timer = Timer()

        fun onFinish() {
            onDone()
            timer?.cancel()
            timer = null
        }

        // For full charging, count up from elapsed; for timed charging, count down from remaining.
        var time = if (isFull) timeShift else chargingTime - timeShift
        // Snap to the nearest 5-second tick so ticks line up with server-side intervals.
        time = (time + 2) / 5 * 5

        val period = interval * 1000L

        if (time < 0 || time > chargingTime) {
            onFinish()
            return
        }

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

        val countUpTimerTask = object : TimerTask() {
            override fun run() {
                if (time <= chargingTime) {
                    val elapsedTime = time
                    onUpdate(elapsedTime, elapsedTime)
                    time += interval
                } else onFinish()
            }
        }

        val task = if (isFull) countUpTimerTask else countDownTimerTask
        timer?.schedule(task, 0, period)
    }

    companion object {
        private const val TAG = "ChargingTimerManager"
    }
}
