package com.powerly.core.data.model

import com.powerly.core.model.powerly.Session
import com.powerly.core.model.util.Message

sealed class SessionStatus {
    data class Error(val msg: Message) : SessionStatus()
    data class Success(val sessions: List<Session>) : SessionStatus()
    data object Loading : SessionStatus()
}