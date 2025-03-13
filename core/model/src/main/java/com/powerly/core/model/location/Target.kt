package com.powerly.core.model.location

import android.location.Location
import java.io.Serializable
import java.text.DecimalFormat

class Target(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) : Serializable {

    override fun toString(): String {
        val dm = DecimalFormat("0.########")
        return dm.format(latitude) + ", " + dm.format(longitude)
    }

    override fun equals(other: Any?): Boolean {
        if (other is Target) {
            return other.latitude == this.latitude && other.longitude == this.longitude
        }
        return super.equals(other)
    }

    fun toLocation(): Location {
        val location = Location("")
        location.latitude = latitude
        location.longitude = longitude
        return location
    }

    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        return result
    }
}
