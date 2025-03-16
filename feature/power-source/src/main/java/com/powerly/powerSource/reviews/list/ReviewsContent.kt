package com.powerly.powerSource.reviews.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.powerly.core.model.powerly.Review
import com.powerly.core.model.powerly.Reviewer
import com.powerly.resources.R
import com.powerly.ui.components.ButtonSmall
import com.powerly.ui.components.NetworkImage
import com.powerly.ui.components.RatingBar
import com.powerly.ui.containers.MyCardColum
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.containers.MyRow
import com.powerly.ui.dialogs.ProgressView
import com.powerly.ui.dialogs.loading.ScreenState
import com.powerly.ui.dialogs.loading.rememberScreenState
import com.powerly.ui.extensions.asPadding
import com.powerly.ui.screen.MyScreen
import com.powerly.ui.screen.ScreenHeader
import com.powerly.ui.theme.AppTheme
import com.powerly.ui.theme.MyColors
import kotlinx.coroutines.flow.flow

@Preview
@Composable
private fun ReviewsScreenPreview() {
    val review = Review(
        id = 450,
        user = Reviewer(1, "John", "Dee"),
        orderId = 123,
        content = "Super Fast Charging",
        rating = 5.0,
        createdAt = "2025-02-13T09:18:42.000000Z"
    )
    val reviews = listOf(review, review, review)
    val lazyPagingItems = flow {
        emit(PagingData.from(reviews))
    }.collectAsLazyPagingItems()

    AppTheme {
        ReviewScreenContent(
            reviews = lazyPagingItems,
            onClose = {},
            onHelpFul = {}
        )
    }
}


@Composable
internal fun ReviewScreenContent(
    screenState: ScreenState = rememberScreenState(),
    reviews: LazyPagingItems<Review>,
    onHelpFul: (Review) -> Unit,
    onClose: () -> Unit
) {
    MyScreen(
        screenState = screenState,
        header = {
            ScreenHeader(
                title = stringResource(id = R.string.station_reviews),
                textAlign = TextAlign.Center,
                onClose = onClose
            )
        },
        spacing = 8.dp,
        modifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = 8.dp
        )
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.Top
            ),
            modifier = Modifier.fillMaxHeight()
        ) {
            items(
                count = reviews.itemCount,
                key = reviews.itemKey(),
                contentType = reviews.itemContentType()
            ) { i ->
                val review = reviews[i] ?: return@items
                ItemReview(
                    review = review,
                    onHelpFul = { onHelpFul(review) }
                )
            }

            // Show loading indicator during refresh
            if (reviews.loadState.refresh is LoadState.Loading ||
                reviews.loadState.append is LoadState.Loading ||
                reviews.loadState.prepend is LoadState.Loading
            ) {
                item {
                    ProgressView()
                }
            }
        }
    }
}

@Composable
private fun ItemReview(
    review: Review,
    onHelpFul: () -> Unit
) {
    MyCardColum(
        fillMaxWidth = true,
        padding = 16.dp.asPadding,
        borderStroke = null,
        horizontalAlignment = Alignment.Start,
        spacing = 8.dp
    ) {
        MyColumn {
            MyRow(modifier = Modifier.fillMaxWidth()) {
                ItemReviewerImage(review.user)
                Text(
                    text = review.user.userName,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = review.createdAt(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            RatingBar(rating = review.rating)
            Text(
                text = review.content.orEmpty(),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            var isHelpful by remember { mutableStateOf(false) }
            ButtonSmall(
                background = MyColors.viewColor,
                text = stringResource(id = R.string.station_reviews_helpful),
                color = MaterialTheme.colorScheme.secondary,
                padding = PaddingValues(horizontal = 4.dp),
                layoutDirection = LayoutDirection.Rtl,
                icon = if (isHelpful) R.drawable.helpful_checked
                else R.drawable.helpful,
                iconTint = if (isHelpful) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary,
                height = 30.dp,
                onClick = {
                    isHelpful = true
                    onHelpFul()
                }
            )
        }
    }
}


@Composable
private fun ItemReviewerImage(reviewer: Reviewer) {
    Surface(
        modifier = Modifier.size(50.dp),
        shape = CircleShape
    ) {
        Box(Modifier.fillMaxSize()) {
            Text(
                reviewer.photoAlt,
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}