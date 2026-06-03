package com.powerly.powersource.details.presentation.reviews

import androidx.compose.runtime.Composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.powerly.ui.dialogs.MyScreenBottomSheet
import com.powerly.ui.dialogs.loading.rememberBasicScreenState
import com.powerly.ui.dialogs.success.SuccessState
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReviewScreen(
    viewModel: ReviewsViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    MyScreenBottomSheet(onDismiss = onBack) {
        val screenState = rememberBasicScreenState(successState = SuccessState())
        val reviews = viewModel.reviews.collectAsLazyPagingItems()
        ReviewScreenContent(
            screenState = screenState,
            reviews = reviews,
            onClose = onBack,
            onHelpFul = { screenState.showSuccess() }
        )
    }
}
