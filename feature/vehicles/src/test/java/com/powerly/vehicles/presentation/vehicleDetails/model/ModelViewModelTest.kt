package com.powerly.vehicles.presentation.vehicleDetails.model

import app.cash.turbine.test
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.powerly.VehicleModel
import com.powerly.vehicles.domain.use_case.GetVehicleModelsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelViewModelTest {

    private val getVehicleModels = mockk<GetVehicleModelsUseCase>()
    private val viewModel = ModelViewModel(getVehicleModels)

    @Test
    fun `getModels emits Loading then the models for the make`() = runTest {
        coEvery { getVehicleModels.invoke(5) } returns ApiStatus.Success(listOf(VehicleModel(id = 1)))

        viewModel.getModels(5).test {
            assertEquals(ApiStatus.Loading, awaitItem())
            assertTrue(awaitItem() is ApiStatus.Success)
            awaitComplete()
        }
    }
}
