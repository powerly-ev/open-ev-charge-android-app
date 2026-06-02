package com.powerly.vehicles.presentation.newVehicle

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.Vehicle
import com.powerly.core.model.powerly.VehicleMaker
import com.powerly.core.model.powerly.VehicleModel
import com.powerly.vehicles.domain.repository.VehiclesRepository
import kotlinx.coroutines.flow.flow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class NewVehicleViewModel(
    private val vehiclesRepository: VehiclesRepository,
) : ViewModel() {

    val vehicle = mutableStateOf(Vehicle(id = 1))
    val makeId: Int get() = vehicle.value.maker?.id ?: 0

    fun setVehicle(vehicle: Vehicle) {
        this.vehicle.value = vehicle
    }

    fun setMake(maker: VehicleMaker) {
        this.vehicle.value.maker = maker
    }

    fun setModel(model: VehicleModel) {
        this.vehicle.value.model = model
    }

    var openModels: Boolean = true
        private set

    fun showModels(show: Boolean) {
        openModels = show
    }

    fun updateVehicle(vehicle: Vehicle) = flow {
        emit(ApiStatus.Loading)
        emit(vehiclesRepository.vehicleUpdate(vehicle))
    }
}
