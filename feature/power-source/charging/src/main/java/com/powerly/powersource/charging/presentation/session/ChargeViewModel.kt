package com.powerly.powersource.charging.presentation.session

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.powerly.core.data.model.ChargingStatus
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.powerly.Session
import com.powerly.lib.AppRoutes
import com.powerly.lib.managers.PusherManager
import com.powerly.powersource.charging.domain.repository.ChargingRepository
import com.powerly.powersource.charging.timer.ChargingTimerManager
import com.powerly.powersource.charging.timer.ChargingTimerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class ChargeViewModel(
    private val chargingRepository: ChargingRepository,
    private val chargingTimerManager: ChargingTimerManager,
    private val pusherManager: PusherManager,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val orderId = savedStateHandle.toRoute<AppRoutes.PowerSource.Charging>().orderId

    val timerState = ChargingTimerState(0, 0)
    val session = mutableStateOf<Session?>(null)
    private var pendingSessionId: String = ""

    private val _chargingStatus = MutableStateFlow<ChargingStatus>(ChargingStatus.Loading)
    val chargingStatus = _chargingStatus.asStateFlow()

    val hasActiveSession: Boolean
        get() = when (val it = _chargingStatus.value) {
            is ChargingStatus.Success -> it.session.isActive
            else -> false
        }

    private suspend fun getSessionDetails(sessionId: String) {
        Log.i(TAG, "getSessionDetails sessionId $sessionId")
        val result = chargingRepository.sessionDetails(sessionId)
        updateSession(result)
    }

    private suspend fun updateSession(result: ChargingStatus) {
        when (result) {
            is ChargingStatus.Success -> {
                _chargingStatus.value = result
                session.value = result.session.apply {
                    currency = userRepository.getCurrency()
                }
                if (hasActiveSession.not()) {
                    _chargingStatus.value = ChargingStatus.Stop(result.session)
                    refreshUserBalance()
                } else {
                    initChargingTimer(result.session)
                }
            }

            else -> {
                _chargingStatus.value = result
            }
        }
    }


    fun stopCharging() {
        Log.v(TAG, "stopCharging")
        val session = session.value ?: return
        // Prevent calling stop charging multiple times.
        if (session.id == pendingSessionId) return
        pendingSessionId = session.id

        pusherManager.unsubscribeSessionConsumption(session.id)
        pusherManager.unsubscribeSessionCompletion(session.id)
        pusherManager.disconnectIfIdle()

        viewModelScope.launch {
            _chargingStatus.emit(ChargingStatus.Loading)
            chargingRepository.stopCharging(
                orderId = session.id,
                chargePointId = session.chargePointId,
                connector = session.connectorNumber
            )
            _chargingStatus.emit(ChargingStatus.Stop(session))
            refreshUserBalance()
            pendingSessionId = ""
        }
    }

    private fun initChargingTimer(session: Session) {
        Log.i(TAG, "UpdateChargingTime")
        chargingTimerManager.initTimer(
            isFull = session.isFull,
            chargingTime = session.chargingTime(),
            timeShift = session.elapsedTime,
            onUpdate = { elapsed, remaining ->
                Log.v(TAG, "elapsed = $elapsed s")
                timerState.update(elapsed, remaining)
            },
            onDone = {
                Log.v(TAG, "onDone")
                // Stop charging when the timer is completed after n seconds.
                viewModelScope.launch {
                    delay(10 * 1000)
                    stopCharging()
                }
            }
        )
    }

    suspend fun initSocketEvent(sessionId: String) {
        Log.i(TAG, "initSocketEvent - $sessionId")
        pusherManager.connect()
        pusherManager.subscribeConsumption(
            sessionId = sessionId,
            onUpdate = {
                Log.i(TAG, "onReceiveConsumption - $it")
                viewModelScope.launch {
                    updateSession(ChargingStatus.Success(it))
                }
            }
        )
        pusherManager.subscribeSessionCompletion(sessionId)
        viewModelScope.launch {
            pusherManager.sessionCompletionFlow.collect { session ->
                if (session != null && session.id == sessionId) {
                    Log.i(TAG, "sessionCompletionFlow - $session")
                    _chargingStatus.emit(ChargingStatus.Stop(session))
                    refreshUserBalance()
                }
            }
        }
    }

    fun initSession() {
        viewModelScope.launch {
            getSessionDetails(orderId)
            initSocketEvent(orderId)
        }
    }

    fun release() {
        chargingTimerManager.release()
        pusherManager.unsubscribeSessionConsumption(orderId)
    }

    private fun refreshUserBalance() {
        viewModelScope.launch {
            userRepository.getUserDetails()
        }
    }

    companion object {
        private const val TAG = "ChargeViewModel"
    }
}
