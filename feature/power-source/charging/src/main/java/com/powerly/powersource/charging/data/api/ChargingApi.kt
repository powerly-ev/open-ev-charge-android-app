package com.powerly.powersource.charging.data.api

internal object ChargingApi {
    const val ORDERS = "orders"
    const val ORDER_DETAILS = "orders/{orderId}"
    const val CHARGING_STOP = "orders/stop"

    const val REVIEW_OPTIONS = "reviews/options"
    const val REVIEW_ADD = "orders/{order_id}/review"
    const val REVIEW_SKIP = "orders/{order_id}/review/skip"
}
