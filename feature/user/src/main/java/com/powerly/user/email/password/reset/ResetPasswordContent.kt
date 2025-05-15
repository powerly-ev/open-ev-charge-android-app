package com.powerly.user.email.password.reset

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.powerly.user.email.PASSWORD_MIN_LENGTH
import com.powerly.user.email.checkPasswordStrength
import com.powerly.user.email.verify.ComposePinInput
import com.powerly.user.email.verify.ComposePinInputStyle
import com.powerly.resources.R
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.components.ButtonSmall
import com.powerly.ui.components.MyPasswordTextField
import com.powerly.ui.containers.LayoutDirectionLtr
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.containers.MyRow
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.components.MyTextDynamic
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.extensions.onClick
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import com.powerly.user.email.verify.SectionCounter
import com.powerly.user.email.verify.VerificationEvents
import java.util.Locale
import java.util.concurrent.TimeUnit


private const val TAG = "VerificationScreen"

@Preview
@Composable
private fun VerificationScreenPreview() {
    AppTheme {
        ResetPasswordScreenContent(
            resetPin = { false },
            timeout = { 60 },
            resetCounter = { false },
            userId = "m@powerly.com",
            uiEvents = {}
        )
    }
}

@Composable
internal fun ResetPasswordScreenContent(
    screenState: ScreenState = rememberScreenState(),
    timeout: () -> Int,
    resetPin: () -> Boolean,
    resetCounter: () -> Boolean,
    userId: String,
    uiEvents: (EmailResetEvents) -> Unit
) {
    var pinCode by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }

    var strength by remember {
        mutableStateOf(
            if (password.isBlank()) null
            else checkPasswordStrength(password)
        )
    }
    var invalidPassword by remember { mutableStateOf(false) }

    fun validatePassword(): Boolean {
        if (password.length < PASSWORD_MIN_LENGTH ||
            password != password2
        ) {
            invalidPassword = true
            return false
        }
        return true
    }

    MyScreen(
        screenState = screenState,
        background = Color.White,
        header = {
            ScreenHeader(
                title = stringResource(R.string.login_Verification_title),
                closeIcon = null
            )
        },
        modifier = Modifier.padding(16.dp),
        spacing = 0.dp,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        MyTextDynamic(
            text = stringResource(R.string.login_enter_code),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(Modifier.height(8.dp))
        MyRow {
            Text(
                text = userId,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
            ButtonSmall(
                text = stringResource(id = R.string.edit),
                background = MaterialTheme.colorScheme.secondary,
                height = 24.dp,
                modifier = Modifier.wrapContentWidth(),
                onClick = { uiEvents(EmailResetEvents.Edit) }
            )
        }
        Spacer(Modifier.height(16.dp))
        SectionPinCode(
            resetPin = resetPin,
            onEnter = { pinCode = it }
        )
        SectionCounter(
            timeout = timeout(),
            resetCounter = resetCounter,
            onResend = { uiEvents(EmailResetEvents.ResendPin) }
        )
        Spacer(Modifier.height(16.dp))
        MyPasswordTextField(
            value = password,
            label = stringResource(R.string.login_email_password_new),
            onValueChange = {
                password = it
                if (it.isNotBlank()) invalidPassword = false
            },
            isError = { invalidPassword },
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    contentType = ContentType.Password
                }
        )
        MyPasswordTextField(
            value = password2,
            label = stringResource(R.string.login_email_password_confirm),
            onValueChange = {
                password2 = it
                if (it.isNotBlank()) invalidPassword = false
            },
            error = { passErrorMessage(password, password2) },
            isError = { invalidPassword },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.weight(1f))
        ButtonLarge(
            text = stringResource(id = R.string.next),
            color = Color.White,
            background = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth(),
            icon = R.drawable.arrow_right,
            disabledBackground = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
            enabled = { pinCode.isNotBlank() },
            onClick = {
                if (validatePassword()) {
                    uiEvents(EmailResetEvents.Next(pinCode, password))
                }
            }
        )
    }
}

@Composable
private fun passErrorMessage(
    password: String,
    password2: String
): String {
    return if (password.length < PASSWORD_MIN_LENGTH) {
        stringResource(
            R.string.login_email_password_length_msg,
            PASSWORD_MIN_LENGTH
        )
    } else if (password != password2) {
        stringResource(R.string.login_email_password_not_matched)
    } else ""
}

@Composable
private fun SectionPinCode(
    resetPin: () -> Boolean,
    onEnter: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }
    LaunchedEffect(resetPin()) {
        pin = ""
    }
    LayoutDirectionLtr {
        ComposePinInput(
            value = pin,
            maxSize = 4,
            cellSize = 50.dp,
            showKeyboard = true,
            onValueChange = { pin = it },
            rowPadding = 32.dp,
            textFontSize = 16.sp,
            fontColor = MaterialTheme.colorScheme.secondary,
            focusedCellBorderColor = MaterialTheme.colorScheme.primary,
            cellBackgroundColor = MyColors.grey200,
            cellColorOnSelect = MyColors.grey200,
            cellBorderColor = MyColors.grey200,
            cellShape = RoundedCornerShape(8.dp),
            onPinEntered = onEnter,
            style = ComposePinInputStyle.BOX
        )
    }
}

@Composable
private fun SectionCounter(
    resetCounter: () -> Boolean,
    timeout: Int,
    onResend: () -> Unit,
    onHelp: () -> Unit
) {

    var count by remember { mutableStateOf("") }
    var showCount by remember { mutableStateOf(true) }
    var showResend by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }

    LaunchedEffect(resetCounter()) {
        Log.i(TAG, "reset-counter")
        showCount = true
        showResend = false
        showHelp = false
        count = ""
        startTimer(
            timeout = timeout,
            onCount = { count = it },
            showHelp = { showHelp = true },
            onFinish = {
                showResend = true
                showCount = false
            }
        )
    }

    MyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        spacing = 0.dp
    ) {
        MyRow {
            if (showCount) {
                Text(
                    text = stringResource(R.string.login_resend_code_in),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = count,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            if (showResend) Text(
                text = stringResource(R.string.login_resend_code),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.onClick(onResend)
            )
        }
        if (showHelp) {
            Spacer(Modifier.height(16.dp))
            ButtonSmall(
                text = stringResource(id = R.string.login_need_help),
                background = MyColors.grey200,
                color = MaterialTheme.colorScheme.secondary,
                height = 36.dp,
                fontSize = 14.sp,
                cornerRadius = 16.dp,
                modifier = Modifier.wrapContentWidth(),
                padding = PaddingValues(horizontal = 8.dp),
                onClick = onHelp
            )
        }
    }
}

private var counter: CountDownTimer? = null

private fun startTimer(
    timeout: Int,
    onCount: (String) -> Unit,
    showHelp: () -> Unit,
    onFinish: () -> Unit
) {
    Log.v(TAG, "startTimer")
    if (counter != null) {
        counter?.cancel()
        counter = null
    }
    counter = object : CountDownTimer(TimeUnit.SECONDS.toMillis(timeout.toLong()) + 1000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            var seconds = (millisUntilFinished / 1000).toInt()
            val minutes = seconds / 60
            seconds %= 60
            val count = String.format(Locale.US, "%02d", minutes) +
                    ":" + String.format(Locale.US, "%02d", seconds)
            onCount(count)
            if (minutes <= 1) showHelp()
        }

        override fun onFinish() {
            onFinish()
        }
    }
    counter?.start()
}