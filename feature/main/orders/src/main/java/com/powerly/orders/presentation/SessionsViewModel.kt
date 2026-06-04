package com.powerly.orders.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.powerly.core.domain.repository.UserRepository
import com.powerly.core.model.powerly.Session
import com.powerly.core.managers.PusherManager
import com.powerly.orders.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class SessionsViewModel(
    private val sessionsRepository: SessionsRepository,
    userRepository: UserRepository,
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

    val selectedSession = mutableStateOf<Session?>(null)

    fun setSession(session: Session) {
        this.selectedSession.value = session.apply { status = 2 }
    }

    val activeOrders = sessionsRepository.activeOrders.stateIn(
        scope = viewModelScope,
        initialValue = PagingData.from(listOf<Session>()),
        started = SharingStarted.Lazily
    ).cachedIn(viewModelScope)

    val completedOrders = sessionsRepository.completedOrders.stateIn(
        scope = viewModelScope,
        initialValue = PagingData.from(listOf<Session>()),
        started = SharingStarted.Lazily
    ).cachedIn(viewModelScope)

    suspend fun getPendingOrderToReview(): Session? {
        return null
    }
}
