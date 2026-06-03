package com.powerly.user.data.api

internal object UserAuthApi {
    const val AUTH_PASSWORD_FORGET = "auth/password/forgot"
    const val AUTH_PASSWORD_RESET = "auth/password/reset"
    const val AUTH_PASSWORD_RESET_RESEND = "auth/password/reset/resend"
    const val AUTH_EMAIL_CHECK = "auth/email/check"
    const val AUTH_EMAIL_LOGIN = "auth/login"
    const val AUTH_EMAIL_REGISTER = "auth/register"
    const val AUTH_EMAIL_VERIFY = "auth/email/verify"
    const val AUTH_EMAIL_VERIFY_RESEND = "auth/resend-verification"
}
