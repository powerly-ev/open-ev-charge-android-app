package com.powerly.vehicles.presentation.newVehicle

import com.powerly.core.domain.model.powerly.Vehicle
import com.powerly.core.domain.model.powerly.VehicleMaker
import com.powerly.vehicles.domain.repository.VehiclesRepository
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class NewVehicleViewModelTest {

    private val vehiclesRepository = mockk<VehiclesRepository>()
    private val viewModel = NewVehicleViewModel(vehiclesRepository)

    @Test
    fun `setMake updates the exposed maker id`() {
        viewModel.setMake(VehicleMaker(id = 5))

        assertEquals(5, viewModel.makeId)
    }

    @Test
    fun `setVehicle replaces the current vehicle`() {
        viewModel.setVehicle(Vehicle(id = 9, title = "Tesla"))

        assertEquals("Tesla", viewModel.vehicle.value.title)
    }

    @Test
    fun `showModels toggles the flag`() {
        viewModel.showModels(false)

        assertFalse(viewModel.openModels)
    }
}
