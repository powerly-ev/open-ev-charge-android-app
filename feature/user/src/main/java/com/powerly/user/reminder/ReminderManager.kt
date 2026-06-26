package com.powerly.user.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.powerly.user.reminder.ReminderManager.Companion.destination
import org.koin.core.annotation.Single
import java.util.Calendar


/**
 * Schedules / cancels the registration nag-notification cascade
 * (1h + 24h + 48h after first launch) via [AlarmManager], delivered to
 * [ReminderReceiver].
 */
@Single
class ReminderManager(
    private val context: Context
) {

    companion object {
        private const val TAG = "ReminderManager"
        private val destination = ReminderReceiver::class.java
    }

    fun initRegistrationReminders() {
        Log.v(TAG, "showRegistrationReminders")
        callNotification1H(destination)
        callNotification24H(destination)
        callNotification48H(destination)
    }

    fun cancelRegistrationReminders() {
        Log.v(TAG, "cancelRegistrationReminders")
        callNotification1H(destination, cancel = true)
        callNotification24H(destination, cancel = true)
        callNotification48H(destination, cancel = true)
    }

    private fun callNotificationNMin(
        minutes: Int,
        destination: Class<*>,
        cancel: Boolean = false
    ) {
        val notificationIntent = Intent(context, destination)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT

        val contentIntent = PendingIntent.getBroadcast(context, minutes, notificationIntent, flags)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, minutes)
        if (cancel) am.cancel(contentIntent)
        else am[AlarmManager.RTC_WAKEUP, calendar.timeInMillis] = contentIntent
    }

    private fun callNotification1H(
        destination: Class<*>,
        cancel: Boolean = false
    ) {
        val notificationIntent = Intent(context, destination)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT

        val contentIntent = PendingIntent.getBroadcast(context, 60, notificationIntent, flags)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, 1)
        if (cancel) am.cancel(contentIntent)
        else am[AlarmManager.RTC_WAKEUP, calendar.timeInMillis] = contentIntent
    }

    private fun callNotification24H(
        destination: Class<*>,
        cancel: Boolean = false
    ) {
        val notificationIntent = Intent(context, destination)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT

        val contentIntent = PendingIntent.getBroadcast(context, 24, notificationIntent, flags)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        if (cancel) am.cancel(contentIntent)
        else am[AlarmManager.RTC_WAKEUP, calendar.timeInMillis] = contentIntent
    }

    private fun callNotification48H(
        destination: Class<*>,
        cancel: Boolean = false
    ) {
        val notificationIntent = Intent(context, destination)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT

        val contentIntent = PendingIntent.getBroadcast(context, 48, notificationIntent, flags)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 2)
        if (cancel) am.cancel(contentIntent)
        else am[AlarmManager.RTC_WAKEUP, calendar.timeInMillis] = contentIntent
    }
}