package com.powerly.core.data.model

import com.powerly.core.model.user.User
import com.powerly.core.model.util.Message

sealed class AuthStatus {
    data class Error(val msg: Message) : AuthStatus()
    data class Success(val user: User) : AuthStatus()
    data object RefreshToken : AuthStatus()
    data object Loading : AuthStatus()
}
