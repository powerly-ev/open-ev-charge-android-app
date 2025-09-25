package com.powerly.powerSource.reviews.create

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import org.koin.androidx.compose.koinViewModel
import com.powerly.core.model.api.ApiStatus
import com.powerly.ui.dialogs.MyBasicBottomSheet
import com.powerly.ui.dialogs.loading.LoadingDialog
import com.powerly.ui.dialogs.loading.rememberLoadingState
import com.powerly.ui.dialogs.success.SuccessDialog
import com.powerly.ui.dialogs.success.rememberSuccessState
import kotlinx.coroutines.launch

private const val TAG = "FeedbackScreen"

/**
 * Displays the Feedback Screen, allowing users to provide feedback
 * on a completed charging session.
 *
 */
@Composable
internal fun FeedbackDialog(
    viewModel: FeedbackViewModel = koinViewModel(),
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val title = remember { viewModel.title }
    val coroutineScope = rememberCoroutineScope()
    val loadingState = rememberLoadingState()
    val successState = rememberSuccessState()


    fun skipReview() {
        viewModel.skipReview()
        onDismiss()
    }

    fun submitReview(rating: Int, msg: String) {
        coroutineScope.launch {
            viewModel.submitReview(rating, msg).collect {
                loadingState.show = it is ApiStatus.Loading
                when (it) {
                    is ApiStatus.Success -> {
                        successState.show {
                            onDismiss()
                        }
                    }

                    is ApiStatus.Error -> {
                        Toast.makeText(context, it.msg.msg, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }


    MyBasicBottomSheet(onDismiss = onDismiss) {
        LoadingDialog(state = loadingState)
        SuccessDialog(state = successState)
        FeedbackScreenContent(
            title = title,
            reasonsFlow = viewModel.reviewOptions,
            onDone = ::submitReview,
            onSkip = ::skipReview
        )
    }
}
