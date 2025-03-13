package com.powerly.ui.dialogs.webview

import androidx.compose.runtime.Composable
import com.powerly.ui.dialogs.MyDialogState
import com.powerly.ui.dialogs.MyScreenBottomSheet

@Composable
fun WebViewDialog(
    webViewPage: () -> WebViewPage,
    state: MyDialogState
) {
    MyScreenBottomSheet(state = state) {
        val page = webViewPage()
        WebViewScreen(
            url = page.url,
            title = page.title,
            onClose = { state.dismiss() }
        )
    }
}

data class WebViewPage(
    val url: String,
    val title: String
)