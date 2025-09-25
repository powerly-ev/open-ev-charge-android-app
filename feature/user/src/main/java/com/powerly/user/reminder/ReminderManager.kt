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
 * Manages registration reminders using the Android AlarmManager.
 *
 * This class is responsible for scheduling and canceling various types of
 * reminders that trigger notifications. These notifications can be set to occur
 * at specific intervals after a certain event, such as an app installation or
 * user registration.
 *
 * @property context The application context, needed to access system services
 *   like [AlarmManager]. It's injected using dependency injection.
 */
@Single
class ReminderManager (
     private val context: Context
) {

    companion object {
        /**
         * A tag used for logging purposes to identify the source of log messages.
         */
        private const val TAG = "ReminderManager"
        private val destination = ReminderReceiver::class.java
    }

    /**
     * Schedules a series of registration reminder notifications.
     *
     * This method sets up multiple alarms to trigger at different time intervals
     * (1 hour, 24 hours, and 48 hours). Each alarm will launch the specified
     * destination activity or broadcast receiver. It also calls the
     * `disableNotification` lambda to perform any actions necessary to disable any
     * other conflicting notifications.
     */
    fun initRegistrationReminders() {
        Log.v(TAG, "showRegistrationReminders")
        callNotification1H(destination)
        callNotification24H(destination)
        callNotification48H(destination)
    }

    /**
     * Cancels all previously scheduled registration reminder notifications.
     *
     * This method cancels the alarms that were set up by `showRegistrationReminders`.
     *
     * @param destination The class of the component (Activity or BroadcastReceiver)
     *   that was scheduled to be launched by the reminders.
     */
    fun cancelRegistrationReminders() {
        Log.v(TAG, "cancelRegistrationReminders")
        callNotification1H(destination, cancel = true)
        callNotification24H(destination, cancel = true)
        callNotification48H(destination, cancel = true)
    }

    /**
     * Schedules or cancels a reminder notification that triggers after a specific number of minutes.
     *
     * This is a private utility function that can set up a notification for a specific number of minutes or cancel it.
     *
     * @param minutes The number of minutes to wait before triggering the notification.
     * @param destination The class of the component (Activity or BroadcastReceiver) that
     *   should be launched when the reminder is triggered.
     * @param cancel A boolean flag indicating whether to cancel the notification (true)
     *   or to schedule it (false). Defaults to false.
     */
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

    /**
     * Schedules or cancels a reminder notification that triggers after 1 hour.
     *
     * This is a private utility function to set up or cancel a notification
     * specifically for the 1-hour interval.
     *
     * @param destination The class of the component (Activity or BroadcastReceiver)
     *   that should be launched when the reminder is triggered.
     * @param cancel A boolean flag indicating whether to cancel the notification (true)
     *   or to schedule it (false). Defaults to false.
     */
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

    /**
     * Schedules or cancels a reminder notification that triggers after 24 hours.
     *
     * This is a private utility function to set up or cancel a notification
     * specifically for the 24-hour interval.
     *
     * @param destination The class of the component (Activity or BroadcastReceiver)
     *   that should be launched when the reminder is triggered.
     * @param cancel A boolean flag indicating whether to cancel the notification (true)
     *   or to schedule it (false). Defaults to false.
     */
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

    /**
     * Schedules or cancels a reminder notification that triggers after 48 hours.
     *
     * This is a private utility function to set up or cancel a notification
     * specifically for the 48-hour interval.
     *
     * @param destination The class of the component (Activity or BroadcastReceiver)
     *   that should be launched when the reminder is triggered.
     * @param cancel A boolean flag indicating whether to cancel the notification (true)
     *   or to schedule it (false). Defaults to false.
     */
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