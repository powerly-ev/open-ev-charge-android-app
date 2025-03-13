package com.powerly.user.email.verify

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


enum class ComposePinInputStyle {
    BOX,
    UNDERLINE
}

/**
 * https://raw.githubusercontent.com/sharp-edge/ComposePinInput/main/PinTextFieldLib/src/main/java/com/sharpedge/pintextfield/ComposePinInput.kt
 */

@Composable
fun ComposePinInput(
    value: String,
    onValueChange: (String) -> Unit,
    maxSize: Int = 4,
    mask: Char? = null,
    isError: Boolean = false,
    showKeyboard: Boolean = true,
    onPinEntered: ((String) -> Unit)? = null,
    cellShape: Shape = RoundedCornerShape(4.dp),
    fontColor: Color = Color.LightGray,
    cellBorderColor: Color = Color.Gray,
    rowPadding: Dp = 8.dp,
    cellPadding: Dp = 16.dp,
    cellSize: Dp = 50.dp,
    cellBorderWidth: Dp = 1.dp,
    textFontSize: TextUnit = 20.sp,
    focusedCellBorderColor: Color = Color.DarkGray,
    errorBorderColor: Color = Color.Red,
    cellBackgroundColor: Color = Color.Transparent,
    cellColorOnSelect: Color = Color.Transparent,
    borderThickness: Dp = 2.dp,
    style: ComposePinInputStyle = ComposePinInputStyle.BOX
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusedState = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    fun showKeyboard() {
        keyboardController?.show()
        focusRequester.requestFocus()
        focusedState.value = true
    }

    fun onPinComplete(pin: String) {
        keyboardController?.hide()
        focusManager.clearFocus(true)
        onPinEntered?.invoke(pin)
    }

    LaunchedEffect(Unit) {
        if (showKeyboard) {
            delay(500)
            showKeyboard()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        BasicTextField(
            value = value,
            onValueChange = { text ->
                if (text.length <= maxSize) {
                    onValueChange(text)
                    if (text.length == maxSize) onPinComplete(text)
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (value.length == maxSize) onPinComplete(value)
                }
            ),
            modifier = Modifier
                .alpha(0.01f)
                .onFocusChanged { focusedState.value = it.isFocused }
                .focusRequester(focusRequester)
                .clickable { showKeyboard() },
            textStyle = TextStyle.Default.copy(color = Color.Transparent)
        )

        // UI for the Pin
        val boxWidth = cellSize
        Row(
            horizontalArrangement = Arrangement.spacedBy(cellPadding),
            modifier = Modifier.padding(rowPadding)
        ) {
            repeat(maxSize) { index ->
                val isActiveBox = focusedState.value && index == value.length

                if (style == ComposePinInputStyle.BOX) {
                    // Box Style Pin field logic starts from here
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .background(
                                color = if (index < value.length) cellColorOnSelect
                                else cellBackgroundColor,
                                shape = cellShape
                            )
                            .border(
                                width = cellBorderWidth,
                                color = when {
                                    isError -> errorBorderColor
                                    isActiveBox -> focusedCellBorderColor
                                    else -> cellBorderColor
                                },
                                shape = cellShape
                            )
                            .clickable(
                                indication = null, // Disable ripple effect
                                interactionSource = remember { MutableInteractionSource() }
                            ) { showKeyboard() }
                    ) {
                        if (index < value.length) {
                            val displayChar = mask ?: value[index]
                            Text(
                                text = displayChar.toString(),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .clickable(
                                        indication = null, // Disable ripple effect
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) { showKeyboard() },
                                fontSize = textFontSize,
                                color = fontColor
                            )
                        }
                    }
                } else {
                    // Underline style logic here
                    Box(
                        modifier = Modifier
                            .size(boxWidth, cellSize + borderThickness)
                            .background(color = if (index < value.length) cellColorOnSelect else cellBackgroundColor)
                            .clickable(
                                indication = null, // Disable ripple effect
                                interactionSource = remember { MutableInteractionSource() }
                            ) { showKeyboard() }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
                            drawLine(
                                color = when {
                                    isError -> errorBorderColor
                                    isActiveBox -> focusedCellBorderColor
                                    else -> cellBorderColor
                                },
                                start = Offset(x = 0f, y = size.height - borderThickness.toPx()),
                                end = Offset(
                                    x = size.width,
                                    y = size.height - borderThickness.toPx()
                                ),
                                strokeWidth = borderThickness.toPx()
                            )
                        })

                        if (index < value.length) {
                            val displayChar = mask ?: value[index]
                            val lineHeightDp: Dp = with(LocalDensity.current) {
                                textFontSize.toDp()
                            }
                            Text(
                                text = displayChar.toString(),
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = (cellSize - lineHeightDp) / 2),
                                fontSize = textFontSize,
                                color = fontColor
                            )
                        }
                    }
                }
            }
        }
    }
}