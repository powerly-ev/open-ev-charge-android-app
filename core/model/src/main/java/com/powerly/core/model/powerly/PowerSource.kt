package com.powerly.core.model.powerly

import android.util.Log
import com.powerly.core.model.api.BaseResponse
import com.powerly.core.model.api.BaseResponsePaginated
import com.powerly.core.model.location.MyAddress
import com.powerly.core.model.location.Target
import com.google.gson.annotations.SerializedName
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
    @SerializedName("id") val id: String = "",
    @SerializedName("identifier") val identifier: String = "",
    @SerializedName("token") val token: String? = null,
    @SerializedName("category") val category: String = "",
    @SerializedName("title") var title: String = "",
    @SerializedName("description") val description: String? = null,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("session_limit_type") val sessionLimitType: String? = null,
    @SerializedName("session_limit_value") val sessionLimit: Int? = 0,
    @SerializedName("contact_number") val contactNumber: String? = null,
    @SerializedName("open_time") private val _openTime: String? = null,
    @SerializedName("close_time") private val _closeTime: String? = null,
    @SerializedName("type") val sourceType: SourceType? = null,
    @SerializedName("price") val price: Double = 0.0,
    @SerializedName("price_unit") val priceUnit: String? = null,
    @SerializedName("rating") val rating: Double = 0.0,
    @SerializedName("online_status") private val onlineStatus: Int = 0,
    @SerializedName("is_in_use") val inUse: Any = false,
    @SerializedName("is_reserved") private val reserved: Any = false,
    @SerializedName("used_by_current_user") val isInUseByYou: Boolean = false,
    @SerializedName("booked_by_current_user") val isReservedByYou: Boolean = false,
    @SerializedName("listed") private val listed: Any = true,
    @SerializedName("connectors") val connectors: List<Connector>? = null,
    @SerializedName("amenities") val amenities: List<Amenity>? = null,
    @SerializedName("address") val address: MyAddress? = null,
    @SerializedName("external") val isExternal: Boolean = false,
    @SerializedName("media") var media: List<Media> = listOf(),
    @SerializedName("distance") private var distance: Double? = null,
    @SerializedName("currency") var currency: String = ""
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

    val isListed: Boolean get() = listed == 1 || listed == true

    val isInUse: Boolean get() = inUse == 1 || inUse == true

    val isReserved: Boolean get() = reserved == 1 || reserved == true

    val isAvailable: Boolean get() = isOffline.not() && isInUse.not() && isReserved.not()

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


data class Connector(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("number") var number: Int = 1,
    @SerializedName("icon") val icon: String = "",
    @SerializedName("status") val status: String = "available",
    @SerializedName("type") val type: String? = "",
    @SerializedName("max_power") val maxPower: Double = 0.0,
) {
    val isAvailable: Boolean get() = status.equals("available", ignoreCase = true)
    val booked: Boolean get() = status.equals("booked", ignoreCase = true)
    val bookedByYou: Boolean get() = status.equals("booked by you", ignoreCase = true)
    val busy: Boolean get() = status.equals("busy", ignoreCase = true)
    val busyByYou: Boolean get() = status.equals("busy by you", ignoreCase = true)
}

data class Pivot(
    @SerializedName("charge_point_id") val chargePointId: String,
    @SerializedName("connector_id") val connectorId: String,
    @SerializedName("order_id") val orderId: String? = null
)

data class Amenity(
    @SerializedName("id") val id: Int = -1,
    @SerializedName("name") val name: String = "",
    @SerializedName("icon") val icon: String? = "",
    @SerializedName("description") val description: String? = "",
    @SerializedName("pivot") val pivot: Pivot? = null
)

data class SourceType(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String = "",
    @SerializedName("img") val img: String = "",
    @SerializedName("current_type") val currentType: String = "",
    @SerializedName("max_power") val maxPower: Double = 0.0
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


class PowerSourceResponse : BaseResponse<PowerSource?>()
class PowerSourcesResponse : BaseResponsePaginated<PowerSource>()

data class VisitedSources(
    @SerializedName("data") val data: List<PowerSource> = emptyList()
)