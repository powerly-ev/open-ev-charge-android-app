package com.powerly.core.domain.model.location

data class Country(
    val id: Int,
    val name: String = "",
    val iso: String = ""
)

data class AppCurrency(
    val iso: String,
    val id: Int = 0,
)
