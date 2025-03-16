package com.powerly.powerSource.media

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.core.model.powerly.Media
import com.powerly.resources.R
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.components.NetworkImage
import com.powerly.ui.components.SlidingCarousel
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors

@Preview
@Composable
private fun MediaScreenPreview() {
    val media = listOf(Media(1), Media(2), Media(3))
    AppTheme {
        MediaScreenContent(
            media = { media },
            onClose = {}
        )
    }
}


@Composable
internal fun MediaScreenContent(
    media: () -> List<Media>,
    onClose: () -> Unit
) {
    MyScreen(
        header = {
            ScreenHeader(
                title = "",
                onClose = onClose,
                showDivider = false
            )
        },
        background = Color.White,
        spacing = 8.dp,
        modifier = Modifier.padding(vertical = 8.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            val images = media()
            SlidingCarousel(
                indicatorSelectedColor = MaterialTheme.colorScheme.secondary,
                indicatorUnSelectedColor = MyColors.grey250,
                autoSlideDuration = 8000,
                itemsCount = images.size,
                itemContent = { index ->
                    val image = images.getOrNull(index) ?: images[0]
                    ItemMedia(image)
                }
            )
        }
    }
}

@Composable
private fun ItemMedia(media: Media) {
    NetworkImage(
        src = media.url,
        placeHolder = R.drawable.logo,
        description = media.title,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.95f)
    )
}