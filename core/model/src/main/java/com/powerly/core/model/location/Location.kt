package com.powerly.core.model.location

import com.google.gson.annotations.SerializedName

data class UserLocation(
    @SerializedName("country") val country: Country? = null,
    @SerializedName("city") val city: City? = null,
    @SerializedName("area") val area: Area? = null,
    @SerializedName("address") val address: Any? = null,
    @SerializedName("latitude") val latitude: Double = 0.0,
    @SerializedName("longitude") val longitude: Double = 0.0,
) {
    fun getAreaAddress(): String {
        val areaName = area?.name.orEmpty()
        val cityName = city?.name.orEmpty()
        return areaName + (if (areaName.isNotEmpty()) ", " else "") + cityName
    }
}

data class Area(
    @SerializedName("id", alternate = ["area_id"]) val areaId: Int,
    @SerializedName("name") val name: String
)

data class City(
    @SerializedName("id", alternate = ["city_id"]) val cityId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("country_id") val countryId: Int
)

