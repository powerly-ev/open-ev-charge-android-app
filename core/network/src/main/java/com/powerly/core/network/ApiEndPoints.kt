package com.powerly.core.network

internal object ApiEndPoints {

    const val COUNTRIES = "countries"
    const val COUNTRY = "countries/{id}"
    const val COUNTRIES_CURRENCIES = "countries/currencies"
    const val DEVICE = "device"

    /**
     * Authentication & User
     */

    const val USER_ME = "users/me"

    const val AUTH_TOKEN_REFRESH = "auth/token/refresh"
    const val AUTH_CHECK = "auth/check"
    const val AUTH_GOOGLE = "auth/google"
    const val AUTH_HUAWEI = "auth/huawei"
    const val AUTH_LOGOUT = "auth/logout"

    /**
     * Powerly
     */

    const val POWER_SOURCE = "stations"
    const val POWER_SOURCE_ACTION = "stations/{id}"
    const val POWER_SOURCE_MEDIA = "stations/{id}/media"
    const val POWER_SOURCE_REVIEWS = "stations/{id}/reviews"
    const val POWER_SOURCE_TOKEN = "stations/token/{identifier}"

    const val POWER_SOURCE_ORDERS = "orders"
    const val POWER_SOURCE_CHARGING_STOP = "orders/stop"

    const val VEHICLE_CONNECTORS = "vehicles/connectors"

}