package com.powerly.core.network

internal object ApiEndPoints {

    const val COUNTRIES = "countries"
    const val COUNTRY = "countries/{id}"
    const val COUNTRIES_CURRENCIES = "countries/currencies"
    const val DEVICE = "device"

    const val PAYMENT_CARDS = "payment-cards"
    const val PAYMENT_CARD_DEFAULT = "payment-cards/default/{id}"
    const val PAYMENT_CARD_DELETE = "payment-cards/{id}"


    const val BALANCE_OFFERS = "balance/offers"
    const val BALANCE_REFILL = "balance/top-up"

    const val PAYOUTS = "payouts"
    const val PAYOUTS_REQUEST = "payouts/request-payout"

    /**
     * Authentication & User
     */

    const val USER_ME = "users/me"

    const val AUTH_TOKEN_REFRESH = "auth/token/refresh"
    const val AUTH_CHECK = "auth/check"
    const val AUTH_PASSWORD_FORGET = "auth/password/forgot"
    const val AUTH_PASSWORD_RESET = "auth/password/reset"
    const val AUTH_PASSWORD_RESET_RESEND = "auth/password/reset/resend"
    const val AUTH_PASSWORD_RESET_VERIFY = "auth/password/reset/verify"
    const val AUTH_EMAIL_CHECK = "auth/email/check"
    const val AUTH_EMAIL_LOGIN = "auth/login"
    const val AUTH_EMAIL_REGISTER = "auth/register"
    const val AUTH_EMAIL_VERIFY = "auth/email/verify"
    const val AUTH_EMAIL_VERIFY_RESEND = "auth/resend-verification"
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

    const val POWER_SOURCE_ORDER_DETAILS = "orders/{orderId}"
    const val POWER_SOURCE_ORDERS = "orders"
    const val POWER_SOURCE_CHARGING_STOP = "orders/stop"

    const val REVIEWS = "reviews"
    const val REVIEW_OPTIONS = "reviews/options"
    const val REVIEW_SKIP = "orders/{order_id}/review/skip"
    const val REVIEW_ADD = "orders/{order_id}/review"

    const val VEHICLES = "vehicles"
    const val VEHICLES_ACTION = "vehicles/{id}"
    const val VEHICLE_MAKES = "vehicles/makes"
    const val VEHICLE_MODELS = "vehicles/models/{make_id}"
    const val VEHICLE_CONNECTORS = "vehicles/connectors"

}