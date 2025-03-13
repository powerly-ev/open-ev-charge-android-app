package com.powerly.ui.extensions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.Dp
import com.powerly.ui.theme.MyColors

@Composable
fun isArabic(): Boolean = Locale.current.language.contains("ar")
@Composable
fun isPreview(): Boolean = LocalInspectionMode.current
fun isArabic2(): Boolean = java.util.Locale.getDefault().language.contains("ar")

fun Modifier.onClick(onClick: (() -> Unit)?, enabled: Boolean = true): Modifier =
    if (onClick != null && enabled) this.clickable(onClick = onClick) else this

fun Modifier.thenIf(condition: Boolean, other: Modifier): Modifier =
    if (condition) this.then(other) else this

fun Modifier.thenIf(
    condition: Boolean,
    case1: Modifier,
    case2: Modifier,
): Modifier = if (condition) this.then(case1) else this.then(case2)

val Dp.asPadding: PaddingValues get() = PaddingValues(this)
val Dp.asBorder: BorderStroke get() = BorderStroke(this, MyColors.borderColor)


