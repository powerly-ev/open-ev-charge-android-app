package com.powerly.user.domain.use_case

import com.powerly.core.data.repositories.AppRepository
import com.powerly.user.reminder.ReminderManager
import org.koin.core.annotation.Single

/**
 * Schedules the registration reminder notifications for unregistered users,
 * but only when [AppRepository.showRegisterNotification] indicates the user
 * hasn't already completed registration.
 */
@Single
class InitRegistrationRemindersUseCase(
    private val appRepository: AppRepository,
    private val reminderManager: ReminderManager
) {
    suspend operator fun invoke() {
        if (appRepository.showRegisterNotification()) {
            reminderManager.initRegistrationReminders()
        }
    }
}
