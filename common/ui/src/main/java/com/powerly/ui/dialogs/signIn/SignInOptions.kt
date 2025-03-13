package com.powerly.ui.dialogs.signIn

sealed class SignInOptions {
    data object Email : SignInOptions()
    data object Guest : SignInOptions()

    companion object {
        fun option(title: String): SignInOptions = when (title) {
            "Email" -> Email
            else -> Guest
        }
    }
}