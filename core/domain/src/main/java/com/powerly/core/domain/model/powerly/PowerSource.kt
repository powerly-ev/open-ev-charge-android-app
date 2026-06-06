package com.powerly.core.domain.model.powerly

import com.powerly.core.domain.model.location.MyAddress
import com.powerly.core.domain.model.location.Target
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class PowerSource(
    val id: String = "",
    val identifier: String = "",
    val token: String? = null,
    val category: String = "",
    var title: String = "",
    val description: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val image: String? = null,
    val sessionLimitType: String? = null,
    val sessionLimit: Int? = 0,
    val contactNumber: String? = null,
    private val _openTime: String? = null,
    private val _closeTime: String? = null,
    val sourceType: SourceType? = null,
    val price: Double = 0.0,
    val priceUnit: String? = null,
    val rating: Double = 0.0,
    private val onlineStatus: Int = 0,
    val inUse: Boolean = false,
    val reserved: Boolean = false,
    val isInUseByYou: Boolean = false,
    val isReservedByYou: Boolean = false,
    val listed: Boolean = true,
    val connectors: List<Connector>? = null,
    val amenities: List<Amenity>? = null,
    val address: MyAddress? = null,
    val isExternal: Boolean = false,
    val media: List<Media> = listOf(),
    private var distance: Double? = null,
    var currency: String = ""
) {
    companion object {
        const val UNIT_TIME = "minutes"
        const val UNIT_KHW = "energy"
    }

    val isTimedPrice: Boolean get() = priceUnit == UNIT_TIME
    val location: Target get() = Target(latitude ?: 0.0, longitude ?: 0.0)
    val maxConnector: Connector? get() = connectors.orEmpty().maxByOrNull { it.maxPower }
    val isEvCharger: Boolean get() = category == SourceCategory.EV_CHARGER.name
    val hasConnectors: Boolean get() = isEvCharger && connectors.isNullOrEmpty().not()

    fun distance(): Double = distance ?: distance(latitude, longitude)

    fun distance(latitude: Double?, longitude: Double?): Double {
        val r = 6371.0
        val lat1 = Math.toRadians(latitude ?: 0.0)
        val lon1 = Math.toRadians(longitude ?: 0.0)
        val lat2 = Math.toRadians(this.latitude ?: 0.0)
        val lon2 = Math.toRadians(this.longitude ?: 0.0)

        val diffLon = lon2 - lon1
        val diffLat = lat2 - lat1

        val a = sin(diffLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(diffLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        distance = String.format(Locale.US, "%.2f", (r * c)).toDoubleOrNull()
        return distance!!.toDouble()
    }

    val isNear: Boolean get() = distance() <= 0.1

    val isOffline: Boolean get() = onlineStatus == 0

    val isAvailable: Boolean get() = isOffline.not() && inUse.not() && reserved.not()

    val shareLink: String get() = "https://powerly.app/charging-point?cp_id=$identifier"

    /**
     * Time properties
     */

    val openTime: String? get() = timeTo12h(_openTime)

    val closeTime: String? get() = timeTo12h(_closeTime)

    val open24h7: Boolean get() = _openTime == "00:00:00" && _closeTime == "23:59:00"


    val isOpen: Boolean
        get() {
            if (_openTime.isNullOrEmpty() || _closeTime.isNullOrEmpty()) return false
            else if (open24h7) return true

            val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
            val current = sdf.format(Date())
            val currentTime = sdf.parse(current) ?: return false
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val openingTime = sdf.parse(_openTime) ?: return false
            val closingTime = sdf.parse(_closeTime) ?: return false
            val case1 = openingTime < closingTime &&
                    currentTime.after(openingTime) && currentTime.before(closingTime)
            val case2 = openingTime > closingTime && currentTime.before(closingTime)

            return case1 || case2
        }

    private fun timeTo12h(time: String?): String? {
        if (time.isNullOrEmpty()) return null
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")

        val outputFormat = SimpleDateFormat("hh:mm", Locale.US)
        val outputFormat2 = SimpleDateFormat("a", Locale.getDefault())

        val timeZone = if (open24h7) TimeZone.getTimeZone("UTC") else TimeZone.getDefault()
        outputFormat.timeZone = timeZone
        outputFormat2.timeZone = timeZone

        val inputDate = inputFormat.parse(time) ?: return null
        return outputFormat.format(inputDate) + " " + outputFormat2.format(inputDate)
    }

    /**
     * Charging time slots
     */
    fun chargingTimes(includeTest: Boolean = false): List<ChargingQuantity> =
        initChargingTimes(includeTest)

    private var times: List<ChargingQuantity>? = null

    private fun initChargingTimes(includeTest: Boolean): List<ChargingQuantity> {
        if (times.isNullOrEmpty()) {
            val limit = sessionLimit ?: 0
            times = if (limit > 0 && isTimedPrice) generateChargingTimes(limit)
            else generateChargingTimes(includeTest)
        }
        return times.orEmpty()
    }

    private fun generateChargingTimes(includeTest: Boolean): List<ChargingQuantity> {
        val times = mutableListOf<ChargingQuantity>()
        times.addAll(
            listOf(
                ChargingQuantity(full = true, minutes = -1),
                ChargingQuantity(minutes = 10),
                ChargingQuantity(minutes = 15)
            )
        )
        if (includeTest) times.add(1, ChargingQuantity(minutes = 1))
        times.addAll((1..24).map { ChargingQuantity(minutes = 30 * it) })
        return times
    }

    private fun generateChargingTimes(limit: Int): List<ChargingQuantity> {
        val times = mutableListOf<ChargingQuantity>()
        if (limit <= 60) {
            val slots = limit / 10
            if (slots > 0) times.addAll((1..slots).map { ChargingQuantity(minutes = 10 * it) })
        } else {
            times.addAll(listOf(ChargingQuantity(minutes = 10), ChargingQuantity(minutes = 15)))
            times.addAll((1..(limit / 30)).map { ChargingQuantity(minutes = 30 * it) })
        }
        if (times.none { it.minutes >= limit }) times.add(ChargingQuantity(minutes = limit))

        return times
    }

}

data class ChargingQuantity(
    val minutes: Int = -1,
    private val full: Boolean = false,
) {
    val isFull: Boolean get() = minutes == -1 || full
    val toQuantity: String get() = if (isFull) "FULL" else minutes.toString()

    fun toTime(per: String = "", minute: String = "m", hour: String = "h"): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60

        val time = when {
            hours > 0 && remainingMinutes > 0 -> "$hours$hour $remainingMinutes$minute"
            hours > 0 -> "$hours$hour"
            remainingMinutes > 0 -> "$remainingMinutes$minute"
            else -> "0$minute"
        }
        return "$per $time".trim()
    }
}


data class Connector(
    val id: Int = 0,
    val name: String = "",
    var number: Int = 1,
    val icon: String = "",
    private val status: ConnectorStatus = ConnectorStatus.Available,
    val statusLabel: String = status.name,
    val type: String? = "",
    val maxPower: Double = 0.0,
) {
    val isAvailable: Boolean get() = status == ConnectorStatus.Available
    val booked: Boolean get() = status == ConnectorStatus.Booked
    val bookedByYou: Boolean get() = status == ConnectorStatus.BookedByYou
    val busy: Boolean get() = status == ConnectorStatus.Busy
    val busyByYou: Boolean get() = status == ConnectorStatus.BusyByYou
}

/**
 * Shared enum used as both DTO field and entity field. Pure value type — no behavior —
 * so the @SerialName mappings don't bleed presentation concerns into domain.
 */
@Serializable
enum class ConnectorStatus {
    @SerialName("available")
    Available,

    @SerialName("busy")
    Busy,

    @SerialName("busy-by-you")
    BusyByYou,

    @SerialName("booked")
    Booked,

    @SerialName("booked-by-you")
    BookedByYou,

    @SerialName("unavailable")
    Unavailable
}

data class Pivot(
    val chargePointId: String,
    val connectorId: String,
    val orderId: String? = null
)

data class Amenity(
    val id: Int = -1,
    val name: String = "",
    val icon: String? = "",
    val description: String? = "",
    val pivot: Pivot? = null
)

data class SourceType(
    val id: Int,
    val name: String = "",
    val img: String = "",
    val currentType: String = "",
    val maxPower: Double = 0.0
) {
    companion object {
        const val AC = "AC"
        const val DC = "DC"
    }
}

enum class SourceCategory {
    EV_CHARGER,
    SMART_PLUG,
    SMART_METER
}
