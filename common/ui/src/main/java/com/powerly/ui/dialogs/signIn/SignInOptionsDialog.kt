package com.powerly.ui.dialogs.signIn

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.powerly.resources.R
import com.powerly.ui.containers.LayoutDirectionLtr
import com.powerly.ui.dialogs.MyBottomSheet
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.components.MyTextDynamic
import com.powerly.ui.dialogs.MyDialogState
import com.powerly.ui.dialogs.rememberMyDialogState
import com.powerly.ui.screen.DialogHeader
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import com.powerly.ui.theme.myBorder

/**
 * please start interactive mode to show bottom sheet preview
 */
@Preview
@Composable
private fun SignInOptionsScreenPreview() {
    AppTheme {
        Surface(color = Color.White) {
            SignInOptionsDialog(
                state = rememberMyDialogState(visible = true),
                showGuest = true,
                uiEvents = {},
            )
        }
    }
}

@Composable
fun SignInOptionsDialog(
    state: MyDialogState,
    showGuest: Boolean,
    uiEvents: (SignInOptions) -> Unit
) {
    MyBottomSheet(
        state=state,
        spacing = 16.dp,
        modifier = Modifier.padding(16.dp),
        background = MyColors.grey100,
        header = {
            DialogHeader(
                title = "",
                layoutDirection = LayoutDirection.Rtl,
                onClose = {state.dismiss()}
            )
        }
    ) {
        SignInButton(
            title = R.string.login_option_email,
            icon = R.drawable.sign_in_email,
            background = Color.White,
            color = MaterialTheme.colorScheme.secondary,
            border = myBorder,
            onClick = {
                state.dismiss()
                uiEvents(SignInOptions.Email)
            }
        )
        if (showGuest) SignInButton(
            title = R.string.login_option_guest,
            background = Color.White,
            color = MaterialTheme.colorScheme.secondary,
            border = myBorder,
            onClick = {
                state.dismiss()
                uiEvents(SignInOptions.Guest)
            }
        )
    }
}

@Preview
@Composable
private fun SignInButtonPreview() {
    AppTheme {
        Surface(color = Color.White) {
            MyColumn(Modifier.padding(16.dp)) {
                SignInButton(
                    title = R.string.login_option_google,
                    icon = R.drawable.sign_in_google,
                    background = MaterialTheme.colorScheme.secondary,
                    color = Color.White,
                    onClick = {}
                )
                SignInButton(
                    title = R.string.login_option_other,
                    background = Color.Transparent,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun SignInButton(
    @StringRes title: Int,
    @DrawableRes icon: Int? = null,
    background: Color = Color.Transparent,
    showArrow: Boolean = false,
    border: BorderStroke? = null,
    color: Color = MaterialTheme.colorScheme.secondary,
    iconTint: Color? = null,
    onClick: () -> Unit
) {
    LayoutDirectionLtr {
        Surface(
            onClick = onClick,
            border = border,
            color = background,
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 16.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (iconTint != null) {
                    Icon(
                        painter = painterResource(id = icon!!),
                        contentDescription = stringResource(id = title),
                        modifier = Modifier.size(24.dp),
                        tint = iconTint
                    )
                } else icon?.let {
                    Image(
                        painter = painterResource(id = it),
                        contentDescription = stringResource(id = title),
                        modifier = Modifier.size(24.dp)
                    )
                }
                MyTextDynamic(
                    text = stringResource(id = title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Normal
                    ),
                    color = color
                )
                if (showArrow) Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_down),
                    contentDescription = "",
                    tint = color
                )
            }
        }
    }
}