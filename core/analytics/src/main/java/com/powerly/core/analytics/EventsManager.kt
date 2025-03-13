package com.powerly.core.analytics

interface EventsManager {
    fun log(event: String, key: String? = null, value: String? = null)
    fun updatePushToken(token: String)
}

data class UserIdentifier(
    val id: String,
    val name: String,
    val email: String
)
