package com.powerly.splash.presentation

import com.powerly.core.domain.model.AppInfo
import com.powerly.splash.domain.model.SplashAction
import com.powerly.splash.domain.use_case.LoadCountriesUseCase
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SplashViewModelTest {

    private val loadCountriesUseCase = mockk<LoadCountriesUseCase>()
    private val appInfo = mockk<AppInfo>()
    private val viewModel = SplashViewModel(loadCountriesUseCase, appInfo)

    @Test
    fun `loadCountries returns the use-case action`() = runTest {
        coEvery { loadCountriesUseCase.invoke() } returns SplashAction.OpenHomeScreen

        assertEquals(SplashAction.OpenHomeScreen, viewModel.loadCountries())
    }

    @Test
    fun `appLink and appVersion come from appInfo`() {
        every { appInfo.appLink } returns "https://app"
        every { appInfo.appVersion } returns "1.0"

        assertEquals("https://app", viewModel.appLink)
        assertEquals("1.0", viewModel.appVersion)
    }

    @Test
    fun `isOnline delegates to appInfo`() = runTest {
        coEvery { appInfo.isOnline() } returns true

        assertTrue(viewModel.isOnline())
    }
}
