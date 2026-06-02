package com.powerly.vehicles.presentation.vehicleDetails.make

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.model.api.ApiStatus
import com.powerly.vehicles.domain.use_case.GetVehicleMakesUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MakeViewModel(
    private val getVehicleMakes: GetVehicleMakesUseCase,
) : ViewModel() {

    val getMakes = flow {
        emit(ApiStatus.Loading)
        emit(getVehicleMakes())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = ApiStatus.Loading
    )
}
