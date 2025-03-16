package com.powerly.user.email.verify

internal sealed class VerificationEvents {
    data object Edit : VerificationEvents()
    data object ResendCode : VerificationEvents()
    data object Help : VerificationEvents()
    data class Next(val code: String) : VerificationEvents()
}