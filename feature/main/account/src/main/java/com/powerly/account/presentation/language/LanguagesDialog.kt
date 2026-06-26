package com.powerly.account.presentation.language

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.powerly.ui.dialogs.languages.LanguagesDialogContent
import com.powerly.ui.dialogs.loading.LoadingDialog
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun LanguagesDialog(
    viewModel: LanguagesViewModel = koinViewModel(),
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val screenState = remember { viewModel.screenState }
    val selectedLanguage by viewModel.language.collectAsState("en")

    LoadingDialog(state = screenState.loadingState!!)

    LanguagesDialogContent(
        selectedLanguage = selectedLanguage,
        onSelect = { newLang ->
            if (newLang == selectedLanguage) onDismiss()
            else coroutineScope.launch {
                viewModel.updateAppLanguage(lang = newLang, onDismiss = onDismiss)
            }
        },
        onDismiss = onDismiss
    )
}
