package com.powerly.vehicles.presentation.vehicleDetails.model

import androidx.lifecycle.ViewModel
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.model.powerly.VehicleModel
import com.powerly.vehicles.domain.use_case.GetVehicleModelsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ModelViewModel(
    private val getVehicleModels: GetVehicleModelsUseCase,
) : ViewModel() {

    fun getModels(makeId: Int): Flow<ApiStatus<List<VehicleModel>>> = flow {
        emit(ApiStatus.Loading)
        emit(getVehicleModels(makeId))
    }
}
