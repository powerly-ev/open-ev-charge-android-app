package com.powerly.payment.balance.withdraw

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.koinViewModel
import com.powerly.core.model.api.ApiStatus
import com.powerly.resources.R
import com.powerly.ui.dialogs.alert.AlertDialogProperties
import com.powerly.ui.dialogs.alert.MyAlertDialog
import com.powerly.ui.dialogs.alert.rememberAlertDialogState
import com.powerly.ui.dialogs.rememberMyDialogState
import com.powerly.ui.dialogs.webview.WebViewDialog
import com.powerly.ui.dialogs.webview.WebViewPage
import kotlinx.coroutines.launch

/**
 * A Composable function that displays the Withdraw Screen.
 *
 * @param viewModel The WithdrawViewModel instance.
 * @param onClose A callback function that is invoked when the screen is closed.
 */
@Composable
internal fun WithdrawScreen(
    viewModel: WithdrawViewModel = koinViewModel(),
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val paymentDialog = rememberAlertDialogState()
    val screenState = remember { viewModel.screenState }
    val wallets = remember { viewModel.wallets }
    val currency = remember { viewModel.currency }
    var webViewPage by remember { mutableStateOf<WebViewPage?>(null) }
    var webViewDialog = rememberMyDialogState()

    // Fetch the list of wallets when the screen is launched.
    LaunchedEffect(Unit) {
        viewModel.loadWallets()
    }

    /**
     * Initiates the withdrawal process.
     */
    fun withdraw() {
        coroutineScope.launch {
            viewModel.walletPayout().collect {
                screenState.loading = it is ApiStatus.Loading
                when (it) {
                    is ApiStatus.Error -> paymentDialog.show(
                        title = context.getString(R.string.wallet_withdraw_failed),
                        message = it.msg.msg
                    )

                    is ApiStatus.Success -> paymentDialog.show(
                        title = context.getString(R.string.wallet_withdraw_successful),
                        message = it.data.msg
                    )

                    else -> {}
                }
            }
        }
    }

    /**
     * Fetches the user agreements based on the provided agreement type and country ID.
     *
     * @param type The type of user agreement to fetch.
     */
    fun getUserAgreement(type: Int) {
        coroutineScope.launch {
            val url = viewModel.getUserAgreementLink(type) ?: return@launch
            val title = if (type == 1) {
                context.getString(R.string.wallet_policy)
            } else {
                context.getString(R.string.wallet_policy_terms)
            }
            webViewPage = WebViewPage(
                url = url,
                title = title
            )
            webViewDialog.show()
        }
    }

    WebViewDialog(
        state = webViewDialog,
        webViewPage = { webViewPage!! }
    )

    MyAlertDialog(
        state = paymentDialog,
        positiveButton = stringResource(R.string.dialog_ok),
        properties = AlertDialogProperties(
            type = AlertDialogProperties.MODAL_DIALOG
        )
    )

    // Display the Withdraw Screen content.
    WithdrawScreenContent(
        screenState = screenState,
        wallets = { wallets },
        currency = currency,
        uiEvents = {
            when (it) {
                WithdrawEvents.Close -> onClose()
                WithdrawEvents.PrivacyPolicy -> getUserAgreement(1)
                WithdrawEvents.TermsOfService -> getUserAgreement(2)
                WithdrawEvents.Withdraw -> withdraw()
            }
        }
    )
}