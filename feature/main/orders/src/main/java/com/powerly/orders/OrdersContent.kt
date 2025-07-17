package com.powerly.orders

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.powerly.core.model.powerly.OrderTab
import com.powerly.resources.R
import com.powerly.ui.components.ButtonSmall
import com.powerly.ui.components.MyTextDynamic
import com.powerly.ui.containers.LayoutDirectionLtr
import com.powerly.ui.containers.MyRow
import com.powerly.ui.dialogs.loading.LoadingDialog
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.dialogs.message.MessageDialog
import com.powerly.ui.dialogs.success.SuccessDialog
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import kotlinx.coroutines.launch

@Preview
@Composable
private fun OrdersScreenPreview() {

    @Composable
    fun FakeScreen(title: String) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                title,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    val tabs = listOf(
        OrderTab(title = R.string.sessions_active),
        OrderTab(title = R.string.sessions_history)
    )

    AppTheme {
        OrdersScreenContent(
            tabs = tabs,
            pagerState = rememberPagerState(
                initialPage = OrderTab.ACTIVE,
                pageCount = { tabs.size }
            )
        ) {
            when (it) {
                OrderTab.ACTIVE -> FakeScreen("Screen A")
                OrderTab.HISTORY -> FakeScreen("Screen B")
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun OrdersScreenContent(
    tabs: List<OrderTab>,
    pagerState: PagerState,
    screenState: ScreenState = rememberScreenState(),
    pageContent: @Composable PagerScope.(page: Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        tabPositions[pagerState.currentPage]
                    ), color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            tabs.forEachIndexed { index, tab ->
                val selected = index == pagerState.currentPage
                ItemTab(
                    selected = selected,
                    tab = tab
                ) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            }
        }
        HorizontalPager(state = pagerState, pageContent = pageContent)
    }

    screenState.successState?.let { SuccessDialog(state = it) }
    screenState.messageState?.let { MessageDialog(state = it) }
    screenState.loadingState?.let { LoadingDialog(state = it) }
}

@Composable
private fun ItemTab(
    selected: Boolean,
    tab: OrderTab,
    onClick: () -> Unit
) {

    Tab(
        selected = selected,
        modifier = Modifier
            .padding(0.dp)
            .background(Color.White),
        text = {
            MyTextDynamic(
                text = stringResource(id = tab.title),
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary,
            )
        },
        onClick = onClick,
    )
}


@Composable
internal fun SessionItemTitle(
    @StringRes title: Int = R.string.order_Id,
    @StringRes total: Int = R.string.order_total,
    id: String,
    price: Any,
    currency: String,
    checked: Boolean = false
) {
    MyRow(spacing = 4.dp) {
        Text(
            text = stringResource(id = title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = id,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        if (checked) Icon(
            painter = painterResource(id = R.drawable.ic_true),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = ""
        )
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(id = total),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        LayoutDirectionLtr {
            MyRow(spacing = 4.dp) {
                Text(
                    text = price.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = currency,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun SessionButton(
    @StringRes title: Int,
    background: Color = MyColors.OrderStatus.Cancelled,
    color: Color = MyColors.white,
    onClick: () -> Unit
) {
    ButtonSmall(
        text = stringResource(id = title),
        height = 40.dp,
        padding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        fontSize = 14.sp,
        background = background,
        color = color,
        onClick = onClick
    )
}

@Composable
fun SessionOutlinedButton(
    @StringRes title: Int,
    @DrawableRes icon: Int,
    iconSpace: Dp = 8.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    background: Color = Color.Transparent,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    border: BorderStroke? = BorderStroke(1.dp, color = borderColor),
    onClick: (() -> Unit)? = null
) {

    Card(
        border = border,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = background
        ),
        modifier = Modifier.clickable(
            onClick = { onClick?.invoke() },
            enabled = onClick != null
        ),
    ) {
        MyRow(
            spacing = iconSpace,
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "",
                modifier = Modifier.size(30.dp),
                tint = iconColor
            )
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.bodyMedium,
                color = color,
            )
        }
    }
}
