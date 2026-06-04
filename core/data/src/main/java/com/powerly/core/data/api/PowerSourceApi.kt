package com.powerly.core.data.api

internal object PowerSourceApi {
    const val SOURCES = "stations"
    const val SOURCE = "stations/{id}"
    const val MEDIA = "stations/{id}/media"
    const val REVIEWS = "stations/{id}/reviews"
    const val BY_IDENTIFIER = "stations/token/{identifier}"
    const val CONNECTORS = "vehicles/connectors"
}
