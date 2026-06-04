package com.powerly.powersource.details.presentation.media

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.powerly.core.domain.repository.PowerSourceRepository
import com.powerly.core.model.powerly.Media
import com.powerly.navigation.AppRoutes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class MediaViewModel(
    private val powerSourceRepository: PowerSourceRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val id = savedStateHandle.toRoute<AppRoutes.PowerSource.Media>().id

    private val _media = MutableStateFlow<List<Media>>(emptyList())
    val media = _media.asStateFlow()

    init {
        viewModelScope.launch {
            _media.value = powerSourceRepository.getMedia(id)
        }
    }
}
