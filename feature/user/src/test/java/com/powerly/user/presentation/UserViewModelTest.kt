package com.powerly.user.presentation

import com.powerly.core.domain.model.AppInfo
import com.powerly.core.domain.model.location.Country
import com.powerly.core.domain.repository.AppRepository
import com.powerly.testing.MainDispatcherRule
import com.powerly.user.domain.use_case.CancelRegistrationRemindersUseCase
import com.powerly.user.domain.use_case.DetectCountryUseCase
import com.powerly.user.domain.use_case.InitRegistrationRemindersUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class UserViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val appRepository = mockk<AppRepository>(relaxed = true)
    private val detectCountryUseCase = mockk<DetectCountryUseCase>()
    private val cancelUseCase = mockk<CancelRegistrationRemindersUseCase>(relaxed = true)
    private val initUseCase = mockk<InitRegistrationRemindersUseCase>(relaxed = true)
    private val appInfo = mockk<AppInfo>()

    private fun viewModel() =
        UserViewModel(appRepository, detectCountryUseCase, cancelUseCase, initUseCase, appInfo)

    private val egypt = Country(id = 1, name = "Egypt", iso = "EG")

    @Test
    fun `detectCountry populates countries and the selected country`() = runTest {
        coEvery { detectCountryUseCase.invoke() } returns
            DetectCountryUseCase.Result(countries = listOf(egypt), country = egypt)

        val viewModel = viewModel()
        viewModel.detectCountry()

        assertEquals(listOf(egypt), viewModel.countries.toList())
        assertEquals(egypt, viewModel.country.value)
    }

    @Test
    fun `cancelRegistrationReminders delegates to the use case`() = runTest {
        viewModel().cancelRegistrationReminders()

        coVerify(exactly = 1) { cancelUseCase.invoke() }
    }

    @Test
    fun `initRegistrationReminders delegates to the use case`() = runTest {
        viewModel().initRegistrationReminders()

        coVerify(exactly = 1) { initUseCase.invoke() }
    }

    @Test
    fun `updateDevice delegates to the repository`() = runTest {
        viewModel().updateDevice()

        coVerify(exactly = 1) { appRepository.updateDevice() }
    }

    @Test
    fun `appVersion formats build type and version`() {
        every { appInfo.buildType } returns "debug"
        every { appInfo.appVersion } returns "1.2.3"

        assertEquals("Version : debug 1.2.3", viewModel().appVersion)
    }

    @Test
    fun `supportNumber comes from appInfo`() {
        every { appInfo.supportNumber } returns "+1000"

        assertEquals("+1000", viewModel().supportNumber)
    }
}
