package com.powerly.home.home.slider

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.powerly.ui.components.SlidingCarousel
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.containers.MySurfaceColumn
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors


@Preview(locale = "ar")
@Composable
private fun SlidePreview() {
    val slides = initSlides(LocalContext.current)
    val slide = slides[0]
    AppTheme {
        Box(
            modifier = Modifier
                .background(Color.White)
                .padding(16.dp)
        ) {
            ItemSlider(
                slide = SliderItem(
                    title = slide.title,
                    message = slide.message,
                    image = slide.image
                ),
                onClick = {}
            )
        }
    }
}

@Composable
internal fun SectionSlider(
    cornerRadius: Dp = 8.dp,
    onClick: () -> Unit
) {
    val slides = initSlides(LocalContext.current)
    Box(Modifier.fillMaxWidth()) {
        SlidingCarousel(
            indicatorSelectedColor = MaterialTheme.colorScheme.secondary,
            indicatorUnSelectedColor = MyColors.grey250,
            itemsCount = slides.size,
            itemContent = { index ->
                val slide = slides.getOrNull(index) ?: slides[0]
                ItemSlider(
                    slide,
                    cornerRadius = cornerRadius,
                    onClick = onClick
                )
            }
        )
    }
}


@Composable
private fun ItemSlider(
    slide: SliderItem,
    cornerRadius: Dp = 8.dp,
    onClick: () -> Unit
) {
    MySurfaceColumn(
        modifier = Modifier.padding(start = 16.dp, end = 0.dp, top = 16.dp, bottom = 16.dp),
        color = MaterialTheme.colorScheme.secondary,
        spacing = 16.dp,
        cornerRadius = cornerRadius,
        onClick = onClick
    ) {
        Text(
            text = slide.title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis
        )
        val style = MaterialTheme.typography.bodyMedium
        val lineHeight = style.fontSize * 4 / 3
        val lines = 4
        Row(verticalAlignment = Alignment.Bottom) {
            MyColumn(
                spacing = 16.dp,
                modifier = Modifier.weight(0.6f)
            ) {
                Text(
                    text = slide.message,
                    textAlign = TextAlign.Start,
                    style = style,
                    color = Color.White,
                    maxLines = lines,
                    minLines = lines,
                    modifier = Modifier
                        .height(IntrinsicSize.Max)
                        .sizeIn(minHeight = with(LocalDensity.current) {
                            (lineHeight * lines).toDp()
                        }),
                    overflow = TextOverflow.Ellipsis
                )
            }
            Image(
                painterResource(id = slide.image),
                contentDescription = "",
                modifier = Modifier.weight(0.4f)
            )
        }
    }
}