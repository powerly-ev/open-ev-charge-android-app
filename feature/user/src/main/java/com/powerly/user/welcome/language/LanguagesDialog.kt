package com.powerly.user.welcome.language

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.powerly.ui.dialogs.languages.LanguagesDialogContent
import org.koin.androidx.compose.koinViewModel


@Composable
internal fun LanguagesDialog(
    viewModel: LanguagesViewModel = koinViewModel(),
    onDismiss: () -> Unit
) {
    val language by viewModel.language.collectAsState(initial = "en")
    LanguagesDialogContent(
        selectedLanguage = language,
        onSelect = {
            viewModel.changeAppLanguage(lang = it)
            onDismiss()
        },
        onDismiss = onDismiss
    )
}

