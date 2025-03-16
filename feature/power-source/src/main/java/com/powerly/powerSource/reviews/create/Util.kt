package com.powerly.powerSource.reviews.create

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.powerly.resources.R


internal data class Emoji(
    val id: Int,
    @StringRes val label: Int,
    @DrawableRes val icon: Int
)

internal val emojis = listOf(
    Emoji(
        id = 1,
        R.string.feedback_terrible,
        R.drawable.feedback_terrible
    ),
    Emoji(
        id = 2,
        R.string.feedback_bad,
        R.drawable.feedback_bad
    ),
    Emoji(
        id = 3,
        R.string.feedback_fine,
        R.drawable.feedback_fine
    ),
    Emoji(
        id = 4,
        R.string.feedback_good,
        R.drawable.feedback_good
    ),
    Emoji(
        id = 5,
        R.string.feedback_excellent,
        R.drawable.feedback_excellent
    )
)