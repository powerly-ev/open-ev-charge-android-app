package com.powerly.powerSource.details

import android.content.Context
import androidx.annotation.DrawableRes
import com.powerly.core.model.powerly.PowerSource
import com.powerly.resources.R

/**
 * Data class representing the uptime and operational status of a power source.
 *
 * @param powerSource The [PowerSource] object providing the raw data.
 * @param context The Android context used for string resources.
 */
internal class PowerSourceUptime(
    powerSource: PowerSource,
    context: Context
) {
    /** Indicates whether the power source has operational time information. */
    var hasTime: Boolean = false

    /** The operational status of the power source (e.g., "Open", "Closed"). */
    var workStatus: String = ""

    /** The time the power source opens or closes, if applicable. */
    var workTime: String? = null

    /** The current status of the power source (e.g., "Busy", "Offline"). */
    var status: String = ""

    /** Drawable resource ID representing the status icon. */
    @DrawableRes
    var icon: Int? = null

    init {
        val openTime = powerSource.openTime
        val closeTime = powerSource.closeTime

        if (openTime.isNullOrEmpty().not() &&
            closeTime.isNullOrEmpty().not()
        ) {
            hasTime = true
            val isOpen = powerSource.isOpen
            val isOpen24h = powerSource.open24h7
            val isInUse = powerSource.isInUse
            val isReserved = powerSource.isReserved
            val isOffline = powerSource.isOffline
            val selfBooked = powerSource.isReservedByYou
            val selfCharging = powerSource.isInUseByYou


            workStatus = context.getString(
                if (isOpen24h) R.string.station_status_open24
                else if (isOpen) R.string.station_status_open
                else R.string.station_closed
            )

            workTime = if (isOpen24h) null
            else if (isOpen) context.getString(
                R.string.station_close_in,
                closeTime
            ) else context.getString(
                R.string.station_open_in,
                openTime
            )


            if (isOffline) {
                status = context.getString(R.string.station_unavailable)
                icon = R.drawable.station_offline
            } else if (selfCharging) {
                status = context.getString(R.string.station_status_self_charging)
                icon = R.drawable.station_busy
            } else if (selfBooked) {
                status = context.getString(R.string.station_status_self_booked)
                icon = R.drawable.station_busy
            } else if (isInUse) {
                status = context.getString(R.string.station_status_busy)
                icon = R.drawable.station_busy
            } else if (isReserved) {
                status = context.getString(R.string.station_status_booked)
                icon = R.drawable.station_busy
            } else {
                status = ""
            }
        }
    }
}
