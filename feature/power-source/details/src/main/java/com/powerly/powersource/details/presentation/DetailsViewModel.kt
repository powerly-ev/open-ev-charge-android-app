package com.powerly.powersource.details.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.powerly.core.data.model.SourceStatus
import com.powerly.core.data.repositories.AppRepository
import com.powerly.core.data.repositories.PowerSourceRepository
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.powerly.PowerSource
import com.powerly.lib.managers.UserLocationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class DetailsViewModel(
    private val powerSourceRepository: PowerSourceRepository,
    private val locationManager: UserLocationManager,
    private val userRepository: UserRepository,
    private val appRepository: AppRepository,
) : ViewModel() {

    private val _powerSourceStatus = MutableStateFlow<SourceStatus>(SourceStatus.Loading)
    val powerSourceStatus = _powerSourceStatus.asStateFlow()

    var powerSource: PowerSource? = null
        private set

    fun userLocation() = locationManager.getLastLocation()

    fun getPowerSource(
        id: String,
        latitude: Double?,
        longitude: Double?
    ) {
        if (powerSource?.id != id) {
            _powerSourceStatus.value = SourceStatus.Loading
            powerSource = null
        }
        viewModelScope.launch {
            val result = powerSourceRepository.getPowerSource(id)
            if (result is SourceStatus.Success) {
                val powerSource = result.powerSource.apply {
                    this.currency = userRepository.getCurrency()
                    this.distance(latitude, longitude)
                }
                this@DetailsViewModel.powerSource = powerSource
                _powerSourceStatus.emit(SourceStatus.Success(powerSource))
            } else {
                _powerSourceStatus.emit(result)
            }
        }
    }

    suspend fun showOnBoardingOnce() = appRepository.showOnBoardingOnce()
}
