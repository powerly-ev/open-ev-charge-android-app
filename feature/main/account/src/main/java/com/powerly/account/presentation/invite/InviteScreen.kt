package com.powerly.account.presentation.invite

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.powerly.resources.R
import com.powerly.ui.theme.LocalMainActivity
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun InviteScreen(
    viewModel: InviteViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val activity = LocalMainActivity.current
    var drawable by remember { mutableStateOf<Drawable?>(null) }
    LaunchedEffect(Unit) {
        val data = QrData.Url(viewModel.appLink)
        drawable = QrCodeDrawable(data)
    }

    fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            putExtra(Intent.EXTRA_TEXT, viewModel.appLink)
        }
        try {
            activity?.startActivity(Intent.createChooser(shareIntent, null))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun copyReferralLink() {
        try {
            val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip =
                ClipData.newPlainText(context.getString(R.string.app_name), viewModel.appLink)
            clipboard.setPrimaryClip(clip)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
    }

    InviteScreenContent(
        qrDrawable = { drawable },
        onShare = ::shareApp,
        onCopyLink = ::copyReferralLink,
        onClose = onBack
    )
}
