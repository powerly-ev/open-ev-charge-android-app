package com.powerly.user.data.model

import com.powerly.user.domain.model.EmailCheck
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class EmailCheckDto(
    @SerialName("email_exists") val emailExists: Int,
    @SerialName("require_verification") val requireVerification: Int,
)

internal fun EmailCheckDto.toDomain(): EmailCheck = EmailCheck(
    emailExists = emailExists,
    requireVerification = requireVerification
)
