package com.powerly.orders

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.powerly.core.data.repositories.SessionsRepository
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.powerly.Session
import com.powerly.lib.managers.PusherManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class SessionViewModel(
    private val sessionsRepository: SessionsRepository,
    private val userRepository: UserRepository,
    private val pusherManager: PusherManager
) : ViewModel() {

    private val _sessionCompletion = MutableSharedFlow<Session>(replay = 0)
    val sessionCompletionFlow = _sessionCompletion.asSharedFlow()

    val currency = userRepository.currencyFlow
    val refresh = mutableStateOf(false)

    fun refreshCompletedOrders() {
        refresh.value = true
    }


    init {
        viewModelScope.launch {
            pusherManager.sessionCompletionFlow.collect {
                if (it != null) _sessionCompletion.emit(it)
            }
        }
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