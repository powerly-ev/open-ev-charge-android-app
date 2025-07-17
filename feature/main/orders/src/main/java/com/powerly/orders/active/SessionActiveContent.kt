package com.powerly.orders.active

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.powerly.core.model.powerly.PowerSource
import com.powerly.core.model.powerly.Session
import com.powerly.orders.SessionButton
import com.powerly.orders.SessionItemTitle
import com.powerly.orders.SessionOutlinedButton
import com.powerly.resources.R
import com.powerly.ui.components.ItemsEmpty
import com.powerly.ui.components.RatingBar
import com.powerly.ui.containers.MyCardColum
import com.powerly.ui.containers.MyRefreshBox
import com.powerly.ui.containers.MyRow
import com.powerly.ui.dialogs.MyProgressView
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Preview
@Composable
private fun SessionActiveScreenPreview() {

    val sessions = listOf(
        Session(
            id = "3387",
            currency = "SR",
            price = 30.0,
            chargePoint = PowerSource(title = "station 1"),
        )
    )

    val sessionsFlow = flow {
        emit(PagingData.from(sessions))
    }

    AppTheme {
        SessionActiveScreenContent(
            currency = "SAR",
            sessionsFlow = sessionsFlow,
            onItemClick = {},
            onStopCharging = {}
        )
    }
}

@Composable
internal fun SessionActiveScreenContent(
    sessionsFlow: Flow<PagingData<Session>>,
    autoRefresh: Boolean = false,
    currency: String,
    onItemClick: (Session) -> Unit,
    onStopCharging: (Session) -> Unit
) {
    val items = sessionsFlow.collectAsLazyPagingItems()
    LaunchedEffect(Unit) {
        if (autoRefresh) items.refresh()
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
            emptyMessage = R.string.sessions_active_empty
        ) { session ->
            session.currency = currency
            ItemSessionUser(
                session = session,
                onClick = { onItemClick(session) },
                onButtonClick = { onStopCharging(session) }
            )
        }
    }
}

@Composable
internal fun ListSessions(
    items: LazyPagingItems<Session>,
    @StringRes emptyMessage: Int,
    itemContent: @Composable (Session) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            count = items.itemCount,
            key = items.itemKey(),
            contentType = items.itemContentType()
        ) { index ->
            val session = items[index] ?: return@items
            itemContent(session)
        }

        when (items.loadState.refresh) { //FIRST LOAD
            is LoadState.Error -> {}
            is LoadState.Loading -> item { MyProgressView() }
            else -> {
                if (items.itemCount == 0) item {
                    ItemsEmpty(emptyMessage)
                }
            }
        }

        when (items.loadState.append) { // Pagination
            is LoadState.Error -> {}
            is LoadState.Loading -> item { MyProgressView() }
            else -> {}
        }
    }
}


@Composable
private fun ItemSessionUser(
    session: Session = Session(chargePoint = PowerSource(title = "Test")),
    onClick: () -> Unit = {},
    onButtonClick: (() -> Unit)? = null
) {
    MyCardColum(
        padding = PaddingValues(16.dp),
        spacing = 8.dp,
        onClick = onClick
    ) {

        SessionItemTitle(
            id = session.id,
            price = session.price,
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
            SessionOutlinedButton(
                title = R.string.station_charging,
                icon = R.drawable.charge,
            )
        }
        MyRow(spacing = 4.dp) {
            SessionButton(
                title = R.string.station_charging_stop,
                onClick = { onButtonClick?.invoke() }
            )
            Spacer(modifier = Modifier.weight(1f))
            RatingBar(
                modifier = Modifier.wrapContentWidth(),
                rating = session.chargePoint.rating
            )
        }

    }
}

