package com.powerly.powersource.details.presentation.source

import android.content.Context
import androidx.annotation.DrawableRes
import com.powerly.core.domain.model.powerly.PowerSource
import com.powerly.resources.R

internal class PowerSourceUptime(
    powerSource: PowerSource,
    context: Context
) {
    var hasTime: Boolean = false
    var workStatus: String = ""
    var workTime: String? = null
    var status: String = ""

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
            val isInUse = powerSource.inUse
            val isReserved = powerSource.reserved
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
