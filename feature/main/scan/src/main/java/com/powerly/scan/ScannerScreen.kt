package com.powerly.scan

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
import org.koin.androidx.compose.koinViewModel
import com.powerly.core.data.model.SourceStatus
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.PowerSource
import com.powerly.core.model.powerly.SourceCategory
import com.powerly.lib.CONSTANTS
import com.powerly.resources.R
import com.powerly.ui.dialogs.loading.LoadingState
import com.powerly.ui.dialogs.loading.rememberBasicScreenState
import com.powerly.ui.dialogs.message.MessageState
import com.powerly.ui.util.rememberPermissionsState
import kotlinx.coroutines.launch

private const val TAG = "ScannerScreen"

/**
 * Displays the Scanner screen, allowing users to scan QR codes to access power sources.
 *
 * This composable function presents a QR code scanner and handles the scanning process.
 * It requests camera permission and displays the scanner if granted. Upon scanning a valid QR code,
 * it retrieves power source details, navigates to the power source details screen, and then navigates back.
 *
 * @param viewModel The `ScannerViewModel` instance handling the scanning logic.
 * @param openPowerSource A lambda function to navigate to a power source details screen with the scanned `PowerSource`.
 * @param onBack A lambda function to navigate back from the screen.
 */
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

    /**
     * Handles the scanning of a power source token.
     *
     * This function initiates a coroutine to retrieve power source details using the provided identifier.
     * It displays a progress dialog while fetching the details and cancels it upon completion or error.
     * If the details are successfully retrieved, it navigates to the power source details screen and then back.
     * If an error occurs, it displays a toast message and navigates back.
     *
     * @param identifier The identifier of the power source to scan.
     */
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
            if (code.contains(CONSTANTS.POWER_SOURCE_ID)) {// for full link power source tokens
                scanner.release()
                val uri = code.toUri()
                //extract token from power source link
                uri.getQueryParameter(CONSTANTS.POWER_SOURCE_ID)?.let { identifier ->
                    Log.i(TAG, "identifier = $identifier")
                    onScanPowerSourceToken(identifier)
                }
            } else if (listOf( // for short power source tokens
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
