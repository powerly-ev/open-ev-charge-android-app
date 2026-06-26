package com.powerly.vehicles.presentation.vehicleList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.domain.model.powerly.Vehicle
import com.powerly.vehicles.domain.repository.VehiclesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class VehicleListViewModel(
    private val vehiclesRepository: VehiclesRepository,
) : ViewModel() {

    private val _vehiclesList = MutableStateFlow<ApiStatus<List<Vehicle>>>(ApiStatus.Loading)
    val vehiclesList = _vehiclesList.asStateFlow()

    fun getVehicles() {
        viewModelScope.launch {
            _vehiclesList.emit(ApiStatus.Loading)
            _vehiclesList.emit(vehiclesRepository.vehiclesList())
        }
    }

    fun updateVehicle(vehicle: Vehicle) = flow {
        emit(ApiStatus.Loading)
        emit(vehiclesRepository.vehicleUpdate(vehicle))
    }

    fun deleteVehicle(id: Int) = flow {
        emit(ApiStatus.Loading)
        emit(vehiclesRepository.vehicleDelete(id))
    }
}
