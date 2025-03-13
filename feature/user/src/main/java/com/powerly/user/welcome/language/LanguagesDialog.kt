package com.powerly.user.welcome.language

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.powerly.ui.dialogs.languages.LanguagesDialogContent


@Composable
internal fun LanguagesDialog(
    viewModel: LanguagesViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val activity = LocalActivity.current
    LanguagesDialogContent(
        selectedLanguage = viewModel.language,
        onSelect = {
            viewModel.changeAppLanguage(
                lang = it,
                activity = activity!!
            )
        },
        onDismiss = onDismiss
    )
}

