package com.powerly.powersource.charging.presentation.feedback

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.powerly.core.domain.model.ApiStatus
import com.powerly.navigation.AppRoutes
import com.powerly.powersource.charging.domain.model.ReviewOptionsStatus
import com.powerly.powersource.charging.domain.repository.FeedbackRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class FeedbackViewModel(
    private val feedbackRepository: FeedbackRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val feedback = savedStateHandle.toRoute<AppRoutes.PowerSource.Feedback>()
    internal val title: String get() = feedback.title

    val reviewOptions = feedbackRepository.reviewOptions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = ReviewOptionsStatus.Loading
    )

    fun submitReview(rating: Int, msg: String) = flow {
        emit(ApiStatus.Loading)
        val result = feedbackRepository.reviewAdd(
            orderId = feedback.sessionId,
            rating = rating.toDouble(),
            msg = msg
        )
        emit(result)
    }

    fun skipReview() {
        viewModelScope.launch(NonCancellable) {
            feedbackRepository.reviewSkip(feedback.sessionId)
        }
    }
}
