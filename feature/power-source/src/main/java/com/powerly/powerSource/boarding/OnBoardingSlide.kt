package com.powerly.powerSource.boarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.powerly.ui.components.MyIcon
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.theme.AppTheme

@Preview(locale = "ar")
@Composable
private fun OnBoardingSlidePreview() {
    AppTheme {
        val items = onBoardingItems()
        OnBoardingSlide(
            item = items[0],
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 1.5f),
            cornerSize = 16.dp
        )
    }
}

@Composable
internal fun OnBoardingSlide(
    item: OnBoardingItem,
    modifier: Modifier,
    cornerSize: Dp
) {
    Surface(shape = RoundedCornerShape(cornerSize)) {
        MyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            spacing = 8.dp
        ) {
            Surface(shape = RoundedCornerShape(cornerSize)) {
                Image(
                    modifier = modifier,
                    contentScale = ContentScale.Fit,
                    painter = painterResource(id = item.image),
                    contentDescription = item.title.text
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                inlineContent = inlineContent(item.titleIcons),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                ),
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyLarge,
                inlineContent = inlineContent(item.innerIcons),
                textAlign = TextAlign.Center,
                minLines = 3,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 *  the composable given in the [InlineTextContent] object.
 */
@Composable
private fun inlineContent(
    icons: List<Int>
): Map<String, InlineTextContent> {
    val map = mutableMapOf<String, InlineTextContent>()
    icons.forEachIndexed { index, icon ->
        map["icon${index + 1}"] = InlineTextContent(
            Placeholder(
                width = 1.4.em,
                height = 0.95.em,
                placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline
            )
        ) {
            MyIcon(icon = icon, modifier = Modifier.fillMaxWidth())
        }
    }
    return map
}