package com.powerly.core.domain.model

import com.powerly.core.domain.model.user.User
import com.powerly.core.domain.model.Message

sealed class AuthStatus {
    data class Error(val msg: Message) : AuthStatus()
    data class Success(val user: User) : AuthStatus()
    data object RefreshToken : AuthStatus()
    data object Loading : AuthStatus()
}
