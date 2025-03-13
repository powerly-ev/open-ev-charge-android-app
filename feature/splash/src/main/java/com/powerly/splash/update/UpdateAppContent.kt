package com.powerly.splash.update

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.resources.R
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.components.MyIcon
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.extensions.asPadding
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors

@Preview
@Composable
private fun UpdateAppScreenPreview() {
    AppTheme {
        UpdateAppScreenContent(
            appVersion = "Powerly 0.1.4",
            onUpdate = {}
        )
    }
}

@Composable
internal fun UpdateAppScreenContent(
    appVersion: String,
    onUpdate: () -> Unit
) {
    MyScreen(
        header = {
            ScreenHeader(
                title = stringResource(R.string.update_required),
                closeIcon = null,
                showDivider = true
            )
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        spacing = 16.dp,
        modifier = Modifier.padding(16.dp),
        footerPadding = 16.dp.asPadding,
        footer = {
            ButtonLarge(
                text = stringResource(R.string.update_now),
                modifier = Modifier.fillMaxWidth(),
                onClick = onUpdate
            )
        }
    ) {
        MyIcon(
            icon = R.drawable.ic_alert,
            modifier = Modifier.size(150.dp),
            tint = MyColors.red
        )
        Text(
            text = appVersion,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.update_force_desc1),
            color = MaterialTheme.colorScheme.tertiary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(0.80f),
            textAlign = TextAlign.Justify
        )
        Text(
            text = stringResource(R.string.update_force_desc2),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}