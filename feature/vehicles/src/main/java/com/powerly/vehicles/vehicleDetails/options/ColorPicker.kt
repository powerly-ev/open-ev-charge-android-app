package com.powerly.vehicles.vehicleDetails.options

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.resources.R
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.dialogs.MyBasicBottomSheet
import com.powerly.ui.dialogs.MyDialogState
import com.powerly.ui.extensions.thenIf
import com.powerly.ui.screen.DialogHeader
import com.powerly.ui.theme.MyColors

@Preview
@Composable
private fun ColorPickerBottomSheetPreview() {
    ColorPicker(
        selectedColor = null,
        onSelectColor = {},
        onClose = {}
    )
}

@Composable
fun ColorPickerDialog(
    state: MyDialogState,
    selectedColor: () -> VehicleColor?,
    onSelect: (VehicleColor) -> Unit
) {
    MyBasicBottomSheet(state = state) {
        ColorPicker(
            selectedColor = selectedColor(),
            onSelectColor = {
                onSelect(it)
                state.dismiss()
            },
            onClose = { state.dismiss() }
        )
    }
}

@Composable
private fun ColorPicker(
    selectedColor: VehicleColor?,
    onSelectColor: (VehicleColor) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {

        DialogHeader(
            title = stringResource(
                id = R.string.vehicle_details_color_custom
            ),
            textAlign = TextAlign.Start,
            onClose = onClose,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MyColors.viewColor
                )
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7)
                ) {
                    items(vehiclesColors.size) { index ->
                        val color = vehiclesColors[index]
                        ItemColor(
                            selected = color.id == selectedColor?.id,
                            color = color.color,
                            onClick = { onSelectColor(color) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemColor(
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    val innerCircle = 28.dp
    val innerPadding = 6.dp

    Box(
        modifier = Modifier
            .wrapContentSize()
            .thenIf(
                selected,
                Modifier.border(
                    border = BorderStroke(2.dp, color),
                    shape = CircleShape
                )
            )
    ) {
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .size(innerCircle)
                .background(
                    color = color,
                    shape = CircleShape
                )
                .clickable(onClick = onClick)
        )
    }
}


@Composable
fun rememberMyColorPickerState(default: VehicleColor? = null) = remember {
    MyColorPickerState(default)
}

class MyColorPickerState(
    private val default: VehicleColor?
) {
    var selected by mutableStateOf(default)
}

data class VehicleColor(
    val id: Int,
    val name: String,
    val color: Color,
    val isLight: Boolean = false
)


private val vehiclesColors = listOf(
    VehicleColor(id = 1, name = "White", color = Color(0xFFFFFFFF), isLight = true),
    VehicleColor(id = 2, name = "Black", color = Color(0xFF000000)),
    VehicleColor(id = 3, name = "Silver", color = Color(0xFFC0C0C0)),
    VehicleColor(id = 4, name = "Gray", color = Color(0xFF808080)),
    VehicleColor(id = 5, name = "Blue", color = Color(0xFF0000FF)),
    VehicleColor(id = 6, name = "Red", color = Color(0xFFFF0000)),
    VehicleColor(id = 7, name = "Green", color = Color(0xFF008000)),
    VehicleColor(id = 8, name = "Brown", color = Color(0xFFA52A2A)),
    VehicleColor(id = 9, name = "Beige", color = Color(0xFFF5F5DC), isLight = true),
    VehicleColor(id = 10, name = "Yellow", color = Color(0xFFFFFF00)),
    VehicleColor(id = 11, name = "Orange", color = Color(0xFFFFA500)),
    VehicleColor(id = 12, name = "Gold", color = Color(0xFFFFD700)),
    VehicleColor(id = 13, name = "Purple", color = Color(0xFF800080)),
    VehicleColor(id = 14, name = "Bronze", color = Color(0xFFCD7F32)),
    VehicleColor(id = 15, name = "Charcoal", color = Color(0xFF36454F)),
    VehicleColor(id = 16, name = "Maroon", color = Color(0xFF800000)),
    VehicleColor(id = 17, name = "Navy", color = Color(0xFF000080)),
    VehicleColor(id = 18, name = "Dark Blue", color = Color(0xFF00008B)),
    VehicleColor(id = 19, name = "Dark Gray", color = Color(0xFFA9A9A9)),
    VehicleColor(id = 20, name = "Light Gray", color = Color(0xFFD3D3D3)),
    VehicleColor(id = 21, name = "Dark Green", color = Color(0xFF006400)),
    VehicleColor(id = 22, name = "Light Green", color = Color(0xFF90EE90)),
    VehicleColor(id = 23, name = "Dark Red", color = Color(0xFF8B0000)),
    VehicleColor(id = 24, name = "Light Red", color = Color(0xFFFF6347)),
    VehicleColor(id = 25, name = "Dark Silver", color = Color(0xFF696969)),
    VehicleColor(id = 26, name = "Light Silver", color = Color(0xFFD8D8D8)),
    VehicleColor(id = 27, name = "Dark Brown", color = Color(0xFF5B3B0A)),
    VehicleColor(id = 28, name = "Light Brown", color = Color(0xFFD2B48C)),
    VehicleColor(id = 29, name = "Dark Beige", color = Color(0xFFD2B48C)),
    VehicleColor(id = 30, name = "Light Beige", color = Color(0xFFF5F5DC), isLight = true)
)

val mainColors = vehiclesColors.subList(0, 5).toMutableList()

val String.asVehicleColor: VehicleColor? get() = vehiclesColors.find { it.name == this }