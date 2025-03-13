package com.powerly.core.model.payment

import com.powerly.core.model.api.BaseResponse
import com.google.gson.annotations.SerializedName


class BalancesResponse : BaseResponse<List<BalanceItem>>()

data class BalanceItem(
    @SerializedName("id") val id: Int = -1,
    @SerializedName("price") val price: Double = 0.0,
    @SerializedName("bonus") val bonus: Double = 0.0,
    @SerializedName("popular") val popular: Boolean = false
) {
    var currency: String = ""
    val totalBalance: Double get() = price + bonus
    val vat = 0.0
    val active: Boolean get() = true
}

class BalanceRefillBody(
    @SerializedName("offer_id") private val offerId: Int,
    @SerializedName("payment_method_id") private val paymentMethodId: String
)

class BalanceRefillResponse : BaseResponse<BalanceRefill>()

data class BalanceRefill(
    @SerializedName("new_balance") val newBalance: Double,
    @SerializedName("next_action") private val nextAction: NextAction?
) {
    val hasRedirectUrl: Boolean get() = nextAction != null
    val redirectUrl: PaymentRedirect get() = nextAction!!.redirectToUrl
}

data class NextAction(
    @SerializedName("redirect_to_url") val redirectToUrl: PaymentRedirect,
    @SerializedName("type") val type: String
)

data class PaymentRedirect(
    @SerializedName("return_url") val returnUrl: String,
    @SerializedName("url") val url: String
)

