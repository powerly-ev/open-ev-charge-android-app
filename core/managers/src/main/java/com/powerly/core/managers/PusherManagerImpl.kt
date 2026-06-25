package com.powerly.core.managers

import android.util.Log
import androidx.annotation.Keep
import com.powerly.core.data.model.powerly.SessionDto
import com.powerly.core.data.model.powerly.toDomain
import com.powerly.core.database.StorageManager
import com.powerly.core.domain.manager.PusherManager
import com.powerly.core.domain.model.powerly.Session
import com.powerly.core.network.DeviceHelper
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpChannelAuthorizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single(binds = [PusherManager::class])
internal class PusherManagerImpl(
    private val storageManager: StorageManager,
    private val deviceHelper: DeviceHelper
) : PusherManager {
    private var pusher: Pusher? = null

    companion object {
        private const val TAG = "PusherManager"
        private const val PORT = 443
        private const val CHANNEL_CHARGING = "private-orders"
        private const val EVENT_CHARGING_COMPLETED = "App\\Events\\ChargePointOrderCompleted"
        private const val EVENT_CHARGING = "App\\Events\\ChargePointConsumption"
    }

    private val subscriptions = mutableMapOf<String, Subscription>()

    override suspend fun initPusherManager() {
        Log.i(TAG, "initPusherManager")
        val userToken = storageManager.getUserToken()
        val host = deviceHelper.pusherHost
        val key = deviceHelper.pusherKey
        val auth = "https://$host/broadcasting/auth"
        Log.i(TAG, "channelAuthorizer $auth - token = $userToken")
        val channelAuthorizer = HttpChannelAuthorizer(auth).apply {
            setHeaders(mapOf("Authorization" to "Bearer $userToken"))
        }
        val options = PusherOptions()
            .setHost(host)
            .setWssPort(PORT)
            .setChannelAuthorizer(channelAuthorizer)

        pusher = Pusher(key, options)
    }

    override val isConnected: Boolean get() = pusher?.connection?.state == ConnectionState.CONNECTED

    private val connectionEvent = object : ConnectionEventListener {
        override fun onConnectionStateChange(change: ConnectionStateChange) {
            val state = change.currentState
            Log.i(TAG, "onConnectionStateChange = $state")
        }

        override fun onError(message: String?, code: String?, e: Exception?) {
            Log.e(TAG, "onError - $message - $code - ${e?.message}")
            e?.printStackTrace()
        }
    }

    override fun connect() {
        Log.i(TAG, "attempt to connected")
        pusher?.connect(connectionEvent, ConnectionState.ALL)
    }

    override fun disconnectIfIdle() {
        if (subscriptions.isEmpty()) {
            pusher?.disconnect()
            Log.i(TAG, "disconnectIfIdle")
        }
    }

    override fun disconnect() {
        pusher?.disconnect()
        subscriptions.clear()
        Log.i(TAG, "disconnect")
    }

///////////////////////////////////////////////////

    private val _sessionCompletion = MutableStateFlow<Session?>(null)
    override val sessionCompletionFlow = _sessionCompletion.asStateFlow()

    private val _sessionConsumption = MutableStateFlow<Session?>(null)
    override val sessionConsumptionFlow = _sessionConsumption.asStateFlow()

    override fun subscribeSessionCompletion(sessionId: String) {
        _sessionCompletion.tryEmit(null)
        subscribe(
            channelName = "$CHANNEL_CHARGING.$sessionId",
            eventName = EVENT_CHARGING_COMPLETED,
            onReceive = {
                val session: Session? = it.asSession()
                Log.i(TAG, "onReceiveSessionCompletion - ${session?.id}")
                _sessionCompletion.tryEmit(session)
                unsubscribeSessionConsumption(sessionId)
                unsubscribeSessionCompletion(sessionId)
                disconnectIfIdle()
            }
        )
    }

    override fun unsubscribeSessionCompletion(sessionId: String) {
        unbindEvent(
            eventName = EVENT_CHARGING_COMPLETED,
            channelName = "$CHANNEL_CHARGING.$sessionId"
        )
    }

///////////////////////////////////////////////////

    override fun subscribeConsumption(
        sessionId: String,
        onUpdate: (Session) -> Unit
    ) {
        subscribe(
            channelName = "$CHANNEL_CHARGING.$sessionId",
            eventName = EVENT_CHARGING,
            onReceive = {
                val session = it.asSession() ?: return@subscribe
                onUpdate(session)
            }
        )
    }

    override fun unsubscribeSessionConsumption(sessionId: String) {
        unbindEvent(
            channelName = "$CHANNEL_CHARGING.$sessionId",
            eventName = EVENT_CHARGING,
        )
    }

///////////////////////////////////////////

    private fun subscribe(
        channelName: String,
        eventName: String,
        onReceive: (PusherEvent) -> Unit,
        onSuccess: (() -> Unit)? = null
    ) {
        val pusher = pusher ?: return
        Log.i(TAG, "attempt-to-subscribe - $eventName - $channelName")

        val eventListener = object : PrivateChannelEventListener {
            override fun onEvent(event: PusherEvent?) {
                Log.v(TAG, "bind-onEvent - $event.")
                event?.let { onReceive(event) }
            }

            override fun onSubscriptionSucceeded(channelName: String?) {
                onSuccess?.invoke()
                Log.i(TAG, "onSubscriptionSucceeded: channel = $channelName - $eventName")
            }

            override fun onAuthenticationFailure(message: String?, e: Exception?) {
                val msg = message.orEmpty().ifEmpty { e?.message.orEmpty() }
                Log.e(TAG, "onAuthenticationFailure - $msg")
            }
        }

        try {
            if (subscriptions.contains(channelName)) {
                Log.v(TAG, "already subscribed to channel - $channelName")
                val subscription = subscriptions[channelName]!!
                if (subscription.events.containsKey(eventName)) {
                    Log.v(TAG, "already bound to $eventName")
                } else {
                    subscription.privateChannel.bind(eventName, eventListener)
                    subscriptions[channelName]?.events[eventName] = eventListener
                }
            } else {
                val channel = pusher.subscribePrivate(channelName, eventListener)
                channel.bind(eventName, eventListener)
                val subscription = Subscription(
                    privateChannel = channel,
                    events = mutableMapOf(eventName to eventListener)
                )
                subscriptions[channelName] = subscription
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, e.message.orEmpty())
        }
    }

    private fun unbindEvent(eventName: String, channelName: String) {
        if (subscriptions.isEmpty()) return
        Log.v(TAG, "subscriptions = $subscriptions")
        try {
            val subscription = subscriptions[channelName] ?: return
            val eventListener = subscription.events[eventName] ?: return
            subscription.privateChannel.unbind(eventName, eventListener)
            subscription.events.remove(eventName)
            Log.i(TAG, "unbindEvent $eventName")
            if (subscription.events.isEmpty()) {
                pusher?.unsubscribe(channelName)
                subscriptions.remove(channelName)
                Log.v(TAG, "unsubscribe $channelName")
            }

        } catch (e: Exception) {
            Log.e(TAG, "unbindEvent - ${e.message}")
            e.printStackTrace()
        }
    }

    override fun unsubscribeAll() {
        subscriptions.keys.forEach { channelName ->
            pusher?.unsubscribe(channelName)
        }
        subscriptions.clear()
    }
}

/**
 * Data
 */

private data class Subscription(
    val privateChannel: PrivateChannel,
    val events: MutableMap<String, PrivateChannelEventListener>
)

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

@Keep
@Serializable
private data class SessionData(
    @SerialName("order") val session: SessionDto
)

private fun PusherEvent.asSession(): Session? = try {
    print(this.data)
    val data = json.decodeFromString<SessionData>(this.data)
    data.session.toDomain()
} catch (e: Exception) {
    e.printStackTrace()
    null
}
