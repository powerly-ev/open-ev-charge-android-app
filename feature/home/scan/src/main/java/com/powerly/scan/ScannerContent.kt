package com.powerly.scan

import android.view.View
import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import com.powerly.resources.R
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.util.OnLifecycleEvent
import com.powerly.ui.components.ButtonText
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.extensions.isPreview
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.powerly.ui.screen.DialogHeader

private const val TAG = "ScannerScreen"

@Preview
@Composable
private fun ScannerScreenPreview() {
    AppTheme {
        ScannerScreenContent(
            showScanner = { true },
            onRequestCamera = {},
            title = R.string.scan_qr_code,
            onScan = { _, _ -> }
        )
    }
}


@Composable
fun ScannerScreenContent(
    screenState: ScreenState = rememberScreenState(),
    @StringRes title: Int,
    showScanner: () -> Boolean,
    onRequestCamera: () -> Unit,
    onScan: (DecoratedBarcodeView?, String) -> Unit,
) {
    MyScreen(
        header = {
            DialogHeader(
                title = stringResource(id = title),
                closeIcon = null
            )
        },
        screenState = screenState
    ) {
        Box(
            modifier = Modifier.background(
                MaterialTheme.colorScheme.background
            )
        ) {
            if (showScanner()) SectionScanner(
                onScan = onScan
            )
            else SectionCameraPermission(
                onRequestCamera = onRequestCamera
            )
        }
    }
}

@Composable
private fun SectionScanner(
    onScan: (DecoratedBarcodeView?, String) -> Unit
) {
    var flashOn by remember { mutableStateOf(false) }
    var barcodeView by remember {
        mutableStateOf<DecoratedBarcodeView?>(null)
    }
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                barcodeView.release()
                flashOn = false
            }

            Lifecycle.Event.ON_RESUME -> {
                barcodeView?.resume()
            }

            else -> {}
        }
    }

    Box {
        val preview = isPreview()
        AndroidView(
            factory = { context ->
                DecoratedBarcodeView(context).apply {
                    if (preview.not()) barcodeView = this
                    statusView.visibility = View.GONE
                    viewFinder.setLaserVisibility(false)
                    viewFinder.setMaskColor(android.R.color.transparent)

                    val formats = listOf(BarcodeFormat.QR_CODE)
                    decoderFactory = DefaultDecoderFactory(formats)
                    decodeContinuous { result ->
                        result.text?.trim()?.let { code ->
                            onScan(barcodeView, code)
                        }
                    }
                }
            }
        )

        QrCodeFrame(
            color = Color.White,
            stroke = 10.dp,
            modifier = Modifier
                .padding(48.dp)
                .zIndex(2f)
                .align(Alignment.Center)
        )

        ButtonFlash(
            flashOn = { flashOn },
            onToggle = {
                flashOn = flashOn.not()
                if (flashOn) barcodeView?.setTorchOn()
                else barcodeView?.setTorchOff()
            }
        )
    }
}

@Composable
private fun BoxScope.ButtonFlash(
    flashOn: () -> Boolean,
    onToggle: () -> Unit
) {
    Icon(
        modifier = Modifier
            .padding(bottom = 24.dp)
            .size(45.dp)
            .align(Alignment.BottomCenter)
            .zIndex(2f)
            .clickable(onClick = onToggle),
        painter = painterResource(
            id = if (flashOn()) R.drawable.flash_on
            else R.drawable.flash_off
        ),
        contentDescription = "",
        tint = Color.White
    )
}

@Composable
private fun SectionCameraPermission(
    onRequestCamera: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        ButtonText(
            text = stringResource(id = R.string.camera_permission_message),
            background = MaterialTheme.colorScheme.secondary,
            color = MyColors.white,
            onClick = onRequestCamera
        )
    }
}

@Composable
private fun QrCodeFrame(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary,
    stroke: Dp = 10.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {

        val s = size.width
        val length = s / 3

        drawArc(
            topLeft = Offset(0f, 0f),
            color = color,
            size = Size(length, length),
            startAngle = 180f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(stroke.value)
        )
        drawArc(
            topLeft = Offset(s - length, 0f),
            color = color,
            size = Size(length, length),
            startAngle = 270f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(stroke.value)
        )

        drawArc(
            topLeft = Offset(0f, s - length),
            color = color,
            size = Size(length, length),
            startAngle = 90f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(stroke.value)
        )

        drawArc(
            topLeft = Offset(s - length, s - length),
            color = color,
            size = Size(length, length),
            startAngle = 360f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(stroke.value)
        )
    }
}

fun DecoratedBarcodeView?.release() {
    this?.pause()
    this?.setTorchOff()
}
