package com.powerly.user.email.password.reset

internal sealed class EmailResetEvents {
    data object Edit : EmailResetEvents()
    data class Next(val pin: String, val password: String) : EmailResetEvents()
}