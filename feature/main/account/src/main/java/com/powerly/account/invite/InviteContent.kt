package com.powerly.account.invite

import android.graphics.drawable.Drawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.powerly.resources.R
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.extensions.isPreview
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors

@Preview
@Composable
private fun InviteScreenPreview() {
    val context = LocalContext.current
    val drawable: Drawable? = ContextCompat.getDrawable(
        context,
        R.drawable.scan
    )
    AppTheme {
        InviteScreenContent(
            qrDrawable = { drawable },
            onClose = {},
            onShare = {},
            onCopyLink = {}
        )
    }
}

@Composable
internal fun InviteScreenContent(
    qrDrawable: () -> Drawable?,
    onCopyLink: () -> Unit,
    onShare: () -> Unit,
    onClose: () -> Unit
) {
    MyScreen(
        modifier = Modifier.padding(16.dp),
        verticalScroll = true,
        header = {
            ScreenHeader(
                title = stringResource(R.string.invite),
                onClose = onClose,
                showDivider = true
            )
        }
    ) {
        SectionQrCode(qrDrawable)
        Text(
            text = stringResource(R.string.invite_QR_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        SectionButtons(onShare, onCopyLink)
    }
}

@Composable
private fun ColumnScope.SectionQrCode(qrDrawable: () -> Drawable?) {
    val drawable = qrDrawable() ?: return
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
    ) {
        Image(
            painter = if (isPreview()) painterResource(R.drawable.scan)
            else rememberAsyncImagePainter(model = drawable),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(0.8f)
                .aspectRatio(1f)
                .align(Alignment.Center),
            contentDescription = ""
        )
    }
}

@Composable
private fun SectionButtons(
    onShare: () -> Unit,
    onCopyLink: () -> Unit
) {
    ButtonLarge(
        text = stringResource(R.string.share_code),
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        icon = R.drawable.ic_share,
        layoutDirection = LayoutDirection.Rtl,
        onClick = onShare
    )
    var linkCopied by remember { mutableStateOf(false) }
    ButtonLarge(
        text = stringResource(
            if (linkCopied) R.string.invite_link_copied
            else R.string.copy_code
        ),
        color = if (linkCopied) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.secondary,
        enabled = { linkCopied.not() },
        layoutDirection = LayoutDirection.Rtl,
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 16.dp,
        icon = R.drawable.ic_copy,
        border = BorderStroke(2.dp, color = MyColors.borderColor),
        background = Color.White,
        onClick = {
            linkCopied = true
            onCopyLink()
        }
    )
}