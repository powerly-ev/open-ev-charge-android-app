package com.powerly.scan.presentation

import android.Manifest
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import com.powerly.core.domain.model.SourceStatus
import com.powerly.core.domain.model.ApiStatus
import com.powerly.core.model.powerly.PowerSource
import com.powerly.core.model.powerly.SourceCategory
import com.powerly.navigation.CONSTANTS
import com.powerly.resources.R
import com.powerly.ui.dialogs.loading.LoadingState
import com.powerly.ui.dialogs.loading.rememberBasicScreenState
import com.powerly.ui.dialogs.message.MessageState
import com.powerly.ui.util.rememberPermissionsState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

private const val TAG = "ScannerScreen"

@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = koinViewModel(),
    openPowerSource: (PowerSource) -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val screenState = rememberBasicScreenState(LoadingState(), MessageState())
    var showScanner by rememberSaveable { mutableStateOf(false) }
    val permissions = rememberPermissionsState(Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (permissions.isGranted()) {
            showScanner = true
        } else permissions.request { granted ->
            showScanner = granted
        }
    }

    fun onScanPowerSourceToken(identifier: String) {
        coroutineScope.launch {
            viewModel.powerSourceDetails(identifier).collect {
                screenState.loading = it is ApiStatus.Loading
                when (it) {
                    is SourceStatus.Success -> {
                        openPowerSource(it.powerSource)
                        onBack()
                    }

                    is SourceStatus.Error -> {
                        screenState.showMessage(it.msg) {
                            onBack()
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    ScannerScreenContent(
        title = R.string.scan_qr_code,
        screenState = screenState,
        showScanner = { showScanner },
        onRequestCamera = {
            permissions.request { granted ->
                if (granted) {
                    showScanner = true
                } else permissions.requestManually {
                    showScanner = it
                }
            }
        },
        onScan = { scanner, code ->
            Log.v(TAG, "code = $code")
            if (code.contains(CONSTANTS.POWER_SOURCE_ID)) {
                scanner.release()
                val uri = code.toUri()
                uri.getQueryParameter(CONSTANTS.POWER_SOURCE_ID)?.let { identifier ->
                    Log.i(TAG, "identifier = $identifier")
                    onScanPowerSourceToken(identifier)
                }
            } else if (listOf(
                    SourceCategory.EV_CHARGER.name,
                    SourceCategory.SMART_METER.name,
                    SourceCategory.SMART_PLUG.name
                ).any { code.startsWith(it) }
            ) {
                scanner.release()
                onScanPowerSourceToken(code)
            } else {
                scanner.release()
                onBack()
            }
        }
    )
}
