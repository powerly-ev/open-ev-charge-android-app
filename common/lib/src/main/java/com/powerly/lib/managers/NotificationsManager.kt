package com.powerly.lib.managers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.gson.annotations.SerializedName
import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.model.user.DeviceBody
import com.powerly.lib.MainScreen.setMainScreenHome
import com.powerly.lib.MyPackages
import com.powerly.resources.R
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class NotificationsManager(
    private val context: Context,
    private val storageManager: StorageManager,
    private val appRepository: AppRepository
) {
    companion object {
        private const val TAG = "NotificationsManager"
        private const val NOTIFICATION_CHANNEL_ID = "powerly_notification_channel"
    }

    init {
        MessagingService.initPushService(context)
    }

    fun onMessageReceived(pushMessage: PushMessage) {
        val type = pushMessage.type
        val body = pushMessage.body
        val title = pushMessage.title
        Log.i(TAG, "pushMessage - $pushMessage")
        Log.w(TAG, "notificationType - $type")
        sendNotificationWhitClass(
            messageTitle = title,
            messageBody = body
        )
    }

    private fun sendNotificationWhitClass(
        messageTitle: String,
        messageBody: String
    ) {
        val intent = Intent(context, Class.forName(MyPackages.MAIN))
        intent.setMainScreenHome()

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, flag)

        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        initNotificationChannel(manager)

        val bigText = NotificationCompat
            .BigTextStyle()
            .setBigContentTitle(messageTitle)
            .bigText(messageBody)

        val notificationBuilder =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.notifications_icon)
                .setColor(ContextCompat.getColor(context, R.color.notification_icon_bg))
                .setStyle(bigText)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)

        notificationBuilder.setContentIntent(pendingIntent)
        val notificationId = UUID.randomUUID().hashCode()
        manager.notify(notificationId, notificationBuilder.build())
    }


    private fun initNotificationChannel(
        manager: NotificationManager?,
        channelId: String = NOTIFICATION_CHANNEL_ID,
        vibration: Boolean = true
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelId,
                NotificationManager.IMPORTANCE_HIGH
            )
            if (vibration) {
                notificationChannel.enableLights(true)
                notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                notificationChannel.enableVibration(true)
            }
            manager?.createNotificationChannel(notificationChannel)
        }
    }

    suspend fun saveToken(token: String) {
        Log.i(TAG, "saveMessagingToken - $token")
        // store token locally
        storageManager.messagingToken = token
        // update token at server
        if (storageManager.isLoggedIn) {
            val deviceBody = DeviceBody(
                imei = storageManager.imei(),
                token = token
            )
            appRepository.updateDevice(deviceBody)
        }
    }

    fun clearNotifications() {
        val nm = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()
    }

    suspend fun getToken(): String {
        val token = MessagingService.getToken()
        storageManager.messagingToken = token
        return token
    }
}

data class PushMessage(
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("notification_type") val type: String
)