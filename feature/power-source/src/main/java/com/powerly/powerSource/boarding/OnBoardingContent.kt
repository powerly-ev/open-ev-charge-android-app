package com.powerly.powerSource.boarding

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.powerly.resources.R
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.components.SlidingCarousel
import com.powerly.ui.extensions.onClick
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import kotlinx.coroutines.launch

private const val TAG = "OnBoardingScreen"

@Preview(locale = "en", heightDp = 500)
@Composable
private fun OnBoardingScreenPreview() {
    AppTheme {
        OnBoardingScreenContent(
            onDone = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun OnBoardingScreenContent(
    onDone: (() -> Unit)? = null,
    modifier: Modifier = Modifier.padding(16.dp),
    background: Color = Color.White,
    slideModifier: Modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(ratio = 1.5f),
    slideCornerRadius: Dp = 16.dp
) {
    val items = onBoardingItems()
    val pagerState = rememberPagerState(pageCount = { items.size })
    val coroutineScope = rememberCoroutineScope()

    val onNext: () -> Unit = {
        if (pagerState.currentPage == items.lastIndex) onDone?.invoke()
        coroutineScope.launch {
            pagerState.animateScrollToPage(pagerState.currentPage + 1)
        }
    }

    MyScreen(
        background = background,
        modifier = modifier,
        footer = {
            MyColumn(modifier = modifier.padding()) {
                if (onDone != null) Surface(color = Color.Transparent) {
                    Text(
                        modifier = Modifier
                            .padding(
                                vertical = 8.dp,
                                horizontal = 16.dp
                            )
                            .onClick(onDone),
                        text = stringResource(id = R.string.skip),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                if (onDone != null) ButtonLarge(
                    text = stringResource(id = R.string.next),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNext
                )
            }
        }
    ) {
        SlidingCarousel(
            indicatorSelectedColor = MaterialTheme.colorScheme.secondary,
            indicatorUnSelectedColor = MyColors.grey250,
            autoSlideDuration = 8000,
            dotsSpace = 16.dp,
            itemsCount = items.size,
            pagerState = pagerState,
            itemContent = { index ->
                val item = items.getOrNull(index) ?: items[0]
                OnBoardingSlide(
                    item = item,
                    modifier = slideModifier,
                    cornerSize = slideCornerRadius
                )
            }
        )
    }
}

