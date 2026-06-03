package com.powerly.powersource.details.presentation.reviews

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.powerly.core.data.repositories.PowerSourceRepository
import com.powerly.core.model.powerly.Review
import com.powerly.lib.AppRoutes
import kotlinx.coroutines.flow.Flow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ReviewsViewModel(
    powerSourceRepository: PowerSourceRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val id = savedStateHandle.toRoute<AppRoutes.PowerSource.Reviews>().id

    val reviews: Flow<PagingData<Review>> =
        powerSourceRepository.getReviews(id).cachedIn(viewModelScope)
}
