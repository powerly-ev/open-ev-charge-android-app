package com.powerly.lib.managers

import android.util.Log
import androidx.annotation.Keep
import com.powerly.core.database.StorageManager
import com.powerly.core.model.powerly.Session
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

@Single
class PusherManager(
    private val storageManager: StorageManager,
    private val deviceHelper: DeviceHelper
) {
    private var pusher: Pusher? = null

    companion object {
        private const val TAG = "PusherManager"
        private const val PORT = 443
        private const val CHANNEL_CHARGING = "private-orders"
        private const val EVENT_CHARGING_COMPLETED = "App\\Events\\ChargePointOrderCompleted"
        private const val EVENT_CHARGING = "App\\Events\\ChargePointConsumption"
    }

    private val subscriptions = mutableMapOf<String, Subscription>()

    /**
     * Initializes the Pusher manager with the necessary configuration.
     */
    suspend fun initPusherManager() {
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

    /**
     * Indicates whether the Pusher connection is currently connected.
     */
    val isConnected: Boolean get() = pusher?.connection?.state == ConnectionState.CONNECTED

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

    /**
     * Connects to the Pusher server .
     */
    fun connect() {
        Log.i(TAG, "attempt to connected")
        pusher?.connect(connectionEvent, ConnectionState.ALL)
    }

    /**
     * Disconnects from the Pusher server if no subscriptions are active.
     */
    fun disconnectIfIdle() {
        if (subscriptions.isEmpty()) {
            pusher?.disconnect()
            Log.i(TAG, "disconnectIfIdle")
        }
    }

    /**
     * Disconnects from the Pusher server.
     */
    fun disconnect() {
        pusher?.disconnect()
        subscriptions.clear()
        Log.i(TAG, "disconnect")
    }

///////////////////////////////////////////////////


    private val _sessionCompletion = MutableStateFlow<Session?>(null)
    val sessionCompletionFlow = _sessionCompletion.asStateFlow()

    private val _sessionConsumption = MutableStateFlow<Session?>(null)
    val sessionConsumptionFlow = _sessionConsumption.asStateFlow()

    /*private val _sessionCompletion = MutableSharedFlow<Session?>(replay = 0)
    val sessionCompletionFlow = _sessionCompletion.asSharedFlow()*/

    /**
     * Subscribes to session completion events for the given session ID using a StateFlow.
     *
     * @param sessionId The ID of the session to subscribe to.
     */
    fun subscribeSessionCompletion(sessionId: String) {
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

    /**
     * Unsubscribes from session completion events for the given session ID.
     *
     * @param sessionId The ID of the session to unsubscribe from.
     */
    fun unsubscribeSessionCompletion(sessionId: String) {
        unbindEvent(
            eventName = EVENT_CHARGING_COMPLETED,
            channelName = "$CHANNEL_CHARGING.$sessionId"
        )
    }

///////////////////////////////////////////////////

    /**
     * Subscribes to consumption updates for a specific session.
     *
     * @param sessionId The ID of the session to subscribe to.
     * @param onUpdate A callback that will be invoked whenever a consumption update is received for the session.
     */
    fun subscribeConsumption(
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

    /**
     * Unsubscribes from consumption updates for a specific session.
     *
     * @param sessionId The ID of the session to unsubscribe from.
     * We unbind but don't make unsubscribe consumption,
     * cause it use the same channel of session completion.
     */
    fun unsubscribeSessionConsumption(sessionId: String) {
        unbindEvent(
            channelName = "$CHANNEL_CHARGING.$sessionId",
            eventName = EVENT_CHARGING,
        )
    }

///////////////////////////////////////////
    /**
     * Subscribes to a specific event on a private channel.
     *
     * @param channelName The name of the channel to subscribe to.
     * @param eventName The name of the event to listen for.
     * @param onReceive A callback to be invoked when the event is received.
     * @param onSuccess A callback to be invoked when the subscription succeeds.
     */
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


    /**
     * Unbinds the event listener from the specified channel.
     *
     * @param eventName The name of the event.
     * @param channelName The name of the channel.
     */
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

    /**
     * Unsubscribes from all currently subscribed channels.
     */
    fun unsubscribeAll() {
        subscriptions.keys.forEach { channelName ->
            pusher?.unsubscribe(channelName)
        }
        subscriptions.clear()
    }
}

/**
 * Data
 */

data class Subscription(
    val privateChannel: PrivateChannel,
    val events: MutableMap<String, PrivateChannelEventListener>
)

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

@Keep
@Serializable
data class SessionData(
    @SerialName("order") val session: Session
)

private fun PusherEvent.asSession(): Session? = try {
    print(this.data)
    val data = json.decodeFromString<SessionData>(this.data)
    data.session
} catch (e: Exception) {
    e.printStackTrace()
    null
}
