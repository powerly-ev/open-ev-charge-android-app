package com.powerly.splash

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.request.ImageRequest
import coil.request.onAnimationEnd
import coil.request.onAnimationStart
import coil.request.repeatCount
import coil.size.Size
import com.powerly.resources.R
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.extensions.isPreview
import com.powerly.ui.theme.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Preview
@Composable
private fun SplashScreenPreview() {
    AppTheme {
        SplashScreenContent(
            onLogoLoaded = {}
        )
    }
}

@Composable
internal fun SplashScreenContent(
    onLogoLoaded: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    // show slogan by default for preview mode
    val preview = isPreview()
    var showSlogan by remember { mutableStateOf(preview) }

    Scaffold(
        containerColor = Color.White
    ) {
        Box(
            Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GifImage(
                    gif = R.drawable.splash_animation,
                    placeholder = R.drawable.logo,
                    modifier = Modifier.size(200.dp),
                    onLoaded = onLogoLoaded,
                    onStarted = {
                        coroutineScope.launch {
                            delay(1000)
                            showSlogan = true
                        }
                    }
                )
                AppSlogan(show = { showSlogan })
            }
        }
    }
}

@Composable
private fun AppSlogan(show: () -> Boolean) {
    MyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (show()) 1f else 0f),
        horizontalAlignment = Alignment.CenterHorizontally,
        spacing = 8.dp
    ) {
        Text(
            text = stringResource(R.string.splash_msg),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary
        )
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun GifImage(
    @DrawableRes gif: Int,
    @DrawableRes placeholder: Int,
    modifier: Modifier = Modifier,
    onStarted: () -> Unit,
    onLoaded: () -> Unit
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(GifDecoder.Factory())
        }
        .build()

    val model = ImageRequest.Builder(context)
        .repeatCount(0)
        .onAnimationStart { onStarted() }
        .onAnimationEnd { onLoaded() }
        .size(Size.ORIGINAL)
        .data(data = gif)
        .build()

    val gifPainter = rememberAsyncImagePainter(
        model = model,
        imageLoader = imageLoader
    )

    Image(
        painter = if (isPreview()) painterResource(placeholder)
        else gifPainter,
        contentDescription = null,
        modifier = modifier,
    )
}