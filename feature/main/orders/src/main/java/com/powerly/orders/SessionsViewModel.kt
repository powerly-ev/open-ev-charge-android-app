package com.powerly.orders

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.powerly.core.data.repositories.FeedbackRepository
import com.powerly.core.data.repositories.SessionsRepository
import com.powerly.core.model.powerly.Session
import com.powerly.lib.managers.StorageManager
import org.koin.android.annotation.KoinViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn


@KoinViewModel
class SessionViewModel (
    private val sessionsRepository: SessionsRepository,
    private val feedbackRepository: FeedbackRepository,
    private val storageManager: StorageManager
) : ViewModel() {

    val currency: String get() = storageManager.currency

    val refresh = mutableStateOf(false)

    fun refreshCompletedOrders() {
        refresh.value = true
    }

    /**
     * Holds the currently selected session, if any.
     */
    val selectedSession = mutableStateOf<Session?>(null)

    fun setSession(session: Session) {
        this.selectedSession.value = session.apply { status = 2 }
    }

    /**
     * Flow of Active sessions for the current user.
     */
    val activeOrders = sessionsRepository.activeOrders.stateIn(
        scope = viewModelScope,
        initialValue = PagingData.from(listOf<Session>()),
        started = SharingStarted.Lazily
    ).cachedIn(viewModelScope)

    /**
     * Flow of session history for the charge point owner.
     */
    val completedOrders = sessionsRepository.completedOrders.stateIn(
        scope = viewModelScope,
        initialValue = PagingData.from(listOf<Session>()),
        started = SharingStarted.Lazily
    ).cachedIn(viewModelScope)


    /**
     * Check for pending feedback to display to users
     */
    suspend fun getPendingOrderToReview(): Session? {

        return null
    }
}