package com.powerly.home.presentation.home

import android.content.Intent
import com.powerly.core.domain.model.AppInfo
import com.powerly.core.domain.repository.PowerSourceRepository
import com.powerly.core.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeViewModelTest {

    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val powerSourceRepository = mockk<PowerSourceRepository>(relaxed = true)
    private val appInfo = mockk<AppInfo>()
    private val viewModel = HomeViewModel(userRepository, powerSourceRepository, appInfo)

    @Test
    fun `isLoggedIn delegates to the user repository`() = runTest {
        coEvery { userRepository.isLoggedIn() } returns true

        assertTrue(viewModel.isLoggedIn())
    }

    @Test
    fun `supportNumber comes from appInfo`() {
        every { appInfo.supportNumber } returns "+100"

        assertEquals("+100", viewModel.supportNumber)
    }

    @Test
    fun `getPowerSourceFromDeepLink returns null when the intent has no source extra`() = runTest {
        val intent = mockk<Intent>()
        every { intent.hasExtra(any()) } returns false

        assertNull(viewModel.getPowerSourceFromDeepLink(intent))
    }
}
