package com.powerly.user.domain.use_case

import com.powerly.core.domain.repository.AppRepository
import com.powerly.user.reminder.ReminderManager
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class InitRegistrationRemindersUseCaseTest {

    private val appRepository = mockk<AppRepository>()
    private val reminderManager = mockk<ReminderManager>(relaxed = true)
    private val useCase = InitRegistrationRemindersUseCase(appRepository, reminderManager)

    @Test
    fun `schedules reminders when registration notification is due`() = runTest {
        coEvery { appRepository.showRegisterNotification() } returns true

        useCase()

        verify(exactly = 1) { reminderManager.initRegistrationReminders() }
    }

    @Test
    fun `does nothing when registration notification is not due`() = runTest {
        coEvery { appRepository.showRegisterNotification() } returns false

        useCase()

        verify(exactly = 0) { reminderManager.initRegistrationReminders() }
    }
}
