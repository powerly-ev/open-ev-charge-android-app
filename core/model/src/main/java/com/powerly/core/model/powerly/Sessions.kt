package com.powerly.core.model.powerly

import androidx.annotation.StringRes
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


@Serializable
data class Session(
    @SerialName("id") val id: String = "",
    @SerialName("connector_number") val connectorNumber: Int? = null,
    @SerialName("status") var status: Int = 0,
    @SerialName("price") val price: Double = 0.0,
    @SerialName("prices") val prices: List<Price>? = null,
    @SerialName("unit_price") val unitPrice: Double = 0.0,
    @SerialName("app_fees") val appFees: Double = 0.0,
    @SerialName("earning") val earning: Double = 0.0,
    @SerialName("fees") val fees: Double = 0.0,
    @SerialName("charging_session_time") val chargingSessionTime: Double = 0.0,
    @SerialName("charging_session_energy") val chargingSessionEnergy: Double = 0.0,
    @OptIn(ExperimentalSerializationApi::class)
    @JsonNames("price_unit", "unit") val priceUnit: String = "minutes",
    @SerialName("quantity") val quantity: String? = null,
    @SerialName("requested_quantity") val quantityRequested: String = "",
    @SerialName("created_at") private val createdAt: String = "",
    @SerialName("delivery_date") val deliveryAt: String? = null,
    @SerialName("reserved_at") private val reservedAt: String = "",
    @SerialName("charge_point") val chargePoint: PowerSource = PowerSource(),
    @SerialName("currency") var currency: String = ""
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

@Serializable
data class Price(
    @SerialName("id") val id: Int = 0,
    @SerialName("quantity") val quantity: Double = 0.0,
    @SerialName("price") val price: Double = 0.0,
    @SerialName("total") val total: Double = 0.0,
    @SerialName("endingTime") val endingTime: String = "",
    @SerialName("startingTime") val startingTime: String = "",
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

data class OrderTab(
    @StringRes val title: Int
) {
    companion object {
        const val ACTIVE = 0
        const val HISTORY = 1
    }
}
