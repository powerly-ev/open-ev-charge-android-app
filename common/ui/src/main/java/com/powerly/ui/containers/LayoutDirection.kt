package com.powerly.ui.containers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.powerly.ui.extensions.isArabic
import com.powerly.ui.extensions.isArabic2

@Composable
fun LayoutDirectionRtl(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        content()
    }
}

@Composable
fun LayoutDirectionLtr(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        content()
    }
}

@Composable
fun LayoutDirectionAny(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalLayoutDirection provides if (isArabic()) LayoutDirection.Rtl
        else LayoutDirection.Ltr
    ) {
        content()
    }
}


val LayoutDirectionAny: LayoutDirection
    get() = if (isArabic2()) LayoutDirection.Rtl else LayoutDirection.Ltr
