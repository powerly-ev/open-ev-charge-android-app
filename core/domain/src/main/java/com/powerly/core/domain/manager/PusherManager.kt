package com.powerly.core.domain.manager

import com.powerly.core.domain.model.powerly.Session
import kotlinx.coroutines.flow.StateFlow

/**
 * Realtime charging-session gateway (websocket). Exposes session consumption/completion
 * as flows and lets callers manage subscriptions; the transport (Pusher) is an
 * implementation detail.
 */
interface PusherManager {

    val isConnected: Boolean

    /** Emits the session when its charging completes. */
    val sessionCompletionFlow: StateFlow<Session?>

    /** Emits live consumption updates for the subscribed session. */
    val sessionConsumptionFlow: StateFlow<Session?>

    suspend fun initPusherManager()

    fun connect()
    fun disconnect()
    fun disconnectIfIdle()

    fun subscribeSessionCompletion(sessionId: String)
    fun unsubscribeSessionCompletion(sessionId: String)

    fun subscribeConsumption(sessionId: String, onUpdate: (Session) -> Unit)
    fun unsubscribeSessionConsumption(sessionId: String)

    fun unsubscribeAll()
}
