package com.powerly.account.language

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import org.koin.androidx.compose.koinViewModel
import com.powerly.ui.dialogs.languages.LanguagesDialogContent
import com.powerly.ui.dialogs.loading.LoadingDialog
import kotlinx.coroutines.launch

/**
 * Displays a dialog allowing the user to change the app's language.
 *
 * This composable function presents a dialog that enables users to select a new language for the application.
 * It interacts with a `LanguagesViewModel` to update user details and app information based on the selected language.
 */
@Composable
internal fun LanguagesDialog(
    viewModel: LanguagesViewModel = koinViewModel(),
    onDismiss: () -> Unit
) {
    val activity = LocalActivity.current!!
    val coroutineScope = rememberCoroutineScope()
    val screenState = remember { viewModel.screenState }
    val selectedLanguage = remember { viewModel.selectedLanguage }


    LoadingDialog(state = screenState.loadingState!!)

    LanguagesDialogContent(
        selectedLanguage = selectedLanguage,
        onSelect = { newLang ->
            if (newLang == selectedLanguage) onDismiss()
            else coroutineScope.launch {
                viewModel.updateAppLanguage(
                    activity = activity,
                    lang = newLang,
                    onDismiss = onDismiss
                )
            }
        },
        onDismiss = onDismiss
    )

}