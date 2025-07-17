package com.powerly.orders.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.powerly.core.model.powerly.PowerSource
import com.powerly.core.model.powerly.Session
import com.powerly.orders.SessionItemTitle
import com.powerly.orders.SessionOutlinedButton
import com.powerly.orders.active.ListSessions
import com.powerly.resources.R
import com.powerly.ui.components.RatingBar
import com.powerly.ui.containers.MyCardColum
import com.powerly.ui.containers.MyRefreshBox
import com.powerly.ui.containers.MyRow
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val TAG = "SessionHistoryScreen"

@Preview
@Composable
private fun SessionHistoryScreenPreview() {

    val list = listOf(
        Session(
            id = "3387",
            currency = "SR",
            price = 30.0,
            deliveryAt = "2024-06-06",
            /*onlineStatus = 1,
            isReserved = 0,
            isInUse = false,*/
            chargePoint = PowerSource(title = "station 1 ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"),
        )
    )

    val lazyPagingItems = flow {
        emit(PagingData.from(list))
    }

    AppTheme {
        SessionHistoryScreenContent(
            currency = "SAR",
            sessions = lazyPagingItems,
            refresh =  remember { mutableStateOf<Boolean>(false) },
            onRecharge = {},
            onItemClick = {}
        )
    }
}


@Composable
internal fun SessionHistoryScreenContent(
    sessions: Flow<PagingData<Session>>,
    refresh: MutableState<Boolean>,
    currency: String,
    onItemClick: (Session) -> Unit,
    onRecharge: (Session) -> Unit
) {
    val items = sessions.collectAsLazyPagingItems()
    LaunchedEffect(Unit) {
        snapshotFlow { refresh.value }.collect {
            if (it == true) items.refresh()
            refresh.value = false
        }
    }
    MyRefreshBox(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        onRefresh = { items.refresh() }
    ) {
        ListSessions(
            items = items,
            emptyMessage = R.string.sessions_history_empty
        ) { session ->
            session.currency = currency
            ItemSessionUser(
                session = session,
                onClick = { onItemClick(session) },
                onRecharge = { onRecharge(session) }
            )
        }
    }
}

@Composable
private fun ItemSessionUser(
    session: Session,
    onClick: () -> Unit,
    onRecharge: () -> Unit
) {
    MyCardColum(
        padding = PaddingValues(16.dp),
        spacing = 8.dp,
        onClick = onClick
    ) {

        SessionItemTitle(
            title = R.string.order_Id,
            id = session.id,
            price = session.price,
            checked = true,
            currency = session.currency
        )
        HorizontalDivider(color = MyColors.dividerColor)
        MyRow {
            Text(
                text = session.chargePoint.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = session.deliveredAt(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        MyRow(spacing = 4.dp) {
            if (session.chargePoint.isAvailable) SessionOutlinedButton(
                title = R.string.station_recharge,
                icon = R.drawable.charge,
                border = null,
                background = MyColors.viewColor,
                color = MaterialTheme.colorScheme.secondary,
                onClick = onRecharge
            )
            Spacer(modifier = Modifier.weight(1f))
            RatingBar(
                modifier = Modifier.wrapContentWidth(),
                rating = session.chargePoint.rating
            )
        }

    }
}
