package com.powerly.powerSource.boarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.components.MyIcon
import com.powerly.ui.theme.AppTheme

@Preview(locale = "ar")
@Composable
private fun OnBoardingSlidePreview() {
    AppTheme {
        val items = onBoardingItems()
        OnBoardingSlide(onBoardingItem = items[0])
    }
}

@Composable
internal fun OnBoardingSlide(onBoardingItem: OnBoardingItem) {
    Surface(shape = RoundedCornerShape(16.dp)) {
        MyColumn(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            spacing = 24.dp
        ) {
            Surface(shape = RoundedCornerShape(16.dp)) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(ratio = 1.5f),
                    contentScale = ContentScale.Fit,
                    painter = painterResource(id = onBoardingItem.image),
                    contentDescription = onBoardingItem.title.text
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = onBoardingItem.title,
                inlineContent = if (onBoardingItem.titleIcon != null)
                    inlineContent(icon = onBoardingItem.titleIcon)
                else mapOf(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = onBoardingItem.description,
                style = MaterialTheme.typography.bodyMedium,
                inlineContent = if (onBoardingItem.descIcon != null)
                    inlineContent(icon = onBoardingItem.descIcon)
                else mapOf(),
                textAlign = TextAlign.Center,
                minLines = 3
            )
        }
    }
}

/**
 *  This tells the Text to replace the placeholder string "[id]" by
 *  the composable given in the [InlineTextContent] object.
 */
@Composable
private fun inlineContent(
    id: String = "icon",
    @DrawableRes icon: Int
) = mapOf(
    Pair(
        id,
        InlineTextContent(
            Placeholder(
                width = 1.2.em,
                height = 0.75.em,
                placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline
            )
        ) {
            MyIcon(icon = icon, modifier = Modifier.fillMaxWidth())
        }
    )
)

