package com.powerly.powerSource.reviews.list

import androidx.compose.runtime.Composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.powerly.powerSource.PsViewModel
import com.powerly.ui.dialogs.MyScreenBottomSheet
import com.powerly.ui.dialogs.loading.rememberBasicScreenState
import com.powerly.ui.dialogs.success.SuccessState

private const val TAG = "ReviewScreen"

/**
 * Displays the review for a power source.
 *
 * This composable function retrieves and displays the review for the specified power source.
 * It shows a loading indicator while fetching the review and presents the review content once available.
 *
 * @param viewModel The ViewModel responsible for fetching and managing review data.
 * @param onBack A callback function to handle the back navigation event.
 */
@Composable
fun ReviewScreen(
    viewModel: PsViewModel,
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
