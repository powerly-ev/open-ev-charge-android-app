package com.powerly.map.home.slider

import android.content.Context
import androidx.annotation.DrawableRes
import com.powerly.resources.R

internal data class SliderItem(
    val title: String,
    val subTitle: String,
    val actionTitle: String,
    val action: SliderAction,
    @DrawableRes val image: Int,
    @DrawableRes val actionIcon: Int?,
)

enum class SliderAction {
    JOIN_NOW,
    REFER_NOW,
    BUY_NOW
}

internal fun initSlides(
    context: Context,
    isLoggedIn: Boolean = true
): List<SliderItem> {
    val slides = mutableListOf<SliderItem>()

    val titles = context.resources.getStringArray(R.array.slider_title).toMutableList()
    val subTitles = context.resources.getStringArray(R.array.slider_sub_title).toMutableList()
    if (isLoggedIn) {
        titles.removeAt(2)
        subTitles.removeAt(2)
    } else {
        titles.removeAt(1)
        subTitles.removeAt(1)
    }
    val actionTitles = context.resources.getStringArray(R.array.slider_action)
    val actions = listOf(SliderAction.JOIN_NOW, SliderAction.REFER_NOW, SliderAction.BUY_NOW)
    val images = listOf(R.drawable.slide1, R.drawable.slide2, R.drawable.slide3)
    val actionIcons =
        listOf(R.drawable.facebook, R.drawable.dollar_symbol, R.drawable.charge_filled)

    for (i in titles.indices) {
        slides.add(
            SliderItem(
                title = titles[i],
                subTitle = subTitles[i],
                actionTitle = actionTitles[i],
                action = actions[i],
                image = images[i],
                actionIcon = actionIcons[i]
            )
        )
    }
    return slides
}