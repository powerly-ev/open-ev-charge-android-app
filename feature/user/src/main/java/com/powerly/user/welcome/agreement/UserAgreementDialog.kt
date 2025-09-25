package com.powerly.user.welcome.agreement

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.koinViewModel
import com.powerly.resources.R
import com.powerly.ui.dialogs.MyProgressView
import com.powerly.ui.dialogs.webview.WebViewPage
import com.powerly.ui.dialogs.webview.WebViewScreen
import kotlinx.coroutines.launch

private const val TAG = "UserAgreementDialog"

@Composable
internal fun UserAgreementDialog(
    viewModel: UserAgreementViewModel = koinViewModel(),
    type: Int,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var webViewPage by remember { mutableStateOf<WebViewPage?>(null) }

    /**
     * Fetches the user agreements based on the provided agreement type and country ID.
     *
     * @param type The type of user agreement to fetch.
     */
    fun getUserAgreement(type: Int) {
        coroutineScope.launch {
            val title = if (type == 1) {
                context.getString(R.string.welcome_privacy_policy)
            } else {
                context.getString(R.string.welcome_terms_condition)
            }
            val url = viewModel.getUserAgreementLink(type).orEmpty()
            webViewPage = WebViewPage(
                url = url,
                title = title
            )
        }
    }

    LaunchedEffect(Unit) {
        getUserAgreement(type)
    }

    BackHandler(enabled = true, onBack = {})

    if (webViewPage != null) WebViewScreen(
        url = webViewPage!!.url,
        title = webViewPage!!.title,
        onClose = onDismiss
    ) else MyProgressView()

}