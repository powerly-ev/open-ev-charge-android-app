package com.powerly.account.profile.dialogs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.resources.R
import com.powerly.ui.dialogs.ItemOptionsMenu
import com.powerly.ui.dialogs.MyDropdownMenu
import com.powerly.ui.theme.AppTheme

/**
 * please start interactive mode to show bottom sheet preview
 */
@Preview
@Composable
private fun ProfileDropMenuPreview() {
    AppTheme {
        Box(
            contentAlignment = Alignment.CenterEnd,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ProfileDropMenu(
                show = { true },
                onDismiss = {},
                onDeleteAccount = {},
            )
        }
    }
}

@Composable
internal fun ProfileDropMenu(
    show: () -> Boolean,
    onDismiss: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    MyDropdownMenu(
        expanded = show,
        onDismiss = onDismiss
    ) {
        ItemOptionsMenu(
            title = stringResource(R.string.delete_account),
            onClick = onDeleteAccount
        )
    }
}
