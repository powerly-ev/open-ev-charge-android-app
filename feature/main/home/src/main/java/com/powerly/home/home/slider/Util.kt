package com.powerly.home.home.slider

import android.content.Context
import androidx.annotation.DrawableRes
import com.powerly.resources.R

internal data class SliderItem(
    val title: String,
    val message: String,
    @DrawableRes val image: Int
)

internal fun initSlides(context: Context): List<SliderItem> {
    val slides = mutableListOf<SliderItem>()

    val titles = context.resources.getStringArray(R.array.slider_title).toMutableList()
    val messages = context.resources.getStringArray(R.array.slider_message).toMutableList()
    val images = listOf(R.drawable.slide1)

    for (i in titles.indices) {
        slides.add(
            SliderItem(
                title = titles[i],
                message = messages[i],
                image = images[i]
            )
        )
    }
    return slides
}