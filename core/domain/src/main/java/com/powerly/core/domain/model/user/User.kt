package com.powerly.core.domain.model.user

data class User(
    val id: Int = -1,
    val firstName: String = "",
    val lastName: String = "",
    val appCurrency: String? = null,
    var balance: Double = 0.0,
    val countryId: Int? = null,
    val email: String = "",
    val emailVerified: Int = 0,
    val vatId: String? = null,
    var accessToken: String? = null
) {
    val fullName: String get() = "$firstName $lastName"
    val currency: String get() = appCurrency.orEmpty()
}
