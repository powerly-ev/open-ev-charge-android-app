package com.powerly.charge

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.powerly.charge.util.ChargingTimerManager
import com.powerly.charge.util.ChargingTimerState
import com.powerly.core.data.model.ChargingStatus
import com.powerly.core.data.repositories.SessionsRepository
import com.powerly.core.data.repositories.UserRepository
import com.powerly.core.model.powerly.Session
import com.powerly.lib.AppRoutes
import com.powerly.lib.managers.PusherManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class ChargeViewModel(
    private val sessionsRepository: SessionsRepository,
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


    /**
     * Retrieves the details of a charging session using the provided session ID.
     *
     */
    private suspend fun getSessionDetails(sessionId: String) {
        Log.i(TAG, "getSessionDetails sessionId $sessionId")
        val result = sessionsRepository.sessionDetails(sessionId)
        updateSession(result)
    }

    /**
     * Updates the charging session with the provided result.
     *
     * @param result The charging status result to update the session with.
     */
    private suspend fun updateSession(result: ChargingStatus) {
        when (result) {
            is ChargingStatus.Success -> {
                _chargingStatus.value = result
                session.value = result.session.apply {
                    currency = userRepository.getCurrency()
                }
                // Handle cases where the charging session has ended prematurely
                if (hasActiveSession.not()) {
                    _chargingStatus.value = ChargingStatus.Stop(result.session)
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
        // to prevent calling stop charging multiple times..
        if (session.id == pendingSessionId) return
        pendingSessionId = session.id

        pusherManager.unbindConsumption(session.id)
        pusherManager.unsubscribeSession(session.id)
        pusherManager.disconnect()

        viewModelScope.launch {
            _chargingStatus.emit(ChargingStatus.Loading)
            sessionsRepository.stopCharging(
                orderId = session.id,
                chargePointId = session.chargePointId,
                connector = session.connectorNumber
            )
            _chargingStatus.emit(ChargingStatus.Stop(session))
            pendingSessionId = ""
        }
    }

    /**
     * Initializes the charging timer and sets up the charging session.
     *
     * This function is called when a new charging session is started.
     * It initializes the charging timer, handles session status updates, and
     * triggers actions when the charging session is completed or stopped.
     *
     * @param session The current charging session.
     */
    private fun initChargingTimer(session: Session) {
        Log.i(TAG, "UpdateChargingTime")
        // Initialize the charging timer
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
                // Stop charging when the timer is completed after n seconds
                viewModelScope.launch {
                    delay(10 * 1000)
                    stopCharging()
                }
            }
        )
    }

    /**
     * Initializes the socket event listener for session updates and completion.
     *
     * This function connects to Pusher, subscribes to session events for the given sessionId,
     * and handles real-time updates for session consumption and completion.
     *
     */
    suspend fun initSocketEvent(sessionId: String) {
        Log.i(TAG, "initSocketEvent - $sessionId")
        pusherManager.connect()
        // subscribe in session consumption/charging event
        // collect result in local coroutine scope
        pusherManager.subscribeConsumption(
            sessionId = sessionId,
            onUpdate = {
                Log.i(TAG, "onReceiveConsumption - $it")
                viewModelScope.launch {
                    updateSession(ChargingStatus.Success(it))
                }
            }
        )

        delay(5000)
        // subscribe in session completion socket event
        // and collect response from any screen with [PusherManager.sessionCompletionFlow]
        pusherManager.subscribeSessionCompletion(sessionId)
        viewModelScope.launch {
            pusherManager.sessionCompletionFlow.collect { session ->
                if (session != null && session.id == sessionId) {
                    Log.i(TAG, "sessionCompletionFlow - $session")
                    _chargingStatus.emit(ChargingStatus.Stop(session))
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
        pusherManager.unbindConsumption(orderId)
    }

    companion object {
        private const val TAG = "ChargeViewModel"
    }
}