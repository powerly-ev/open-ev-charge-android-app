package com.powerly.vehicles

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.data.model.MakersStatus
import com.powerly.core.data.model.ModelsStatus
import com.powerly.core.data.repositories.VehiclesRepository
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.Vehicle
import com.powerly.core.model.powerly.VehicleMaker
import com.powerly.core.model.powerly.VehicleModel
import org.koin.android.annotation.KoinViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@KoinViewModel
class VehiclesViewModel (
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

    /**
     * A flag indicating whether the models screen is open.
     */
    var openModels: Boolean = true

    /**
     * A flow representing the status of the vehicles list.
     * Emits [ApiStatus.Loading] while loading, [ApiStatus.Success] with the list of vehicles
     * if successful, or [ApiStatus.Error] if an error occurred.
     */
    private val _vehiclesList = MutableStateFlow<ApiStatus<List<Vehicle>>>(ApiStatus.Loading)
    val vehiclesList = _vehiclesList.asStateFlow()

    /**
     * Fetches the list of vehicles from the repository and updates the [vehiclesList] flow.
     */
    fun getVehicles() {
        viewModelScope.launch {
            _vehiclesList.emit(ApiStatus.Loading)
            val it = vehiclesRepository.vehiclesList()
            _vehiclesList.emit(it)
        }
    }

    /**
     * A flow representing the status of the vehicle models.
     * Emits [ModelsStatus.Loading] while loading, [ModelsStatus.Success] with the list of models
     * if successful, or [ModelsStatus.Error] if an error occurred.
     * This flow is started when the ViewModel is created and remains active while subscribed.
     */
    val getModels = flow {
        emit(ModelsStatus.Loading)
        val it = vehiclesRepository.vehiclesModels(makeId)
        emit(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ModelsStatus.Loading
    )

    /**
     * A flow representing the status of the vehicle manufacturers.
     * Emits [MakersStatus.Loading] while loading, [MakersStatus.Success] with the list of makes
     * if successful, or [MakersStatus.Error] if an error occurred.
     * This flow is started lazily to load manufacturers once
     */
    val getMakes = flow {
        emit(MakersStatus.Loading)
        val it = vehiclesRepository.vehiclesMakes()
        emit(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = MakersStatus.Loading
    )

    /**
     * Updates a vehicle in the repository.
     *
     * @param vehicle The vehicle to update.
     * @return A flow representing the status of the update operation.
     * Emits [ApiStatus.Loading] while updating, [ApiStatus.Success] if the update was successful,
     * or [ApiStatus.Error] if an error occurred.
     */
    fun updateVehicle(vehicle: Vehicle) = flow {
        emit(ApiStatus.Loading)
        val it = vehiclesRepository.vehicleUpdate(vehicle)
        emit(it)
    }

    /**
     * Deletes a vehicle from the repository.
     *
     * @param id The ID of the vehicle to delete.
     * @return A flow representing the status of the delete operation.
     * Emits [ApiStatus.Loading] while deleting, [ApiStatus.Success] if the deletion was successful,
     * or [ApiStatus.Error] if an error occurred.
     */
    fun deleteVehicle(id: Int) = flow {
        emit(ApiStatus.Loading)
        val it = vehiclesRepository.vehicleDelete(id)
        emit(it)
    }
}