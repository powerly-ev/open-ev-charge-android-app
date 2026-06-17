package com.powerly.user.presentation.welcome.language

import com.powerly.core.domain.repository.AppRepository
import com.powerly.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class LanguagesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val appRepository = mockk<AppRepository>(relaxed = true)

    @Test
    fun `changeAppLanguage sets a different language`() = runTest {
        coEvery { appRepository.getLanguage() } returns "en"

        LanguagesViewModel(appRepository).changeAppLanguage("ar")

        coVerify(exactly = 1) { appRepository.setLanguage("ar") }
    }

    @Test
    fun `changeAppLanguage skips when the language is unchanged`() = runTest {
        coEvery { appRepository.getLanguage() } returns "en"

        LanguagesViewModel(appRepository).changeAppLanguage("en")

        coVerify(exactly = 0) { appRepository.setLanguage(any()) }
    }
}
