package com.powerly.account.invite

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
import org.koin.androidx.compose.koinViewModel
import com.github.alexzhirkevich.customqrgenerator.QrData
import com.github.alexzhirkevich.customqrgenerator.vector.QrCodeDrawable
import com.powerly.resources.R
import com.powerly.ui.extensions.safeStartActivity

/**
 * Composable function that displays the Invite screen.
 *
 * This screen allows users to share the app with others via a referral link.
 * It displays a QR code representing the referral link and provides options
 * to share the link or copy it to the clipboard.
 */
@Composable
internal fun InviteScreen(
    viewModel: InviteViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    // State to hold the QR code drawable
    var drawable by remember { mutableStateOf<Drawable?>(null) }
    // LaunchedEffect to generate the QR code once
    LaunchedEffect(Unit) {
        val data = QrData.Url(viewModel.appLink)
        drawable = QrCodeDrawable(data)
    }

    /**
     * Creates a share intent to allow the user to share app link with others.
     *
     */
    fun shareApp() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
            //val message = context.getString(R.string.invite_message, appLink)
            putExtra(Intent.EXTRA_TEXT, viewModel.appLink)

        }
        context.safeStartActivity(Intent.createChooser(shareIntent, null))
    }

    /**
     * Copies the referral link to the system clipboard.
     */
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