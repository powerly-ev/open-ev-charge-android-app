package com.powerly.scan.presentation

import androidx.lifecycle.ViewModel
import com.powerly.core.domain.repository.PowerSourceRepository
import com.powerly.core.domain.model.ApiStatus
import kotlinx.coroutines.flow.flow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ScannerViewModel(
    private val powerSourceRepository: PowerSourceRepository,
) : ViewModel() {
    fun powerSourceDetails(identifier: String) = flow {
        emit(ApiStatus.Loading)
        val it = powerSourceRepository.getPowerSourceByIdentifier(identifier)
        emit(it)
    }
}
