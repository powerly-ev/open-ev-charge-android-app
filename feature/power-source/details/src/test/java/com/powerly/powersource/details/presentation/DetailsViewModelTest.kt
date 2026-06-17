package com.powerly.powersource.details.presentation

import com.powerly.core.domain.model.SourceStatus
import com.powerly.core.domain.model.powerly.PowerSource
import com.powerly.core.domain.repository.AppRepository
import com.powerly.core.domain.repository.PowerSourceRepository
import com.powerly.core.domain.repository.UserRepository
import com.powerly.core.domain.manager.UserLocationManager
import com.powerly.testing.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val powerSourceRepository = mockk<PowerSourceRepository>()
    private val locationManager = mockk<UserLocationManager>(relaxed = true)
    private val userRepository = mockk<UserRepository>(relaxed = true)
    private val appRepository = mockk<AppRepository>(relaxed = true)

    private val viewModel = DetailsViewModel(
        powerSourceRepository, locationManager, userRepository, appRepository,
    )

    @Test
    fun `getPowerSource publishes success and applies the user currency`() = runTest {
        coEvery { powerSourceRepository.getPowerSource("p1") } returns
            SourceStatus.Success(PowerSource(id = "p1"))
        coEvery { userRepository.getCurrency() } returns "USD"

        viewModel.getPowerSource(id = "p1", latitude = 10.0, longitude = 20.0)

        assertTrue(viewModel.powerSourceStatus.value is SourceStatus.Success)
        assertEquals("p1", viewModel.powerSource?.id)
        assertEquals("USD", viewModel.powerSource?.currency)
    }

    @Test
    fun `showOnBoardingOnce delegates to the app repository`() = runTest {
        coEvery { appRepository.showOnBoardingOnce() } returns true

        assertTrue(viewModel.showOnBoardingOnce())
    }
}
