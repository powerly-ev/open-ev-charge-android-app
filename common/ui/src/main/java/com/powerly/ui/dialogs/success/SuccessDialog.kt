package com.powerly.ui.dialogs.success

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.powerly.ui.R


@Preview
@Composable
fun SuccessScreenPreview() {
    val state = rememberSuccessState(visible = true)
    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            SuccessDialog(
                state = state,
                onDone = {}
            )
        }
    }
}


private val dialogSize = 140.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessDialog(
    state: SuccessState = rememberSuccessState(),
    onDone: () -> Unit = {}
) {
    if (state.isVisible) {
        Popup(
            alignment = Alignment.Center,
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                color = Color.Transparent,
                modifier = Modifier.size(dialogSize)
            ) {
                SuccessAnimation(
                    onDone = {
                        state.dismiss()
                        onDone()
                    }
                )
            }
        }
    }
}


@Composable
private fun SuccessAnimation(
    modifier: Modifier = Modifier,
    onDone: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.lotti_anim)
    )
    val animationState by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        isPlaying = true
    )
    // Detect when animation is finished
    LaunchedEffect(key1 = Unit) {
        snapshotFlow { animationState }.collect {
            if (it == 1f) onDone()
        }
    }
    LottieAnimation(
        modifier = Modifier
            .size(dialogSize)
            .then(modifier),
        composition = composition,
        progress = { animationState },
    )

}