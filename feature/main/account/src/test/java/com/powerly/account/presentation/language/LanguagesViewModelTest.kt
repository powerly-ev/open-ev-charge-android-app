package com.powerly.account.presentation.language

import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.repository.AppRepository
import com.powerly.core.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LanguagesViewModelTest {

    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val appRepository = mockk<AppRepository>(relaxed = true)
    private val viewModel = LanguagesViewModel(userRepository, appRepository)

    @Test
    fun `sets language locally without server call when not logged in`() = runTest {
        coEvery { userRepository.isLoggedIn() } returns false
        var dismissed = false

        viewModel.updateAppLanguage("ar") { dismissed = true }

        coVerify(exactly = 1) { appRepository.setLanguage("ar") }
        coVerify(exactly = 0) { appRepository.updateAppLanguage(any()) }
        assertFalse(dismissed)
    }

    @Test
    fun `updates language on the server and dismisses on success when logged in`() = runTest {
        coEvery { userRepository.isLoggedIn() } returns true
        coEvery { appRepository.updateAppLanguage("ar") } returns ApiStatus.Success(true)
        var dismissed = false

        viewModel.updateAppLanguage("ar") { dismissed = true }

        assertTrue(dismissed)
    }
}
