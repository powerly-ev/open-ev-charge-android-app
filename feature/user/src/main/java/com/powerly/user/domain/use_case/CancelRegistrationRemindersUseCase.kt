package com.powerly.user.domain.use_case

import com.powerly.core.domain.repository.UserRepository
import com.powerly.user.reminder.ReminderManager
import org.koin.core.annotation.Single

/**
 * Cancels pending registration reminders once the user is logged in, so they
 * don't keep nagging an already-registered account.
 */
@Single
class CancelRegistrationRemindersUseCase(
    private val userRepository: UserRepository,
    private val reminderManager: ReminderManager
) {
    suspend operator fun invoke() {
        if (userRepository.isLoggedIn()) {
            reminderManager.cancelRegistrationReminders()
        }
    }
}
