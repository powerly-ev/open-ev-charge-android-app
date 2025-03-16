package com.powerly.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map


@Preview
@Composable
fun PickerPreview() {
    val hours = (1..12).map { it.toString() }
    val minutes = (0..55 step 5).map {
        it.toString().padStart(2, '0')
    }

    val hoursState: PickerState = rememberPickerState("6")
    val minutesState: PickerState = rememberPickerState("05")
    val timeState: PickerState = rememberPickerState("PM")

    MyTimePicker(hours, minutes, hoursState, minutesState, timeState)

}

@Preview
@Composable
private fun MySlidingPickerPreview() {
    AppTheme {
        MySlidingPicker(
            items = listOf("1", "2", "3", "4")
        )
    }
}


@Composable
fun MyTimePicker(
    hours: List<String>,
    minutes: List<String>,
    hoursState: PickerState = rememberPickerState(),
    minutesState: PickerState = rememberPickerState(),
    timeState: PickerState = rememberPickerState()
) {

    val time = listOf("AM", "PM")
    val textStyle = MaterialTheme.typography.bodyLarge.copy(
        textAlign = TextAlign.Center,
        fontSize = 20.sp
    )

    val pickerModifier = Modifier.width(50.dp)

    val textModifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 8.dp, vertical = 8.dp)

    val indicatorHeight = 40.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f)
        ) {
            Picker(
                items = hours,
                pickerState = hoursState,
                visibleItemsCount = 7,
                modifier = pickerModifier,
                textModifier = textModifier,
                textStyle = textStyle,
            )
            Picker(
                items = minutes,
                pickerState = minutesState,
                visibleItemsCount = 7,
                modifier = pickerModifier,
                textModifier = textModifier,
                textStyle = textStyle,
            )
            Picker(
                items = time,
                pickerState = timeState,
                visibleItemsCount = 1,
                modifier = pickerModifier,
                textModifier = textModifier,
                textStyle = textStyle,
            )
        }

        Box(
            modifier = Modifier
                .padding(vertical = 2.dp, horizontal = 8.dp)
                .fillMaxWidth()
                .height(indicatorHeight)
                .background(
                    MyColors.viewColor2,
                    shape = RoundedCornerShape(8.dp)
                )
                .align(Alignment.Center)

        )
    }
}

@Composable
fun MySlidingPicker(
    items: List<String>,
    pickerState: PickerState = rememberPickerState(),
    fontSize: TextUnit = 18.sp,
    indicatorHeight: Dp = 35.dp
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {


        val textStyle = MaterialTheme.typography.bodyLarge.copy(
            textAlign = TextAlign.Center,
            fontSize = fontSize
        )

        val textModifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 8.dp, vertical = 8.dp)

        Picker(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(2f),
            items = items,
            pickerState = pickerState,
            visibleItemsCount = 3,
            textModifier = textModifier,
            textStyle = textStyle,
            showDividers = false
        )

        Box(
            modifier = Modifier
                .padding(vertical = 2.dp, horizontal = 8.dp)
                .fillMaxWidth()
                .height(indicatorHeight)
                .background(
                    MyColors.viewColor2,
                    shape = RoundedCornerShape(8.dp)
                )
                .align(Alignment.Center)

        )
    }
}

/**
 * https://gist.github.com/nhcodes/dc68c65ee586628fda5700911e44543f
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Picker(
    items: List<String>,
    modifier: Modifier = Modifier,
    pickerState: PickerState,
    startIndex: Int = 0,
    visibleItemsCount: Int = 3,
    textModifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    dividerColor: Color = LocalContentColor.current,
    showDividers: Boolean = false,
) {

    val visibleItemsMiddle = visibleItemsCount / 2
    val listScrollCount = Integer.MAX_VALUE
    val listScrollMiddle = listScrollCount / 2
    val startAt = if (pickerState.value.isNotEmpty())
        items.indexOf(pickerState.value)
    else startIndex

    val listStartIndex =
        listScrollMiddle - listScrollMiddle % items.size - visibleItemsMiddle + startAt

    fun getItem(index: Int) = items[index % items.size]

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = listStartIndex
    )
    val flingBehavior = rememberSnapFlingBehavior(
        lazyListState = listState
    )

    val itemHeightPixels = remember { mutableIntStateOf(0) }
    val itemHeightDp = pixelsToDp(itemHeightPixels.intValue)

    val fadingEdgeGradient = remember {
        Brush.verticalGradient(
            0f to Color.Transparent,
            0.5f to Color.Black,
            1f to Color.Transparent
        )
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> getItem(index + visibleItemsMiddle) }
            .distinctUntilChanged()
            .collect { item -> pickerState.value = item }
    }

    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeightDp * visibleItemsCount)
                .fadingEdge(fadingEdgeGradient)
        ) {
            items(listScrollCount) { index ->
                Text(
                    text = getItem(index),
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.secondary,
                    overflow = TextOverflow.Ellipsis,
                    style = textStyle,
                    modifier = Modifier
                        .onSizeChanged { size ->
                            itemHeightPixels.intValue = size.height
                        }
                        .then(textModifier)
                )
            }
        }

        if (showDividers) {
            HorizontalDivider(
                color = dividerColor,
                modifier = Modifier.offset(y = itemHeightDp * visibleItemsMiddle)
            )

            HorizontalDivider(
                color = dividerColor,
                modifier = Modifier.offset(y = itemHeightDp * (visibleItemsMiddle + 1))
            )
        }

    }
}

private fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush = brush, blendMode = BlendMode.DstIn)
    }

@Composable
private fun pixelsToDp(pixels: Int) = with(LocalDensity.current) { pixels.toDp() }


@Composable
fun rememberPickerState(default: String = "") = remember { PickerState(default) }

class PickerState(
    private val default: String
) {
    var value by mutableStateOf(default)
}