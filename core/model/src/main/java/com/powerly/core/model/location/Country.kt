package com.powerly.core.model.location

import com.powerly.core.model.api.BaseResponse
import com.google.gson.annotations.SerializedName


class CountriesResponse : BaseResponse<List<Country>>()
class CountryResponse : BaseResponse<Country>()

data class Country(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String = "",
    @SerializedName("iso") val iso: String = "",
    @SerializedName("image") val image: String? = ""
)

class CurrenciesResponse : BaseResponse<List<AppCurrency>>()

data class AppCurrency(
    @SerializedName("currency_iso") val iso: String,
    @SerializedName("id") val id: Int = 0,
)