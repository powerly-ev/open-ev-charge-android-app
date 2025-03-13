package com.powerly.ui.components

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.widget.RatingBar
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.StarHalf
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.theme.AppTheme
import kotlin.math.ceil
import kotlin.math.floor


@Preview(locale = "ar")
@Composable
fun DynamicRatingPreview() {
    AppTheme {
        var rating by remember { mutableDoubleStateOf(0.0) }
        MyColumn {
            Text("rating = $rating")
            DynamicRatingBar(
                rating = 4.0,
                modifier = Modifier.scale(0.8f),
                onRate = { rating = it }
            )
        }
    }
}


@Composable
fun DynamicRatingBar(
    modifier: Modifier = Modifier,
    rating: Double = 0.0,
    stars: Int = 5,
    stepSize: Float = 1.0f,
    color: Color = MaterialTheme.colorScheme.primary,
    onRate: (Double) -> Unit = {}
) {

    AndroidView(
        modifier = modifier,
        factory = { context ->
            RatingBar(
                context,
                null,
                android.R.attr.ratingBarStyleIndicator
            ).apply {
                this.stepSize = stepSize
                this.numStars = stars
                this.setIsIndicator(false)
                val drawable = this.progressDrawable
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    drawable.colorFilter = BlendModeColorFilter(
                        color.toArgb(),
                        BlendMode.SRC_ATOP
                    )
                } else {
                    val colorArgb = color.toArgb()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        drawable?.colorFilter = BlendModeColorFilter(colorArgb, BlendMode.SRC_ATOP)
                    } else {
                        @Suppress("DEPRECATION")
                        drawable.setColorFilter(colorArgb, PorterDuff.Mode.SRC_ATOP)
                    }
                }
                this.rating = rating.toFloat()
                this.onRatingBarChangeListener =
                    RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                        onRate(rating.toDouble())
                    }
            }
        }
    )
}

@Preview(locale = "en")
@Composable
fun RatingPreviewFull() {
    RatingBar(rating = 4.5, starSize = 30.dp)
}

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Double = 0.0,
    stars: Int = 5,
    starsColor: Color = MaterialTheme.colorScheme.primary,
    showLabel: Boolean = false,
    labelColor: Color = Color.Black,
    starSize: Dp = 28.dp,
) {
    val filledStars = floor(rating).toInt()
    val unfilledStars = (stars - ceil(rating)).toInt()
    val halfStar = !(rating.rem(1).equals(0.0))
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(filledStars) {
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = null,
                tint = starsColor,
                modifier = Modifier.size(starSize)
            )
        }
        if (halfStar) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.StarHalf,
                contentDescription = null,
                tint = starsColor,
                modifier = Modifier.size(starSize),
            )
        }
        repeat(unfilledStars) {
            Icon(
                imageVector = Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = starsColor,
                modifier = Modifier.size(starSize)
            )
        }

        if (showLabel) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = rating.toString(), color = labelColor)
        }
    }
}
