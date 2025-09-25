package com.powerly.user.reminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.powerly.lib.managers.StorageManager
import com.powerly.resources.R
import com.powerly.ui.extensions.intent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


/**
 *  ReminderReceiver is a BroadcastReceiver that listens for a specific broadcast intent.
 *  Upon receiving the intent, it checks if the user is logged in. If the user is NOT logged in,
 *  it displays a notification to remind the user to complete the registration process.
 */
class ReminderReceiver : BroadcastReceiver(), KoinComponent {

    private val storageManager: StorageManager by inject()

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "delivery_channel"
    }

    override fun onReceive(context: Context, intent: Intent) = showNotification(context)

    // show complete registration notification
    private fun showNotification(context: Context) {
        if (storageManager.isLoggedIn) return

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        initNotificationChannel(notificationManager)

        val bigText = NotificationCompat
            .BigTextStyle()
            .setBigContentTitle(context.getString(R.string.app_name))
            .bigText(context.resources.getString(R.string.reminder_message))

        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.notifications_icon)
            .setColor(ContextCompat.getColor(context, R.color.notification_icon_bg))
            .setStyle(bigText)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.resources.getString(R.string.reminder_message))
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)

        val flag =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT

        val contentIntent = PendingIntent.getActivity(context, 0, context.intent, flag)
        notificationBuilder.setContentIntent(contentIntent)
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun initNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Delivery", NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}