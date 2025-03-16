package com.powerly.powerSource.boarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import com.powerly.resources.R
import com.powerly.ui.extensions.isArabic


internal data class OnBoardingItem(
    val index: Int,
    val title: AnnotatedString,
    val description: AnnotatedString,
    @DrawableRes val image: Int,
    @DrawableRes val titleIcon: Int? = null,
    @DrawableRes val descIcon: Int? = null
)

@Composable
internal fun onBoardingItems(): List<OnBoardingItem> = listOf(
    OnBoardingItem(
        index = 0,
        title = buildAnnotatedString {
            if (isArabic()) {
                pushStyle(SpanStyle(color = MaterialTheme.colorScheme.primary))
                append(stringResource(R.string.onboarding1_title2))
                appendInlineContent("icon")
                pushStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary))
                append("\n")
                append(stringResource(R.string.onboarding1_title1))
            } else {
                pushStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary))
                append(stringResource(R.string.onboarding1_title1))
                appendInlineContent("icon")
                pushStyle(SpanStyle(color = MaterialTheme.colorScheme.primary))
                append(stringResource(R.string.onboarding1_title2))
            }
        },
        description = buildAnnotatedString {
            append(stringResource(R.string.onboarding1_description))
        },
        image = R.drawable.onboarding1,
        titleIcon = R.drawable.location_enable
    ),
    OnBoardingItem(
        index = 1,
        title = buildAnnotatedString {
            append(stringResource(R.string.onboarding2_title))
        },
        description = buildAnnotatedString {
            append(stringResource(R.string.onboarding2_description1))
            appendInlineContent("icon")
            pushStyle(SpanStyle(color = MaterialTheme.colorScheme.primary))
            append(stringResource(R.string.onboarding2_description2))
            pushStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary))
            append(" ")
            append(stringResource(R.string.onboarding2_description3))
        },
        image = R.drawable.onboarding2,
        descIcon = R.drawable.calendar
    ),
    OnBoardingItem(
        index = 2,
        title = buildAnnotatedString {
            append(stringResource(R.string.onboarding3_title))
        },
        description = buildAnnotatedString {
            append(stringResource(R.string.onboarding3_description1))
            appendInlineContent("icon")
            pushStyle(SpanStyle(color = MaterialTheme.colorScheme.primary))
            append(stringResource(R.string.onboarding3_description2))
            pushStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary))
            append(" ")
            append(stringResource(R.string.onboarding3_description3))
        },
        image = R.drawable.onboarding3,
        descIcon = R.drawable.charge
    ),
    OnBoardingItem(
        index = 3,
        title = buildAnnotatedString {
            pushStyle(SpanStyle(color = MaterialTheme.colorScheme.primary))
            append(stringResource(R.string.onboarding4_title1))
            append(" ")
            pushStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary))
            append(stringResource(R.string.onboarding4_title2))
        },
        description = buildAnnotatedString {
            append(stringResource(R.string.onboarding4_description))
        },
        image = R.drawable.onboarding4
    )
)

