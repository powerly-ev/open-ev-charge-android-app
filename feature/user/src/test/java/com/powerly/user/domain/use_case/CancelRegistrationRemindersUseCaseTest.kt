package com.powerly.user.domain.use_case

import com.powerly.core.domain.repository.UserRepository
import com.powerly.user.reminder.ReminderManager
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CancelRegistrationRemindersUseCaseTest {

    private val userRepository = mockk<UserRepository>()
    private val reminderManager = mockk<ReminderManager>(relaxed = true)
    private val useCase = CancelRegistrationRemindersUseCase(userRepository, reminderManager)

    @Test
    fun `cancels reminders when the user is logged in`() = runTest {
        coEvery { userRepository.isLoggedIn() } returns true

        useCase()

        verify(exactly = 1) { reminderManager.cancelRegistrationReminders() }
    }

    @Test
    fun `does nothing when the user is not logged in`() = runTest {
        coEvery { userRepository.isLoggedIn() } returns false

        useCase()

        verify(exactly = 0) { reminderManager.cancelRegistrationReminders() }
    }
}
