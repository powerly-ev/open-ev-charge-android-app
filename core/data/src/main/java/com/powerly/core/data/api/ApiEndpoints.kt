package com.powerly.core.data.api

/**
 * Central registry of every backend endpoint used across the app.
 *
 * All endpoints live in this single object so that targeting a different
 * backend (e.g. for a sibling app) is a matter of editing/replacing this one
 * file rather than hunting through each feature module.
 *
 * Convention: plain endpoints are public `const val` strings; templated
 * endpoints keep their raw string `private` and expose a function that fills
 * the path params, so call sites never deal with `{...}` placeholders.
 */
object ApiEndpoints {

    // ===== App =====
    const val COUNTRIES = "countries"
    const val CURRENCIES = "countries/currencies"
    const val DEVICE = "device"
    private const val COUNTRY = "countries/{id}"
    fun country(id: Int): String = COUNTRY.replace("{id}", id.toString())

    // ===== User =====
    const val ME = "users/me"
    const val TOKEN_REFRESH = "auth/token/refresh"
    const val LOGOUT = "auth/logout"

    // ===== Auth =====
    const val AUTH_PASSWORD_FORGET = "auth/password/forgot"
    const val AUTH_PASSWORD_RESET = "auth/password/reset"
    const val AUTH_PASSWORD_RESET_RESEND = "auth/password/reset/resend"
    const val AUTH_EMAIL_CHECK = "auth/email/check"
    const val AUTH_EMAIL_LOGIN = "auth/login"
    const val AUTH_EMAIL_REGISTER = "auth/register"
    const val AUTH_EMAIL_VERIFY = "auth/email/verify"
    const val AUTH_EMAIL_VERIFY_RESEND = "auth/resend-verification"

    // ===== Power sources / Stations =====
    const val SOURCES = "stations"
    private const val SOURCE = "stations/{id}"
    fun source(id: String): String = SOURCE.replace("{id}", id)
    private const val MEDIA = "stations/{id}/media"
    fun media(id: String): String = MEDIA.replace("{id}", id)
    private const val REVIEWS = "stations/{id}/reviews"
    fun reviews(id: String): String = REVIEWS.replace("{id}", id)
    private const val BY_IDENTIFIER = "stations/token/{identifier}"
    fun byIdentifier(identifier: String): String = BY_IDENTIFIER.replace("{identifier}", identifier)

    // ===== Charging =====
    const val CHARGING_ORDERS = "orders"
    const val CHARGING_STOP = "orders/stop"
    private const val REVIEW_ADD = "orders/{order_id}/review"
    private const val REVIEW_SKIP = "orders/{order_id}/review/skip"
    fun reviewAdd(orderId: String): String = REVIEW_ADD.replace("{order_id}", orderId)
    fun reviewSkip(orderId: String): String = REVIEW_SKIP.replace("{order_id}", orderId)

    const val REVIEW_OPTIONS = "reviews/options"

    // ===== Sessions/Orders =====
    const val ORDERS = "orders"
    const val ORDERS_STOP = "orders/stop"
    private const val ORDER_DETAILS = "orders/{orderId}"
    fun orderDetails(orderId: String): String = ORDER_DETAILS.replace("{orderId}", orderId)

    // ===== Payment =====
    const val BALANCE_OFFERS = "balance/offers"
    const val BALANCE_REFILL = "balance/top-up"
    const val PAYOUTS = "payouts"
    const val PAYOUTS_REQUEST = "payouts/request-payout"
    const val PAYMENT_CARDS = "payment-cards"
    private const val PAYMENT_CARD_DEFAULT = "payment-cards/default/{id}"
    private const val PAYMENT_CARD_DELETE = "payment-cards/{id}"
    fun paymentCardDefault(id: String): String = PAYMENT_CARD_DEFAULT.replace("{id}", id)
    fun paymentCardDelete(id: String): String = PAYMENT_CARD_DELETE.replace("{id}", id)

    // ===== Vehicles =====
    const val VEHICLES = "vehicles"
    const val VEHICLE_MAKES = "vehicles/makes"
    private const val VEHICLES_ACTION = "vehicles/{id}"
    private const val VEHICLE_MODELS = "vehicles/models/{make_id}"
    const val CONNECTORS = "vehicles/connectors"
    fun vehicleAction(id: Int): String = VEHICLES_ACTION.replace("{id}", id.toString())
    fun vehicleModels(makeId: Int): String = VEHICLE_MODELS.replace("{make_id}", makeId.toString())
}
