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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
        private val subscriptions = mutableMapOf<String, PrivateChannel>()
        private val events = mutableMapOf<String, PrivateChannelEventListener>()
        private const val CHANNEL_CHARGING = "private-orders"
        private const val EVENT_CHARGING_COMPLETED = "App\\Events\\ChargePointOrderCompleted"
        private const val EVENT_CHARGING = "App\\Events\\ChargePointConsumption"
    }

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

    /**
     * Connects to the Pusher server with optional error and status callbacks.
     *
     * @param onError A callback to be invoked when an error occurs during connection.
     * @param status A callback to be invoked when the connection state changes.
     */
    fun connect(
        onError: ((String) -> Unit)? = null,
        status: ((ConnectionState) -> Unit)? = null,
    ) {
        Log.i(TAG, "attempt to connected")
        pusher?.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                val state = change.currentState
                status?.invoke(state)
                Log.i(TAG, "onConnectionStateChange = $state")
            }

            override fun onError(message: String?, code: String?, e: Exception?) {
                onError?.invoke(message.orEmpty())
                Log.e(TAG, "onError - $message - $code - ${e?.message}")
                e?.printStackTrace()
            }
        }, ConnectionState.ALL)
    }

    /**
     * Connects to the Pusher server.
     */
    fun connect() {
        Log.i(TAG, "attempt to connected")
        pusher?.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                // Handle connection state changes if needed
                Log.v(TAG, "onConnectionStateChange - ${change.currentState.name}")
            }

            override fun onError(message: String?, code: String?, e: Exception?) {
                // Handle connection errors if needed
                Log.e(TAG, "$message - ${e?.message.orEmpty()}")
                e?.printStackTrace()
            }
        }, ConnectionState.CONNECTED)
    }


    /**
     * Disconnects from the Pusher server.
     */
    fun disconnect() {
        pusher?.disconnect()
        Log.i(TAG, "disconnect")
    }

    /**
     * Reconnects to the Pusher server.
     */
    fun reconnect() {
        pusher?.connect()
        Log.i(TAG, "reconnect")
    }

///////////////////////////////////////////////////


    private val _sessionCompletion = MutableStateFlow<Session?>(null)
    val sessionCompletionFlow = _sessionCompletion.asStateFlow()


    /*private val _sessionCompletion = MutableSharedFlow<Session?>(replay = 0)
    val sessionCompletionFlow = _sessionCompletion.asSharedFlow()*/

    /**
     * Subscribes to session completion events for the given session ID using a StateFlow.
     *
     * @param sessionId The ID of the session to subscribe to.
     */
    fun subscribeSessionCompletion(sessionId: String) {
        _sessionCompletion.tryEmit(null)
        subscribeEvent(
            channelName = "$CHANNEL_CHARGING.$sessionId",
            eventName = EVENT_CHARGING_COMPLETED,
            onReceive = {
                val session: Session? = it.asSession()
                Log.i(TAG, "onReceiveSessionCompletion - ${session?.id}")
                _sessionCompletion.tryEmit(session)
                unbindConsumption(sessionId)
                unsubscribeSession(sessionId)
                disconnect()
            }
        )
    }

    /**
     * Unsubscribes from session completion events for the given session ID.
     *
     * @param sessionId The ID of the session to unsubscribe from.
     */
    fun unsubscribeSession(sessionId: String) {
        val channelName = "$CHANNEL_CHARGING.$sessionId"
        val eventName = EVENT_CHARGING_COMPLETED
        unbindEvent(channelName, eventName)
        unsubscribeChannel(channelName)
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
        subscribeEvent(
            channelName = "$CHANNEL_CHARGING.$sessionId",
            eventName = EVENT_CHARGING,
            onReceive = {
                val session = it.asSession() ?: return@subscribeEvent
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
    fun unbindConsumption(sessionId: String) {
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
     * @param onError A callback to be invoked when an error occurs during subscription.
     */
    private fun subscribeEvent(
        channelName: String,
        eventName: String,
        onReceive: (PusherEvent) -> Unit,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        if (pusher == null) return
        Log.i(TAG, "attempt-to-subscribe - $eventName - $channelName")
        try {
            val channel = if (subscriptions.contains(channelName)) {
                subscriptions[channelName]!!
            } else {
                val newChannel: PrivateChannel =
                    pusher!!.subscribePrivate(channelName, object : PrivateChannelEventListener {
                        override fun onEvent(event: PusherEvent?) {}
                        override fun onSubscriptionSucceeded(channelName: String?) {
                            Log.v(TAG, "onSubscriptionSucceeded - $channelName")
                        }

                        override fun onAuthenticationFailure(message: String?, e: Exception?) {
                            val msg = message.orEmpty().ifEmpty { e?.message.orEmpty() }
                            onError?.invoke(msg)
                            Log.e(TAG, "onAuthenticationFailure - $msg")
                            e?.printStackTrace()
                        }

                    })
                subscriptions[channelName] = newChannel
                newChannel
            }

            if (events.contains(eventName)) {
                Log.w(TAG, "eventName - $eventName already bound")
                val eventListener = events[eventName]
                channel.bind(eventName, eventListener)
                return
            }

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
            channel.bind(eventName, eventListener)
            events[eventName] = eventListener
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, e.message.orEmpty())
        }
    }

    /**
     * Unbinds the event listener from the specified channel.
     *
     * @param channelName The name of the channel.
     * @param eventName The name of the event.
     */
    private fun unbindEvent(
        channelName: String,
        eventName: String
    ) {
        try {
            val privateChannel = subscriptions[channelName]
            val eventListener = events[eventName]
            if (privateChannel != null && eventListener != null) {
                privateChannel.unbind(eventName, eventListener)
                events.remove(eventName)
                Log.i(TAG, "unbindEvent $eventName")
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
        if (pusher == null) return
        subscriptions.forEach { (channelName, _) ->
            pusher!!.unsubscribe(channelName)
        }
        subscriptions.clear()
    }

    /**
     * Unsubscribes from the given channel.
     *
     * This method removes the channel from the list of subscriptions and unsubscribes from the channel
     * using the `pusher` object.
     *
     * @param channelName The name of the channel to unsubscribe from.
     */
    private fun unsubscribeChannel(channelName: String) {
        if (pusher == null) return
        if (subscriptions.contains(channelName)) {
            Log.v(TAG, "unsubscribeChannel - $channelName")
            pusher!!.unsubscribe(channelName)
            subscriptions.remove(channelName)
        }
    }
}

/**
 * Data
 */

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
