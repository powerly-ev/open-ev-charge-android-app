package com.powerly.core.model.payment

import com.powerly.core.model.api.BaseResponse
import com.google.gson.annotations.SerializedName

class WithdrawResponse : BaseResponse<Any>()

class WalletsResponse : BaseResponse<List<Wallet>>()
data class Wallet(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("balance") val balance: Double,
    @SerializedName("currency") var currency: String = "",
    @SerializedName("type") val type: String = "",
    @SerializedName("payable") val payable: Boolean = false,
    @SerializedName("rechargeable") val rechargeable: Boolean = false,
    @SerializedName("withdrawable") val withdrawable: Boolean = false
)