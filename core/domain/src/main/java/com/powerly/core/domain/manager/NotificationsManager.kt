package com.powerly.core.domain.manager

/**
 * Notification operations needed by presentation/domain.
 *
 * Push-message handling and token registration are infrastructure concerns handled
 * by the implementation (and the messaging service), not exposed here.
 */
interface NotificationsManager {

    /** Dismisses all currently shown notifications. */
    fun clearNotifications()
}
