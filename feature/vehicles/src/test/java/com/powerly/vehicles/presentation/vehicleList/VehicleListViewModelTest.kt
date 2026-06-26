package com.powerly.vehicles.presentation.vehicleList

import app.cash.turbine.test
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.powerly.Vehicle
import com.powerly.testing.MainDispatcherRule
import com.powerly.vehicles.domain.repository.VehiclesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class VehicleListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val vehiclesRepository = mockk<VehiclesRepository>()
    private val viewModel = VehicleListViewModel(vehiclesRepository)

    @Test
    fun `getVehicles publishes the loaded list`() = runTest {
        coEvery { vehiclesRepository.vehiclesList() } returns ApiStatus.Success(listOf(Vehicle(id = 1)))

        viewModel.getVehicles()

        assertTrue(viewModel.vehiclesList.value is ApiStatus.Success)
    }

    @Test
    fun `updateVehicle emits Loading then the result`() = runTest {
        coEvery { vehiclesRepository.vehicleUpdate(any()) } returns ApiStatus.Success(true)

        viewModel.updateVehicle(Vehicle(id = 1)).test {
            assertEquals(ApiStatus.Loading, awaitItem())
            assertTrue(awaitItem() is ApiStatus.Success)
            awaitComplete()
        }
    }

    @Test
    fun `deleteVehicle emits Loading then the result`() = runTest {
        coEvery { vehiclesRepository.vehicleDelete(1) } returns ApiStatus.Success(true)

        viewModel.deleteVehicle(1).test {
            assertEquals(ApiStatus.Loading, awaitItem())
            assertTrue(awaitItem() is ApiStatus.Success)
            awaitComplete()
        }
    }
}
