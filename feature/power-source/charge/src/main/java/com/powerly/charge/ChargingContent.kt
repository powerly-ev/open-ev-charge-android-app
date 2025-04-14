package com.powerly.charge

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.powerly.charge.util.ChargingTimerState
import com.powerly.charge.util.asFormattedTime
import com.powerly.charge.util.chargingAnimation
import com.powerly.charge.util.rememberChargingTimerState
import com.powerly.core.model.powerly.PowerSource
import com.powerly.core.model.powerly.Session
import com.powerly.resources.R
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.theme.MyColors
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.containers.MyRow
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.extensions.thenIf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "ChargingScreenContent"

@Preview
@Composable
private fun ChargingScreenPreview() {
    val session = Session(
        "4955", price = 2.0, appFees = 25.0,
        quantity = "30",
        chargePoint = PowerSource(price = 1.5)
    ).apply {
        currency = "SAR"
    }
    AppTheme {
        ChargingScreenContent(
            timerState = rememberChargingTimerState(
                elapsedTime = 60 * 3,
                remainingTime = 60 * 7
            ),
            session = session,
            onClose = {},
            onStop = {},
        )
    }
}


@Composable
internal fun ChargingScreenContent(
    screenState: ScreenState = rememberScreenState(),
    timerState: ChargingTimerState,
    session: Session?,
    onClose: () -> Unit,
    onStop: () -> Unit,
) {
    MyScreen(
        screenState = screenState,
        background = Color.White,
        header = {
            ScreenHeader(
                title = stringResource(
                    id = R.string.station_number,
                    session?.chargePointId.orEmpty()
                ),
                onClose = onClose,
            )
        },
        spacing = 32.dp,
        modifier = Modifier.padding(16.dp),
        verticalScroll = true
    ) {
        if (session == null) return@MyScreen
        Spacer(modifier = Modifier.height(32.dp))
        SectionProgress(timerState)

        SectionDetails(session, timerState)

        ButtonLarge(
            modifier = Modifier.fillMaxWidth(),
            onClick = onStop,
            layoutDirection = LayoutDirection.Rtl,
            text = stringResource(id = R.string.station_charging_stop),
            icon = R.drawable.charge,
            color = MaterialTheme.colorScheme.secondary,
            background = MyColors.viewColor2
        )

    }
}

@Composable
private fun SectionDetails(
    session: Session,
    timerState: ChargingTimerState
) {
    MyColumn(
        spacing = 0.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        //total price
        DetailsItem(
            title = R.string.order_total_price,
            value = { session.price },
            unit = session.currency
        )
        Spacer(modifier = Modifier.height(16.dp))

        //charged
        DetailsItem(
            title = R.string.station_charged,
            value = { session.chargingSessionEnergy }
        )
        Spacer(modifier = Modifier.height(16.dp))

        //time
        DetailsItem(
            title = R.string.station_time,
            value = {
                if (session.isFull) timerState.elapsedTime.asFormattedTime()
                else session.displayTime.asFormattedTime()
            }
        )
    }
}

@Composable
private fun SectionPrice(
    totalPrice: () -> Double,
    chargingPrice: () -> Double,
    appFess: () -> Double,
    currency: String
) {
    var showPriceDetails by remember { mutableStateOf(false) }

    DetailsItem(
        title = R.string.station_total_price,
        value = { "${totalPrice()} $currency" },
        showArrow = true,
        paddingVertical = 0.dp,
        arrowIcon = if (showPriceDetails) R.drawable.ic_arrow_down
        else R.drawable.ic_arrow_up,
        onClick = { showPriceDetails = showPriceDetails.not() }
    )
    AnimatedVisibility(showPriceDetails) {
        Spacer(modifier = Modifier.height(8.dp))
        MyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            spacing = 0.dp
        ) {
            DetailsSubItem(
                title = R.string.station_price,
                value = "${chargingPrice()} $currency"
            )
            DetailsSubItem(
                title = R.string.station_app_fees,
                value = "${appFess()} $currency"
            )
        }
    }
}

@Composable
private fun SectionProgress(
    timerState: ChargingTimerState
) {

    val coroutineScope = rememberCoroutineScope() // Create a coroutine scope
    var currentProgress by remember { mutableFloatStateOf(0.0f) }
    var reload by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(true) }
    val progressAlpha: Float by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing,
        ),
        finishedListener = { alpha ->
            if (alpha == 0f) {
                visible = true
                coroutineScope.launch {
                    delay(800)
                    reload = reload.not()
                }
            }
        },
        label = "progressAlpha",
    )

    LaunchedEffect(key1 = reload) {
        coroutineScope.launch {
            chargingAnimation(
                onProgress = { currentProgress = it },
                onComplete = { visible = false }
            )
        }
    }


    Box(modifier = Modifier.wrapContentSize()) {
        CircularProgressIndicator(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(1f)
                .shadow(
                    elevation = 40.dp,
                    shape = RoundedCornerShape(50),
                    spotColor = MyColors.blueLight,
                )
                .rotate(90f)
                .background(Color.White)
                .graphicsLayer(alpha = progressAlpha),
            progress = { currentProgress },
            strokeWidth = 10.dp,
            color = MaterialTheme.colorScheme.primary,
            trackColor = MyColors.blueLight,
        )

        SectionProgressLabel(
            remainingTime = { timerState.remainingTime.asFormattedTime() }
        )
    }


}

@Composable
private fun BoxScope.SectionProgressLabel(
    remainingTime: () -> String
) {
    MyColumn(
        modifier = Modifier
            .align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        spacing = 16.dp
    ) {
        Icon(
            painter = painterResource(id = R.drawable.charge),
            contentDescription = "",
            modifier = Modifier.size(40.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = remainingTime(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 50.sp
            ),
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
private fun DetailsItem(
    @StringRes title: Int,
    value: () -> Any,
    unit: String? = null,
    showArrow: Boolean = false,
    paddingVertical: Dp = 4.dp,
    @DrawableRes arrowIcon: Int = R.drawable.ic_arrow_down,
    onClick: (() -> Unit)? = null
) {
    MyRow(
        spacing = 4.dp,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = paddingVertical)
            .thenIf(
                onClick != null,
                Modifier.clickable { onClick?.invoke() }
            )
    ) {
        Text(
            text = stringResource(id = title),
            style = MaterialTheme.typography.bodyLarge,
            color = MyColors.subColor,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value().toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
        unit?.let {
            Text(
                text = unit,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        if (showArrow) Icon(
            painter = painterResource(id = arrowIcon),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun DetailsSubItem(
    @StringRes title: Int,
    value: String,
) {
    MyRow(
        spacing = 4.dp,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp)
    ) {
        Text(
            text = stringResource(id = title),
            style = MaterialTheme.typography.bodyMedium,
            color = MyColors.subColor,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MyColors.subColor,
        )
    }
}
