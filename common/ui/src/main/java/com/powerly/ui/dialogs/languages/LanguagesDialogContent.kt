package com.powerly.ui.dialogs.languages


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.powerly.resources.R
import com.powerly.ui.containers.MySurfaceRow
import com.powerly.ui.components.MyIcon
import com.powerly.ui.dialogs.MyBottomSheet
import com.powerly.ui.dialogs.MyDialogState
import com.powerly.ui.dialogs.rememberMyDialogState
import com.powerly.ui.screen.DialogHeader
import com.powerly.ui.theme.AppTheme

/**
 * please start interactive mode to show bottom sheet preview
 */
@Preview
@Composable
private fun AppLanguageDialogPreview() {
    AppTheme {
        LanguagesDialogContent(
            selectedLanguage = "ar",
            state = rememberMyDialogState(visible = true),
            onSelect = {}
        )
    }
}


@Composable
fun LanguagesDialogContent(
    state: MyDialogState? = null,
    selectedLanguage: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit = {}
) {
    val languages = stringArrayResource(R.array.available_languages)
    val codes = stringArrayResource(R.array.available_language_codes)
    var selected by remember { mutableStateOf(selectedLanguage) }

    MyBottomSheet(
        state = state,
        modifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 10.dp
        ),
        spacing = 8.dp,
        background = Color.White,
        header = {
            DialogHeader(
                title = stringResource(R.string.select_language),
                layoutDirection = LayoutDirection.Rtl,
                onClose = {
                    onDismiss()
                    state?.dismiss()
                }
            )
        },
        onDismiss = onDismiss
    ) {
        languages.forEachIndexed { index, language ->
            val languageCode = codes[index]
            ItemOptionsMenu(
                title = language,
                selected = { languageCode == selected },
                onSelect = {
                    selected = languageCode
                    onSelect(languageCode)
                }
            )
        }
    }
}

@Composable
private fun ItemOptionsMenu(
    title: String,
    padding: PaddingValues = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    background: Color = MaterialTheme.colorScheme.surface,
    selected: () -> Boolean,
    onSelect: () -> Unit
) {
    MySurfaceRow(
        modifier = modifier.padding(padding),
        onClick = onSelect,
        color = background,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (selected()) MyIcon(
            icon = R.drawable.ic_true,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        ) else Spacer(Modifier.size(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f),
            textAlign = textAlign
        )
    }
}