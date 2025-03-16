package com.powerly.account.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.powerly.core.data.model.CurrenciesStatus
import com.powerly.core.model.location.AppCurrency
import com.powerly.core.model.user.User
import com.powerly.account.profile.dialogs.CurrencyDropMenu
import com.powerly.account.profile.dialogs.ProfileDropMenu
import com.powerly.resources.R
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.components.ButtonText
import com.powerly.ui.components.MyIcon
import com.powerly.ui.components.MyPasswordTextField
import com.powerly.ui.components.MyTextField
import com.powerly.ui.containers.LayoutDirectionLtr
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.containers.MyRow
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.dialogs.rememberMyDialogState
import com.powerly.ui.extensions.onClick
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Preview
@Composable
private fun ProfileScreenPreview() {
    val user = User(
        id = 1234,
        firstName = "Mahmoud",
        lastName = "Aly",
        email = "m.aly@gasable.com",
        appCurrency = "USD",
        vatId = "22222"
    )
    val currencies = listOf(
        AppCurrency("USD"),
        AppCurrency("EUR"),
        AppCurrency("EGP"),
        AppCurrency("JP")
    )
    val status = CurrenciesStatus.Success(currencies)
    AppTheme {
        AppTheme {
            ProfileScreenContent(
                user = user,
                country = { "Egypt" },
                screenState = rememberScreenState(),
                currenciesFlow = flow { emit(status) },
                focusPassword = false,
                uiEvents = {}
            )
        }
    }
}

@Composable
internal fun ProfileScreenContent(
    screenState: ScreenState,
    user: User,
    country: () -> String,
    focusPassword: Boolean,
    currenciesFlow: Flow<CurrenciesStatus>,
    uiEvents: (ProfileEvents) -> Unit
) {
    var firstName by remember { mutableStateOf(user.firstName) }
    var firstNameInvalid by remember { mutableStateOf(false) }

    var lastName by remember { mutableStateOf(user.lastName) }

    var email by remember { mutableStateOf(user.email) }
    var emailInvalid by remember { mutableStateOf(false) }

    var vatId by remember { mutableStateOf(user.vatId.orEmpty()) }

    var password by remember { mutableStateOf("") }


    fun onSave() {
        if (firstName.isEmpty()) {
            firstNameInvalid = true
            return
        }
        if (email.isEmpty()) {
            emailInvalid = true
            return
        }
        val newUser = user.copy(
            firstName = firstName,
            lastName = lastName,
            email = email,
            vatId = vatId
        )
        uiEvents(ProfileEvents.Save(newUser, password.ifBlank { null }))
    }

    MyScreen(
        screenState = screenState,
        modifier = Modifier.padding(0.dp),
        verticalScroll = true,
        spacing = 0.dp,
        background = Color.White,
        header = {
            SectionHeader(
                onClose = { uiEvents(ProfileEvents.Close) },
                onDeleteAccount = { uiEvents(ProfileEvents.DeleteAccount) }
            )
        }
    ) {
        SectionTop(
            country = country(),
            currency = user.currency,
            currenciesFlow = currenciesFlow,
            onSelectCountry = { uiEvents(ProfileEvents.SelectCountry) },
            onSelectCurrency = { uiEvents(ProfileEvents.SelectCurrency(it)) },
            onSignOut = { uiEvents(ProfileEvents.SignOut) }
        )
        MyColumn(
            spacing = 0.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            MyTextField(
                value = firstName,
                modifier = Modifier.fillMaxWidth(),
                error = { stringResource(R.string.login_enter_first_name) },
                isError = { firstNameInvalid },
                label = stringResource(R.string.login_first_name),
                onValueChange = {
                    firstNameInvalid = false
                    firstName = it
                }
            )
            MyTextField(
                value = lastName,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { lastName = it },
                label = stringResource(R.string.login_last_name)
            )
            MyTextField(
                value = email,
                error = { stringResource(R.string.profile_require_email_address) },
                isError = { emailInvalid },
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.profile_email),
                enabled = false,
                onValueChange = {
                    emailInvalid = false
                    email = it
                }
            )
            MyTextField(
                value = vatId,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { vatId = it },
                label = stringResource(R.string.order_vatId)
            )
            MyPasswordTextField(
                value = password,
                modifier = Modifier.fillMaxWidth(),
                onValueChange = { password = it },
                label = stringResource(R.string.login_password),
                showKeyboard = focusPassword
            )
            ButtonLarge(
                text = stringResource(R.string.save),
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                background = MaterialTheme.colorScheme.secondary,
                onClick = { onSave() }
            )
        }
    }
}

@Composable
private fun SectionHeader(
    onClose: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    var showOptionsMenu by remember { mutableStateOf(false) }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.close),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.onClick(onClose)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.profile),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Box {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_more_horiz_24),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.clickable {
                        showOptionsMenu = true
                    }
                )

                ProfileDropMenu(
                    show = { showOptionsMenu },
                    onDismiss = { showOptionsMenu = false },
                    onDeleteAccount = onDeleteAccount
                )
            }
        }
    }
}

@Composable
private fun SectionTop(
    country: String?,
    currency: String,
    onSelectCountry: () -> Unit,
    onSelectCurrency: (AppCurrency) -> Unit,
    currenciesFlow: Flow<CurrenciesStatus>,
    onSignOut: () -> Unit
) {
    LayoutDirectionLtr {
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFDDE8F0)
        ) {
            MyColumn(
                modifier = Modifier.padding(16.dp),
                spacing = 0.dp,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MyRow(modifier = Modifier.fillMaxWidth()) {
                    CountryButton(
                        country = country,
                        onSelectCountry = onSelectCountry
                    )
                    Spacer(Modifier.weight(1f))
                    ButtonText(
                        text = stringResource(id = R.string.logout_title),
                        background = Color.Transparent,
                        color = MyColors.red500,
                        fontSize = 16.sp,
                        onClick = onSignOut,
                        modifier = Modifier.width(IntrinsicSize.Max)
                    )
                }
                Image(
                    painter = painterResource(R.drawable.ic_user_avatar),
                    contentDescription = null,
                    modifier = Modifier.size(84.dp)
                )
                Spacer(Modifier.height(32.dp))
                SectionCurrency(
                    currency = currency,
                    currenciesFlow = currenciesFlow,
                    onSelectCurrency = onSelectCurrency
                )
            }
        }
    }
}

@Composable
private fun SectionCurrency(
    currenciesFlow: Flow<CurrenciesStatus>,
    currency: String,
    onSelectCurrency: (AppCurrency) -> Unit,
) {
    var dialogState = rememberMyDialogState()
    var selectedCurrency by remember { mutableStateOf(currency) }

    Box(Modifier.fillMaxWidth()) {
        CurrencyDropMenu(
            state = dialogState,
            currenciesFlow = currenciesFlow,
            onSelectCurrency = {
                selectedCurrency = it.iso
                onSelectCurrency(it)
            }
        )

        TextButton(
            onClick = { dialogState.show() },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = selectedCurrency,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            MyIcon(R.drawable.ic_arrow_down)
        }
    }
}

@Composable
private fun CountryButton(
    country: String?,
    onSelectCountry: () -> Unit
) {
    if (country.isNullOrEmpty()) ButtonText(
        text = stringResource(R.string.profile_select_country),
        background = Color.Transparent,
        color = MaterialTheme.colorScheme.secondary,
        fontSize = 16.sp,
        onClick = onSelectCountry,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            textDecoration = TextDecoration.Underline
        ),
        modifier = Modifier.width(IntrinsicSize.Max)
    ) else Text(
        text = country,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
private fun LockedIcon() = MyIcon(R.drawable.ic_lockicon)