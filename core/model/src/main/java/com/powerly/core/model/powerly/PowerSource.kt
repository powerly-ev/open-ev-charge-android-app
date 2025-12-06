package com.powerly.core.model.powerly

import android.util.Log
import com.powerly.core.model.api.FlexibleBooleanSerializer
import com.powerly.core.model.location.MyAddress
import com.powerly.core.model.location.Target
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

@Serializable
data class PowerSource(
    @SerialName("id") val id: String = "",
    @SerialName("identifier") val identifier: String = "",
    @SerialName("token") val token: String? = null,
    @SerialName("category") val category: String = "",
    @SerialName("title") var title: String = "",
    @SerialName("description") val description: String? = null,
    @SerialName("latitude") val latitude: Double? = null,
    @SerialName("longitude") val longitude: Double? = null,
    @SerialName("image") val image: String? = null,
    @SerialName("session_limit_type") val sessionLimitType: String? = null,
    @SerialName("session_limit_value") val sessionLimit: Int? = 0,
    @SerialName("contact_number") val contactNumber: String? = null,
    @SerialName("open_time") private val _openTime: String? = null,
    @SerialName("close_time") private val _closeTime: String? = null,
    @SerialName("type") val sourceType: SourceType? = null,
    @SerialName("price") val price: Double = 0.0,
    @SerialName("price_unit") val priceUnit: String? = null,
    @SerialName("rating") val rating: Double = 0.0,
    @SerialName("online_status") private val onlineStatus: Int = 0,
    @SerialName("is_in_use")
    @Serializable(with = FlexibleBooleanSerializer::class)
    val inUse: Boolean = false,
    @SerialName("is_reserved")
    @Serializable(with = FlexibleBooleanSerializer::class)
    val reserved: Boolean = false,
    @SerialName("used_by_current_user")
    @Serializable(with = FlexibleBooleanSerializer::class)
    val isInUseByYou: Boolean = false,
    @SerialName("booked_by_current_user")
    @Serializable(with = FlexibleBooleanSerializer::class)
    val isReservedByYou: Boolean = false,
    @SerialName("listed")
    @Serializable(with = FlexibleBooleanSerializer::class)
    val listed: Boolean = true,
    @SerialName("connectors") val connectors: List<Connector>? = null,
    @SerialName("amenities") val amenities: List<Amenity>? = null,
    @SerialName("address") val address: MyAddress? = null,
    @SerialName("external") val isExternal: Boolean = false,
    @SerialName("media") val media: List<Media> = listOf(),
    @SerialName("distance") private var distance: Double? = null,
    @SerialName("currency") var currency: String = ""
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

    /**
     * Api-calculated distance between the power source location &
     * the passed latitude& longitude
     * power-source/{id}?latitude=lat&longitude=lng
     * @return distance in km
     */
    fun distance(): Double = distance ?: distance(latitude, longitude)

    /**
     * Calculate the distance between two points first:
     * @param latitude: user latitude
     * @param longitude: user longitude
     * second point is the power source latitude & longitude
     * @return distance in km
     */
    fun distance(latitude: Double?, longitude: Double?): Double {
        val r = 6371.0 // Earth's radius in kilometers
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
            // convert utc time to Date object with local time zone
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val openingTime = sdf.parse(_openTime) ?: return false
            val closingTime = sdf.parse(_closeTime) ?: return false
            Log.i("PowerSource", "open - $openingTime, close - $closingTime")
            Log.i("PowerSource", "current - $currentTime")
            val case1 = openingTime < closingTime &&
                    currentTime.after(openingTime) && currentTime.before(closingTime)
            val case2 = openingTime > closingTime && currentTime.before(closingTime)

            return case1 || case2
        }

    //convert time to 12h - local time zone
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

    /**
     *  generate charging & booking time slots
     */
    private fun initChargingTimes(includeTest: Boolean): List<ChargingQuantity> {
        //generate time slots once
        if (times.isNullOrEmpty()) {
            //if there is a limit for session
            val limit = sessionLimit ?: 0
            times = if (limit > 0 && isTimedPrice) generateChargingTimes(limit)
            // when there is no session limit
            else generateChargingTimes(includeTest)
        }
        return times.orEmpty()
    }

    /**
     * Generate all time slots FULL, 10,15,30,n+30....720
     */
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

    /**
     * Generate time slots regarding to session limit
     */

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

@Serializable
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
        //example-> per 1 hour 30 minutes
        return "$per $time".trim()
    }
}


@Serializable
data class Connector(
    @SerialName("id") val id: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("number") var number: Int = 1,
    @SerialName("icon") val icon: String = "",
    @SerialName("status") private val status: ConnectorStatus = ConnectorStatus.Available,
    @SerialName("localized_status") val statusLabel: String = status.name,
    @SerialName("type") val type: String? = "",
    @SerialName("max_power") val maxPower: Double = 0.0,
) {
    val isAvailable: Boolean get() = status == ConnectorStatus.Available
    val booked: Boolean get() = status == ConnectorStatus.Booked
    val bookedByYou: Boolean get() = status == ConnectorStatus.BookedByYou
    val busy: Boolean get() = status == ConnectorStatus.Busy
    val busyByYou: Boolean get() = status == ConnectorStatus.BusyByYou
}

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

@Serializable
data class Pivot(
    @SerialName("charge_point_id") val chargePointId: String,
    @SerialName("connector_id") val connectorId: String,
    @SerialName("order_id") val orderId: String? = null
)

@Serializable
data class Amenity(
    @SerialName("id") val id: Int = -1,
    @SerialName("name") val name: String = "",
    @SerialName("icon") val icon: String? = "",
    @SerialName("description") val description: String? = "",
    @SerialName("pivot") val pivot: Pivot? = null
)

@Serializable
data class SourceType(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String = "",
    @SerialName("img") val img: String = "",
    @SerialName("current_type") val currentType: String = "",
    @SerialName("max_power") val maxPower: Double = 0.0
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

