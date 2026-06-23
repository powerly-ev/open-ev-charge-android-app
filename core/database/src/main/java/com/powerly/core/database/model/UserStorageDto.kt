package com.powerly.core.database.model

import com.powerly.core.domain.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * On-disk representation of [User] for DataStore persistence. Kept separate from the
 * domain entity so storage's serialization concerns don't bleed into the model.
 */
@Serializable
internal data class UserStorageDto(
    @SerialName("id") val id: Int = -1,
    @SerialName("first_name") val firstName: String = "",
    @SerialName("last_name") val lastName: String = "",
    @SerialName("currency") val appCurrency: String? = null,
    @SerialName("balance") val balance: Double = 0.0,
    @SerialName("country_id") val countryId: Int? = null,
    @SerialName("email") val email: String = "",
    @SerialName("email_verified") val emailVerified: Int = 0,
    @SerialName("vat_id") val vatId: String? = null,
    @SerialName("access_token") val accessToken: String? = null
)

internal fun UserStorageDto.asUser(): User = User(
    id = id,
    firstName = firstName,
    lastName = lastName,
    appCurrency = appCurrency,
    balance = balance,
    countryId = countryId,
    email = email,
    emailVerified = emailVerified,
    vatId = vatId,
    accessToken = accessToken
)

internal fun User.asStorageDto(): UserStorageDto = UserStorageDto(
    id = id,
    firstName = firstName,
    lastName = lastName,
    appCurrency = appCurrency,
    balance = balance,
    countryId = countryId,
    email = email,
    emailVerified = emailVerified,
    vatId = vatId,
    accessToken = accessToken
)
