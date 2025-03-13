package com.powerly.vehicles.vehicleDetails.make

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.powerly.core.data.model.MakersStatus
import com.powerly.core.model.powerly.VehicleMaker
import com.powerly.resources.R
import com.powerly.ui.containers.MyCardRow
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.dialogs.MyProgressView
import com.powerly.ui.containers.MyRow
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.screen.IndexedScreenHeader
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import com.powerly.ui.theme.MyColors.dividerColor

@Preview
@Composable
private fun MakesScreenPreview() {

    val makers = listOf(
        VehicleMaker(1, "ACURA", "Acura", ""),
        VehicleMaker(2, "ALFA", "Alfa Romeo", ""),
        VehicleMaker(3, "AMC", "Amc", ""),
        VehicleMaker(4, "ASTON", "Aston Martin", ""),
        VehicleMaker(5, "AUDI", "Audi", ""),
        VehicleMaker(6, "AVANTI", "Avanti", ""),
        VehicleMaker(7, "BENTL", "Bentley", ""),
        VehicleMaker(8, "BMW", "BMW", ""),
        VehicleMaker(9, "BUICK", "Buick", ""),
        VehicleMaker(10, "CAD", "Cadillac", ""),
        VehicleMaker(11, "CHEV", "Chevrolet", ""),
        VehicleMaker(12, "CHRY", "Chrysler", ""),
        VehicleMaker(13, "DAEW", "Daewoo", ""),
        VehicleMaker(14, "DAIHAT", "Daihatsu", ""),
        VehicleMaker(15, "DATSUN", "Datsun", ""),
        VehicleMaker(16, "DELOREAN", "DeLorean", ""),
        VehicleMaker(17, "DODGE", "Dodge", ""),
        VehicleMaker(18, "EAGLE", "Eagle", ""),
        VehicleMaker(19, "FER", "Ferrari", ""),
        VehicleMaker(20, "FIAT", "FIAT", "")
    )

    val makersMap: Map<String, List<VehicleMaker>> = makers
        .sortedBy { it.name }
        .groupBy { it.name.first().toString() }

    AppTheme {
        MakesScreenContent(
            index = 1,
            makers = { MakersStatus.Success(makersMap) },
            onClose = { },
            onNext = {}
        )
    }
}


@Composable
internal fun MakesScreenContent(
    index: Int,
    makers: () -> MakersStatus,
    onNext: (VehicleMaker) -> Unit,
    onClose: () -> Unit
) {
    MyScreen(
        background = MyColors.white,
        header = {
            IndexedScreenHeader (
                index = index,
                pages = 3,
                title = stringResource(
                    id = R.string.vehicle_manufacturer
                ),
                onClose = onClose,
            )
        }, modifier = Modifier.padding(vertical = 16.dp)
    ) {

        var query by remember { mutableStateOf("") }
        var alphabets by remember { mutableStateOf("") }

        SectionSearch(
            hint = R.string.vehicle_manufacturer_search,
            onQueryChanges = { query = it }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .height(IntrinsicSize.Max)
            ) {
                when (val state = makers()) {
                    is MakersStatus.Error -> {}
                    is MakersStatus.Loading -> MyProgressView()
                    is MakersStatus.Success -> {
                        state.makers.mapValues { (_, makersList) ->
                            makersList.filter {
                                it.name.contains(
                                    query,
                                    ignoreCase = true
                                )
                            }
                        }.filter { (_, makersList) ->
                            makersList.isNotEmpty()
                        }.also {
                            alphabets = it.keys.sorted().joinToString(separator = "")
                        }.toSortedMap().forEach { (alpha, makers) ->
                            SectionMakers(alpha, makers, onClick = onNext)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

            }

            SectionAlphabets(
                alphabets = { alphabets },
                onClick = {

                }
            )
        }
    }
}

@Composable
private fun SectionMakers(
    alpha: String,
    makers: List<VehicleMaker>,
    onClick: (VehicleMaker) -> Unit
) {
    MyColumn(spacing = 4.dp) {
        Text(
            text = alpha,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(thickness = 1.dp, color = dividerColor)
        makers.forEach {
            ItemName(name = it.name, onClick = { onClick(it) })
            HorizontalDivider(thickness = 1.dp, color = dividerColor)
        }
    }
}

@Composable
private fun SectionAlphabets(
    alphabets: () -> String,
    onClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .fillMaxHeight()
            .background(Color.Transparent),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        alphabets().forEach {
            Text(
                text = "$it",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(vertical = 2.dp, horizontal = 8.dp)
                    .clickable { onClick("$it") }
            )
        }
    }
}

@Composable
fun ItemName(
    name: String,
    onClick: () -> Unit
) {
    MyRow(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.arrow_right),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
internal fun SectionSearch(
    @StringRes hint: Int,
    margin: PaddingValues = PaddingValues(16.dp),
    onQueryChanges: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    MyCardRow(
        fillMaxWidth = true,
        margin = margin,
        padding = PaddingValues(horizontal = 16.dp),
        cornerRadius = 8.dp,
        spacing = 0.dp,
        background = MaterialTheme.colorScheme.surface
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = stringResource(id = hint),
            tint = MaterialTheme.colorScheme.secondary
        )

        OutlinedTextField(
            value = query,
            singleLine = true,
            onValueChange = {
                query = it
                onQueryChanges(it)
            },
            placeholder = {
                Text(
                    text = stringResource(id = hint),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MyColors.subColor
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.secondary,
                focusedPlaceholderColor = MyColors.subColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}
