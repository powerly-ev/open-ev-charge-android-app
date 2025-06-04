package com.powerly.orders.history.details

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.core.model.powerly.Price
import com.powerly.core.model.powerly.Session
import com.powerly.core.model.location.MyAddress
import com.powerly.core.model.powerly.PowerSource
import com.powerly.resources.R
import com.powerly.ui.components.ButtonSmall
import com.powerly.ui.containers.LayoutDirectionAny
import com.powerly.ui.containers.LayoutDirectionLtr
import com.powerly.ui.containers.MyRow
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.extensions.isArabic
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors

private const val TAG = "DeliveredOrderScreen"

@Preview
@Composable
internal fun DeliveredOrderScreenPreview() {
    val session = Session(
        id = "4685",
        deliveryAt = "2023-10-23 10:59:12",
        chargePoint = PowerSource(
            "123",
            address = MyAddress("Egypt, Cairo"),
            price = 2.5,
            priceUnit = "minutes"
        ),
        prices = listOf(
            Price(price = 10.0),
            Price(price = 20.0),
            Price(price = 30.0)
        ),
        price = 1.5,
        quantityRequested = "FULL",
        quantity = "75.1",
        status = 2,
        currency = "SAR"
    )
    AppTheme {
        DeliveredOrderScreenContent(
            session = session,
            onClose = {}
        )
    }
}


@Composable
internal fun DeliveredOrderScreenContent(
    session: Session,
    onClose: () -> Unit
) {
    MyScreen(
        header = { SectionHeader(session, onClose) },
        background = MyColors.white,
        modifier = Modifier.padding(16.dp)
    ) {
        SectionAddress(session.chargePointAddress)
        HorizontalDivider()
        SectionPayment()
        HorizontalDivider()
        SectionSessionDetails(session)
    }
}

@Composable
private fun SectionHeader(
    session: Session,
    onClose: () -> Unit
) {

    val title = if (session.isScheduled) R.string.sessions_booking_id else R.string.order_Id

    val status = when (session.status) {
        1 -> R.string.sessions_scheduled
        2 -> R.string.sessions_completed
        else -> R.string.sessions_active
    }

    val sessionDate = remember {
        if (session.isScheduled) session.reservedAt("yyyy-MM-dd")
        else session.deliveredAt("yyyy-MM-dd")
    }
    val sessionTime = remember {
        if (session.isScheduled) session.reservedAt("h:mm a")
        else session.deliveredAt("h:mm a")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MyColors.viewColor)
            .padding(16.dp)
            .statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.clickable { onClose() },
            painter = painterResource(id = R.drawable.close),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row {
                Text(
                    text = stringResource(id = title),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = session.id,
                    style = MaterialTheme.typography.titleSmall,
                    color = MyColors.subColor
                )
            }

            MyRow {
                ButtonSmall(
                    text = stringResource(id = status),
                    background = MaterialTheme.colorScheme.tertiary,
                    height = 24.dp,
                    modifier = Modifier.wrapContentWidth()
                )

                ButtonSmall(
                    text = sessionDate,
                    color = MyColors.subColor,
                    background = Color.White,
                    border = BorderStroke(width = 1.dp, color = MyColors.borderColor),
                    height = 24.dp,
                    modifier = Modifier.wrapContentWidth()
                )

                ButtonSmall(
                    text = sessionTime,
                    background = Color.White,
                    color = MyColors.subColor,
                    border = BorderStroke(width = 1.dp, color = MyColors.borderColor),
                    height = 24.dp,
                    modifier = Modifier.wrapContentWidth()
                )
            }
        }
    }
}


@Composable
private fun SectionSessionDetails(session: Session) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        ItemSessionDetails(
            title = R.string.order_selected_product,
            value = stringResource(
                id = if (session.isTimedSession) R.string.station_unit_price_per_minute_total
                else R.string.station_unit_price_per_kwh_total,
                unitPrice(session),
                session.currency,
                session.formatedQuantity(
                    h = stringResource(R.string.time_h),
                    m = stringResource(R.string.time_m),
                    s = stringResource(R.string.time_s),
                )
            )
        )
        ItemSessionDetails(
            title = R.string.order_total,
            value1 = session.price,
            value2 = session.currency,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun SectionPayment() {
    MyRow(
        spacing = 8.dp,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ), modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_payment_balance),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(4.dp)
                    .size(32.dp)
            )
        }

        Text(
            text = stringResource(id = R.string.balance),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun ItemSessionDetails(
    @StringRes title: Int,
    value: Any
) {
    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.tertiary
        )
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = title),
                style = style
            )
            Spacer(modifier = Modifier.weight(1f))
            LayoutDirectionAny {
                Text(text = value.toString(), style = style)
            }
        }
        HorizontalDivider(color = MyColors.dividerColor)
    }
}

@Composable
private fun ItemSessionDetails(
    @StringRes title: Int,
    value1: Any,
    value2: Any,
    style: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.tertiary
    )
) {
    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(id = title),
                style = style
            )
            Spacer(modifier = Modifier.weight(1f))
            LayoutDirectionLtr {
                MyRow(spacing = 4.dp) {
                    Text(text = value1.toString(), style = style)
                    Text(text = value2.toString(), style = style)
                }
            }
        }
        HorizontalDivider(color = MyColors.dividerColor)
    }
}

@Composable
fun SectionAddress(address: String) {
    Column(Modifier.fillMaxWidth()) {
        MyRow(spacing = 4.dp) {
            Icon(
                painter = painterResource(id = R.drawable.location),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = stringResource(id = R.string.location_delivery_address),
                style = MaterialTheme.typography.titleSmall
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = address,
            modifier = Modifier.padding(start = 28.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

/**
 * Returns a formatted price string for the given session.
 *
 * If the session has multiple prices, the returned string will show the price range
 * (e.g., "10-20"). If the session has a single price, that price will be returned.
 * If the session has no prices, the session's unit price will be returned.
 *
 * For Arabic locales, a left-to-right mark is added to ensure correct display of the price.
 *
 * @param session the session to get the price for
 * @return the formatted price string
 */
@Composable
private fun unitPrice(session: Session): String {
    val prices = session.prices.orEmpty()
    val pre = if (isArabic()) "\u200E" else ""

    val unitPriceRange: String = if (prices.size == 1) {
        prices.first().price.toString()
    } else if (prices.size > 1) {
        val minPrice = prices.minBy { it.price }.price
        val maxPrice = prices.maxBy { it.price }.price
        "$minPrice-$maxPrice"
    } else {
        session.unitPrice.toString()
    }
    return "$pre$unitPriceRange"
}
