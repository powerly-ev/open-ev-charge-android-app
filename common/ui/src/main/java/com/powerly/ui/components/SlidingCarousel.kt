package com.powerly.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.powerly.resources.R
import com.powerly.ui.theme.MyColors
import kotlinx.coroutines.delay

/**
 * Auto Image Slider with Dots Indicator
 * https://blog.protein.tech/jetpack-compose-auto-image-slider-with-dots-indicator-45dfeba37712
 */

@Preview()
@Composable
fun CarouselPreview() {
    val images = listOf(
        R.drawable.ic_refresh,
        R.drawable.arrow_back,
        R.drawable.account,
        R.drawable.arrow_right
    )

    Box(
        modifier = Modifier
            .background(MyColors.viewColor)
            .padding(4.dp)
            .wrapContentHeight(),
    ) {
        SlidingCarousel(
            itemsCount = images.size,
            itemContent = {
                Image(
                    painter = painterResource(id = images[it]),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.height(200.dp)
                )
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoSlidingCarousel(
    modifier: Modifier = Modifier,
    autoSlideDuration: Long = 3000,
    itemsCount: Int,
    indicatorSelectedColor: Color = Color.Blue,
    indicatorUnSelectedColor: Color = Color.White,
    itemContent: @Composable (index: Int) -> Unit,
) {
    val pagerState: PagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { itemsCount }
    )
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect(pagerState.currentPage) {
        delay(autoSlideDuration)
        val page = (pagerState.currentPage + 1) % itemsCount
        pagerState.scrollToPage(page)
    }

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 16.dp,
        ) {
            itemContent(it)
        }

        DotsIndicator(
            modifier = Modifier
                .padding(
                    horizontal = 8.dp,
                    vertical = 6.dp
                )
                .align(Alignment.BottomCenter),
            space = 4.dp,
            selectedColor = indicatorSelectedColor,
            unSelectedColor = indicatorUnSelectedColor,
            totalDots = itemsCount,
            selectedIndex = if (isDragged) pagerState.currentPage
            else pagerState.targetPage,
            dotSize = 8.dp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SlidingCarousel(
    modifier: Modifier = Modifier,
    autoSlideDuration: Long? = null,
    itemsCount: Int,
    dotsSpace: Dp = 0.dp,
    indicatorSelectedColor: Color = MaterialTheme.colorScheme.secondary,
    indicatorUnSelectedColor: Color = MyColors.grey200,
    pagerState: PagerState = rememberPagerState(
        pageCount = { itemsCount }
    ),
    itemContent: @Composable (index: Int) -> Unit,
) {

    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    autoSlideDuration?.let {
        LaunchedEffect(pagerState.currentPage) {
            delay(it)
            val page = (pagerState.currentPage + 1) % itemsCount
            pagerState.scrollToPage(page)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dotsSpace)
    ) {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 16.dp,
        ) {
            itemContent(it)
        }
        DotsIndicator(
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 6.dp
            ),
            space = 4.dp,
            selectedColor = indicatorSelectedColor,
            unSelectedColor = indicatorUnSelectedColor,
            totalDots = itemsCount,
            selectedIndex = if (isDragged) pagerState.currentPage
            else pagerState.targetPage,
            dotSize = 8.dp
        )
    }
}

@Composable
fun DotsIndicator(
    modifier: Modifier = Modifier,
    totalDots: Int,
    selectedIndex: Int,
    space: Dp = 2.dp,
    selectedColor: Color = Color.Yellow,
    unSelectedColor: Color = Color.Gray,
    dotSize: Dp
) {
    LazyRow(
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight(),
    ) {
        items(totalDots) { index ->
            IndicatorDot(
                color = if (index == selectedIndex) selectedColor
                else unSelectedColor,
                size = dotSize
            )

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = space))
            }
        }
    }
}

@Composable
fun IndicatorDot(
    modifier: Modifier = Modifier,
    size: Dp,
    color: Color
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}