package com.powerly.vehicles.vehicleDetails.options.connectors

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.model.powerly.Connector
import com.powerly.vehicles.vehicleDetails.make.SectionSearch
import com.powerly.resources.R
import com.powerly.ui.containers.MyCardColum
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.components.NetworkImage
import com.powerly.ui.dialogs.MyProgressView
import com.powerly.ui.theme.MyColors

private const val TAG = "ConnectorsDialog"


@Preview
@Composable
private fun ConnectorsDialogPreview() {

    val connectors = listOf(
        Connector(1, name = "Mennekes", maxPower = 34.0),
        Connector(2, name = "Mennekes2", maxPower = 43.0),
        Connector(2, name = "Mennekes3", maxPower = 33.0)

    )

    ConnectorsScreenContent(
        connectors = ApiStatus.Success(connectors),
        onClose = { },
        selectedIds = listOf(1),
        onSelect = {}
    )
}

@Composable
internal fun ConnectorsScreenContent(
    connectors: ApiStatus<List<Connector>>,
    selectedIds: List<Int>,
    onSelect: (Connector) -> Unit,
    onClose: () -> Unit
) {
    MyScreen(
        background = MyColors.white,
        header = {
            ScreenHeader(
                title = stringResource(id = R.string.station_plugs),
                onClose = onClose,
            )
        },
        modifier = Modifier.padding(16.dp)
    ) {
        var query by remember { mutableStateOf("") }

        SectionSearch(
            margin = PaddingValues(0.dp),
            hint = R.string.vehicle_model_search,
            onQueryChanges = { query = it }
        )
        Spacer(modifier = Modifier.height(8.dp))


        MyColumn(modifier = Modifier.verticalScroll(rememberScrollState())) {
            when (val state = connectors) {
                is ApiStatus.Error -> {}
                is ApiStatus.Loading -> MyProgressView()
                is ApiStatus.Success -> {
                    state.data.filter {
                        it.name.contains(query, ignoreCase = true)
                    }.forEach {
                        ItemConnector(
                            connector = it,
                            isSelected = selectedIds.contains(it.id),
                            onClick = { onSelect(it) }
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun ItemConnector(
    connector: Connector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    MyCardColum(
        padding = PaddingValues(16.dp),
        onClick = if (isSelected) null else onClick,
        background = MaterialTheme.colorScheme.surface,
        horizontalAlignment = Alignment.CenterHorizontally,
        spacing = 8.dp
    ) {
        NetworkImage(
            src = connector.icon,
            modifier = Modifier.size(100.dp)
        )
        Text(
            text = connector.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary
        )
        val watt = stringResource(id = R.string.station_kwh)
        Text(
            text = "type ${connector.type.orEmpty()} - ${connector.maxPower} $watt",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        ButtonLarge(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
            layoutDirection = LayoutDirection.Rtl,
            text = stringResource(id = R.string.station_plug_add),
            icon = if (isSelected) R.drawable.ic_true
            else R.drawable.ic_add,
            enabled = { isSelected.not() },
            background = MaterialTheme.colorScheme.secondary,
            color = MyColors.white,
        )
    }
}
