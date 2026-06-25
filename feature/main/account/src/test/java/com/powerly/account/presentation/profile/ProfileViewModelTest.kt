package com.powerly.account.presentation.profile

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.AppInfo
import com.powerly.core.domain.model.location.Country
import com.powerly.core.domain.repository.AppRepository
import com.powerly.core.domain.repository.UserRepository
import com.powerly.core.domain.manager.NotificationsManager
import com.powerly.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val appRepository = mockk<AppRepository>(relaxed = true)
    private val appInfo = mockk<AppInfo>()
    private val notificationsManager = mockk<NotificationsManager>(relaxed = true)

    private fun viewModel(): ProfileViewModel {
        every { userRepository.userFlow } returns emptyFlow()
        return ProfileViewModel(userRepository, appRepository, appInfo, notificationsManager)
    }

    @Test
    fun `logout clears notifications and returns true on success`() = runTest {
        coEvery { userRepository.isLoggedIn() } returns true
        coEvery { userRepository.logout() } returns ApiStatus.Success(true)

        assertTrue(viewModel().logout())
        coVerify { notificationsManager.clearNotifications() }
    }

    @Test
    fun `logout returns false when not logged in`() = runTest {
        coEvery { userRepository.isLoggedIn() } returns false

        assertFalse(viewModel().logout())
    }

    @Test
    fun `deleteAccount clears notifications and returns true on success`() = runTest {
        coEvery { userRepository.deleteUser() } returns ApiStatus.Success(true)

        assertTrue(viewModel().deleteAccount())
        coVerify { notificationsManager.clearNotifications() }
    }

    @Test
    fun `updateUserCountry updates the selected country`() = runTest {
        val viewModel = viewModel()

        viewModel.updateUserCountry(Country(id = 7, name = "Egypt"))

        assertEquals(7, viewModel.getUserCountry().id)
    }

    @Test
    fun `appLink comes from appInfo`() = runTest {
        every { appInfo.appLink } returns "https://app"

        assertEquals("https://app", viewModel().appLink)
    }
}
