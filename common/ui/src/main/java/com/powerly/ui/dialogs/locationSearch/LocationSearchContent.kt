package com.powerly.ui.dialogs.locationSearch

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.lib.managers.PlaceItem
import com.powerly.resources.R
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.containers.MyCardRow
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.components.MySearchBox
import com.powerly.ui.screen.ScreenHeader

private const val TAG = " LocationSearchScreen"

@Preview
@Composable
private fun LocationSearchScreenPreview() {
    val places = listOf(
        PlaceItem(primary = "Egypt", secondary = "Cairo, Egypt"),
        PlaceItem(primary = "Egypt", secondary = "Alex, Egypt"),
        PlaceItem(primary = "Egypt", secondary = "Giza, Egypt"),
        PlaceItem(primary = "Egypt", secondary = "Sohag, Egypt")
    )
    AppTheme {
        LocationSearchScreenContent(
            places = { places },
            onSelectPlace = {},
            onQueryChanges = {},
            onClose = {}
        )
    }
}

@Composable
internal fun LocationSearchScreenContent(
    places: () -> List<PlaceItem>,
    onSelectPlace: (PlaceItem) -> Unit,
    onQueryChanges: (String) -> Unit,
    onClose: () -> Unit
) {
    MyScreen(
        header = {
            ScreenHeader(
                title = stringResource(id = R.string.location_search_for),
                onClose = onClose,
                showDivider = false
            )
        },
        spacing = 16.dp,
        modifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 8.dp
        )
    ) {
        MySearchBox(
            showKeyboard = true,
            hint = R.string.location_search,
            background = Color.White,
            iconColor = MaterialTheme.colorScheme.primary,
            afterQueryChanges = onQueryChanges
        )
        MyColumn(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            spacing = 8.dp
        ) {
            places().forEach {
                SearchItem(
                    place = it,
                    onClick = { onSelectPlace(it) }
                )
            }
        }
    }
}

@Composable
private fun SearchItem(
    place: PlaceItem,
    onClick: () -> Unit
) {
    MyCardRow(
        padding = PaddingValues(
            vertical = 8.dp,
            horizontal = 16.dp
        ),
        spacing = 16.dp,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = R.drawable.location),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.secondary
        )
        MyColumn(spacing = 4.dp) {
            Text(
                text = place.primary,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                text = place.secondary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}
