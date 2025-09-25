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
import com.powerly.core.model.powerly.Session
import com.powerly.lib.AppRoutes
import com.powerly.lib.managers.StorageManager
import org.koin.android.annotation.KoinViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@KoinViewModel
class ChargeViewModel (
    private val sessionsRepository: SessionsRepository,
    private val chargingTimerManager: ChargingTimerManager,
    private val storageManager: StorageManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val charger = savedStateHandle.toRoute<AppRoutes.PowerSource.Charging>()

    val session = mutableStateOf<Session?>(null)
    private var pendingSessionId: String = ""
    private var lastSessionUpdateTime: Int = 0

    private val _chargingStatus = MutableStateFlow<ChargingStatus>(ChargingStatus.Loading)
    val chargingStatus = _chargingStatus.asStateFlow()

    val hasActiveSession: Boolean
        get() = when (val it = _chargingStatus.value) {
            is ChargingStatus.Success -> it.session.isActive
            else -> false
        }

    val timerState = ChargingTimerState(0, 0)

    suspend fun startCharging() {
        val chargePointId = charger.chargePointId
        val quantity = charger.quantity
        val sessionId = session.value?.id ?: charger.orderId
        // Update session details on-resume to sync with the server
        if (sessionId.isNotEmpty()) {
            updateSessionDetails(sessionId)
        }
        // Start charging when session_id is null
        else {
            Log.v(TAG, "charging cp = $chargePointId, quantity = $quantity")
            val result = sessionsRepository.startCharging(
                chargePointId = chargePointId,
                quantity = quantity,
                connector = charger.connector
            )
            initSession(result)
        }
    }

    private suspend fun updateSessionDetails(sessionId: String) {
        Log.i(TAG, "updateSessionDetails sessionId $sessionId")
        val result = sessionsRepository.sessionDetails(sessionId)
        initSession(result)
    }

    private fun initSession(result: ChargingStatus) {
        when (result) {
            is ChargingStatus.Success -> {
                _chargingStatus.value = result
                session.value = result.session.apply {
                    currency = storageManager.currency
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
        val sessionId = session.value?.id
        // to prevent calling stop charging multiple times..
        if (sessionId == null || sessionId == pendingSessionId) return
        pendingSessionId = sessionId

        viewModelScope.launch {
            _chargingStatus.emit(ChargingStatus.Loading)
            val result = sessionsRepository.stopCharging(
                orderId = sessionId,
                chargePointId = charger.chargePointId,
                connector = charger.connector
            )
            _chargingStatus.emit(result)
            pendingSessionId = ""
        }
    }

    private suspend fun forceStopCharging() {
        val session = session.value ?: return
        Log.i(TAG, "forceStopCharging - ${session.id}")
        _chargingStatus.emit(ChargingStatus.Loading)
        sessionsRepository.stopCharging(
            orderId = session.id,
            chargePointId = charger.chargePointId,
            connector = charger.connector
        )
        _chargingStatus.emit(ChargingStatus.Stop(session))
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
        Log.i(TAG, "initCharging")
        // Initialize the charging timer
        chargingTimerManager.initTimer(
            isFull = session.isFull,
            chargingTime = session.chargingTime(),
            timeShift = session.elapsedTime,
            onUpdate = { elapsed, remaining ->
                Log.v(TAG, "elapsed = $elapsed s")
                timerState.update(elapsed, remaining)
                // update session details from server each 120 seconds
                if (elapsed > 0 && elapsed % 120 == 0 && elapsed != lastSessionUpdateTime) {
                    lastSessionUpdateTime = elapsed
                    Log.v(TAG, "sessionUpdateTime - $lastSessionUpdateTime")
                    viewModelScope.launch {
                        updateSessionDetails(session.id)
                    }
                }
            },
            onDone = {
                viewModelScope.launch {
                    delay(5 * 1000)
                    forceStopCharging()
                }
            }
        )
    }

    fun release() {
        chargingTimerManager.release()
    }

    companion object {
        private const val TAG = "ChargeViewModel"
    }
}