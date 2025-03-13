package com.powerly.ui.components


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.powerly.resources.R
import com.powerly.ui.theme.MyColors


@Composable
fun ButtonLarge(
    text: String,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.secondary,
    disabledBackground: Color = MyColors.disabledColor,
    color: Color = Color.White,
    disabledColor: Color = color,
    cornerRadius: Dp = 8.dp,
    @DrawableRes icon: Int? = null,
    iconSize: Dp = ButtonDefaults.IconSize,
    enabled: () -> Boolean = { true },
    padding: PaddingValues = PaddingValues(horizontal = 16.dp),
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    elevation: Dp = 0.dp,
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    border: BorderStroke? = null,
    onClick: (() -> Unit)? = null
) {
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Button(
            onClick = { onClick?.invoke() },
            modifier = Modifier
                .height(55.dp)
                .then(modifier),
            colors = ButtonDefaults.buttonColors(
                containerColor = background,
                disabledContainerColor = disabledBackground
            ),
            enabled = enabled(),
            contentPadding = padding,
            shape = RoundedCornerShape(cornerRadius),
            elevation = ButtonDefaults.buttonElevation(elevation),
            border = border,
        ) {
            Text(
                text,
                maxLines = 1,
                color = if (enabled()) color else disabledColor,
                style = textStyle,
                overflow = TextOverflow.Ellipsis
            )
            icon?.let {
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Icon(
                    painterResource(icon),
                    contentDescription = "",
                    tint = if (enabled()) color else disabledColor,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

@Composable
fun ButtonAction(
    text: String,
    background: Color = MyColors.viewColor3,
    color: Color = MaterialTheme.colorScheme.secondary,
    height: Dp = 30.dp,
    width: Dp = 60.dp,
    border: BorderStroke? = null,
    cornerRadius: Dp = 4.dp,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        Modifier
            .height(height)
            .wrapContentWidth()
            .defaultMinSize(minWidth = width),
        colors = ButtonDefaults.buttonColors(containerColor = background),
        contentPadding = PaddingValues(0.dp),
        border = border,
        shape = RoundedCornerShape(cornerRadius),
    ) {
        Text(
            text,
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall.copy(
                color = color,
            )
        )
    }
}

@Composable
fun ButtonText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary,
    background: Color = Color.Transparent,
    textAlign: TextAlign = TextAlign.Center,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    paddingHorizontal: Dp = 8.dp,
    fontSize: TextUnit = 14.sp,
    isBold: Boolean = false,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = paddingHorizontal, vertical = 0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = background)
    ) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            style = textStyle,
            textAlign = textAlign,
            fontSize = fontSize,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = color
        )
    }
}

@Composable
fun ButtonSmall(
    text: String,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.primary,
    disabledBackground: Color = background,
    height: Dp = 25.dp,
    color: Color = Color.White,
    disabledColor: Color = color,
    enabled: () -> Boolean = { true },
    padding: PaddingValues = PaddingValues(0.dp),
    fontSize: TextUnit = 12.sp,
    cornerRadius: Dp = 4.dp,
    border: BorderStroke? = null,
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    @DrawableRes icon: Int? = null,
    iconSize: Dp = ButtonDefaults.IconSize,
    iconTint: Color = color,
    onClick: (() -> Unit)? = null
) {
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Button(
            onClick = { onClick?.invoke() },
            modifier = Modifier
                .height(height)
                .wrapContentWidth()
                .then(modifier),
            colors = ButtonDefaults.buttonColors(
                containerColor = background,
                disabledContainerColor = disabledBackground
            ),
            contentPadding = padding,
            enabled = enabled(),
            shape = RoundedCornerShape(cornerRadius),
            border = border
        ) {
            Text(
                text,
                style = MaterialTheme.typography.bodySmall,
                fontSize = fontSize,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp),
                color = if (enabled()) color else disabledColor
            )
            icon?.let {
                Spacer(Modifier.width(4.dp))
                Icon(
                    painterResource(icon),
                    contentDescription = "",
                    tint = iconTint,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

@Composable
fun ButtonRound(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int = R.drawable.ic_add,
    background: Color = MaterialTheme.colorScheme.primary,
    color: Color = Color.White,
    border: BorderStroke? = null,
    elevation: ButtonElevation? = null,
    iconPadding: Dp = 0.dp,
    size: Dp = 24.dp,
    onClick: (() -> Unit)? = null
) {

    Button(
        modifier = modifier.then(Modifier.size(size)),
        shape = CircleShape,
        border = border,
        elevation = elevation,
        colors = ButtonDefaults.buttonColors(background),
        contentPadding = PaddingValues(0.dp),  // avoid the little icon
        onClick = { onClick?.invoke() }) {
        Icon(
            painterResource(id = icon),
            contentDescription = "",
            tint = color, modifier = Modifier
                .size(size)
                .padding(iconPadding)
        )
    }
}

@Composable
fun RoundBadge(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 11.sp,
    background: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = Modifier
            .zIndex(2f)
            .size(20.dp)
            .background(
                color = background,
                shape = CircleShape
            )
            .then(modifier)
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            maxLines = 1,
            style = MaterialTheme.typography.labelSmall.copy(
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = fontSize
            ), modifier = Modifier
                .align(alignment = Alignment.Center)
        )
    }
}


@Composable
fun MySwitch(
    checked: () -> Boolean,
    onCheckedChange: (Boolean) -> Unit,
    checkedColor: Color = MyColors.green,
    uncheckedColor: Color = MyColors.grey250
) {
    Switch(
        checked = checked(),
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            uncheckedThumbColor = MyColors.white,
            checkedThumbColor = MyColors.white,
            uncheckedTrackColor = uncheckedColor,
            checkedTrackColor = checkedColor,
            checkedBorderColor = Color.Transparent,
            uncheckedBorderColor = Color.Transparent
        )
    )
}

@Composable
fun MyCheckBox(
    @StringRes label: Int,
    checked: () -> Boolean,
    modifier: Modifier = Modifier,
    dynamicLabel: Boolean = false,
    space: Dp = 8.dp,
    onClicked: () -> Unit
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            modifier = Modifier.size(20.dp),
            selected = checked(),
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary
            ),
            onClick = onClicked
        )
        Spacer(modifier = Modifier.width(space))
        if (dynamicLabel.not()) Text(
            text = stringResource(id = label),
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            color = MaterialTheme.colorScheme.secondary
        ) else MyTextDynamic(
            text = stringResource(id = label),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

