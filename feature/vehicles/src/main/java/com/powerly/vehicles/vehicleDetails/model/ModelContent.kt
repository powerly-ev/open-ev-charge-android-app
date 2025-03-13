package com.powerly.vehicles.vehicleDetails.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.core.data.model.ModelsStatus
import com.powerly.core.model.powerly.VehicleModel
import com.powerly.vehicles.vehicleDetails.make.ItemName
import com.powerly.vehicles.vehicleDetails.make.SectionSearch
import com.powerly.resources.R
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import com.powerly.ui.theme.MyColors.dividerColor
import com.powerly.ui.dialogs.MyProgressView
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.screen.IndexedScreenHeader


@Preview
@Composable
private fun ModelScreenPreview() {
    val modelsLists = listOf(
        VehicleModel(1, "ACURA", "Acura", ""),
        VehicleModel(2, "ALFA", "Alfa Romeo", ""),
        VehicleModel(3, "AMC", "AMC", ""),
        VehicleModel(4, "ASTON", "Aston Martin", ""),
        VehicleModel(5, "AUDI", "Audi", ""),
        VehicleModel(6, "AVANTI", "Avanti", ""),
        VehicleModel(7, "BENTL", "Bentley", ""),
        VehicleModel(8, "BMW", "BMW", ""),
        VehicleModel(9, "BUICK", "Buick", ""),
        VehicleModel(10, "CAD", "Cadillac", ""),
        VehicleModel(11, "CHEV", "Chevrolet", ""),
        VehicleModel(12, "CHRY", "Chrysler", ""),
        VehicleModel(13, "DAEW", "Daewoo", ""),
        VehicleModel(14, "DAIHAT", "Daihatsu", ""),
        VehicleModel(15, "DATSUN", "Datsun", ""),
        VehicleModel(16, "DELOREAN", "DeLorean", ""),
        VehicleModel(17, "DODGE", "Dodge", ""),
        VehicleModel(18, "EAGLE", "Eagle", ""),
        VehicleModel(19, "FER", "Ferrari", ""),
        VehicleModel(20, "FIAT", "FIAT", "")
    )
    AppTheme {
        ModelScreenContent(
            index = 2,
            models = { ModelsStatus.Success(modelsLists) },
            onClose = { },
            onNext = {}
        )
    }
}


@Composable
internal fun ModelScreenContent(
    index: Int,
    models: () -> ModelsStatus,
    onNext: (VehicleModel) -> Unit,
    onClose: () -> Unit
) {
    MyScreen(
        background = MyColors.white,
        header = {
            IndexedScreenHeader(
                index = index,
                pages = 3,
                title = stringResource(id = R.string.vehicle_model),
                onClose = onClose,
            )
        },
        modifier = Modifier.padding(vertical = 16.dp),
        spacing = 8.dp
    ) {
        var query by remember { mutableStateOf("") }

        SectionSearch(
            hint = R.string.vehicle_model_search,
            onQueryChanges = { query = it }
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            when (val state = models()) {
                is ModelsStatus.Error -> {}
                is ModelsStatus.Loading -> item { MyProgressView() }
                is ModelsStatus.Success -> {
                    state.models.filter {
                        it.name.contains(query, ignoreCase = true)
                    }.forEach {
                        item {
                            ItemName(name = it.name, onClick = { onNext(it) })
                            HorizontalDivider(thickness = 1.dp, color = dividerColor)
                        }
                    }
                }
            }
        }
    }
}
