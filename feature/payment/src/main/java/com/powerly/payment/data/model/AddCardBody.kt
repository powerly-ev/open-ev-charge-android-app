package com.powerly.payment.data.model

import kotlinx.serialization.Serializable

@Serializable
internal data class AddCardBody(val token: String)
