package com.powerly.core.domain.model.powerly

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


data class Session(
    val id: String = "",
    val connectorNumber: Int? = null,
    var status: Int = 0,
    val price: Double = 0.0,
    val prices: List<Price>? = null,
    val unitPrice: Double = 0.0,
    val appFees: Double = 0.0,
    val earning: Double = 0.0,
    val fees: Double = 0.0,
    val chargingSessionTime: Double = 0.0,
    val chargingSessionEnergy: Double = 0.0,
    val priceUnit: String = "minutes",
    val quantity: String? = null,
    val quantityRequested: String = "",
    private val createdAt: String = "",
    val deliveryAt: String? = null,
    private val reservedAt: String = "",
    val chargePoint: PowerSource = PowerSource(),
    var currency: String = ""
) {

    val isActive: Boolean get() = status == 0
    val isScheduled: Boolean get() = status == 1
    val isDelivered: Boolean get() = status == 2
    val isTimedSession: Boolean get() = chargePoint.priceUnit == PowerSource.UNIT_TIME

    val chargePointId: String get() = chargePoint.id
    val chargePointName: String get() = chargePoint.title
    val chargePointAddress: String get() = chargePoint.address?.detailedAddress.orEmpty()
    val chargePointListed: Boolean get() = chargePoint.listed
    val sessionLimit: Int? get() = chargePoint.sessionLimit

    val isFull: Boolean
        get() = quantityRequested.equals("FULL", ignoreCase = true)
                || quantity.equals("FULL", ignoreCase = true)

    fun createdAt(format: String = "MMM dd h:mm a"): String =
        createdAt.toLocalTime(format)

    fun reservedAt(format: String = "MMM dd h:mm a"): String =
        reservedAt.toLocalTime(format)

    fun deliveredAt(format: String = "yyyy-MM-dd"): String =
        deliveryAt.orEmpty().toLocalTime(format)

    fun formatedQuantity(h: String, m: String, s: String): String {
        if (isTimedSession.not()) return quantity.orEmpty()
        val minutes: Double = quantity?.toDoubleOrNull() ?: return quantity.orEmpty()

        val totalSeconds: Int = (minutes * 60).toInt()
        val hours = totalSeconds / 3600
        val remainingMinutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        var time = ""
        if (hours > 0) time += "$hours $h"
        if (remainingMinutes > 0) time += " $remainingMinutes $m"
        if (seconds > 0) time += " $seconds $s"
        return time.trim()
    }

    /**
     * Real time Charging fees
     */

    // time in seconds since charging has started
    val elapsedTime: Int get() = (chargingSessionTime * 60).toInt()

    val displayTime: Int
        get() = if (isFull) elapsedTime
        else (quantityRequested.toIntOrNull() ?: 0) * 60

    private val Double.round get() = String.format(Locale.US, "%.2f", this).toDouble()

    /**
     * returns the charging price in app currency
     */

    fun chargingPrice(): Double {
        val amount = if (isTimedSession) chargingSessionTime else chargingSessionEnergy
        return (amount * chargePoint.price).round
    }

    /**
     * returns the charging time in seconds
     */
    fun chargingTime(): Int {
        return if (isFull) {
            val minutes = if (sessionLimit == 0 || sessionLimit == null) 5000
            else sessionLimit!!
            minutes * 60
        } else {
            (quantityRequested.toIntOrNull() ?: 0) * 60
        }
    }
}

data class Price(
    val id: Int = 0,
    val quantity: Double = 0.0,
    val price: Double = 0.0,
    val total: Double = 0.0,
    val endingTime: String = "",
    val startingTime: String = "",
)

private fun String.toLocalTime(format: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    val outputFormat = SimpleDateFormat(format, Locale.US).apply {
        timeZone = TimeZone.getDefault()
    }
    return try {
        val date = inputFormat.parse(this)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        this
    }
}
