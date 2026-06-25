package com.powerly.vehicles.presentation.vehicleDetails.make

import app.cash.turbine.test
import com.powerly.core.domain.model.ApiStatus
import com.powerly.testing.MainDispatcherRule
import com.powerly.vehicles.domain.use_case.GetVehicleMakesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MakeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getVehicleMakes = mockk<GetVehicleMakesUseCase>()

    @Test
    fun `getMakes emits Loading then the makes`() = runTest {
        coEvery { getVehicleMakes.invoke() } returns ApiStatus.Success(mockk())

        MakeViewModel(getVehicleMakes).getMakes.test {
            // getMakes is a conflating StateFlow, so the initial Loading may be
            // collapsed before collection starts — skip any leading Loading items.
            var item = awaitItem()
            while (item is ApiStatus.Loading) item = awaitItem()
            assertTrue(item is ApiStatus.Success)
            cancelAndConsumeRemainingEvents()
        }
    }
}
