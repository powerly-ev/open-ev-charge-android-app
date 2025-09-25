package com.powerly.lib.managers

import android.content.Context
import android.util.Log
import com.powerly.core.analytics.EventsManager
import com.powerly.lib.CONSTANTS.NOTIFICATION_TYPE
import com.powerly.resources.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.text.orEmpty


class MessagingService : FirebaseMessagingService(), KoinComponent {
    private val eventsManager: EventsManager by inject()
    private val notificationsManager: NotificationsManager by inject()

    private val serviceScope = CoroutineScope(SupervisorJob())

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "onNewToken - $token")
        serviceScope.launch {
            notificationsManager.saveToken(token)
        }
        // Send new token to AppsFlyer
        eventsManager.updatePushToken(token)
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.v(TAG, Gson().toJson(remoteMessage))
        val body = remoteMessage.notification?.body.orEmpty()
        val title = remoteMessage.notification?.title ?: getString(R.string.app_name)
        val type = remoteMessage.data[NOTIFICATION_TYPE].orEmpty()
        val pushMessage = PushMessage(title = title, body = body, type = type)
        notificationsManager.onMessageReceived(pushMessage)
    }

    companion object {
        private const val TAG = "MessagingService"

        fun initPushService(context: Context) {
            FirebaseApp.initializeApp(context)
        }

        internal suspend fun getToken(): String =
            suspendCancellableCoroutine { continuation ->
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    Log.i(TAG, "token isSuccessful = ${task.isSuccessful}")
                    if (task.isSuccessful) {
                        val token = task.result.orEmpty()
                        continuation.resume(token)
                    } else continuation.resume("")
                }
            }
    }
}