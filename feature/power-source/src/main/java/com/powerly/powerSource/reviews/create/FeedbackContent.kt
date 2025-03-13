package com.powerly.powerSource.reviews.create

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.core.model.api.ApiStatus
import com.powerly.core.data.model.ReviewOptionsStatus
import com.powerly.resources.R
import com.powerly.ui.components.ButtonLarge
import com.powerly.ui.containers.LayoutDirectionLtr
import com.powerly.ui.theme.MyColors
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.components.MyTextField
import com.powerly.ui.dialogs.ProgressView
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

private const val TAG = "FeedbackScreen"

@Preview
@Composable
private fun FeedbackScreenPreview() {
    val reasons = mapOf(
        "5" to listOf(
            "Not Polite",
            "Not Working",
            "couldn't Not find it easily", "No Electricity",
            "Incompatible Plug",
            "Other"
        ),
        "4" to listOf(
            "Not Polite",
            "Not Working",
            "couldn't Not find it easily", "No Electricity",
            "Incompatible Plug",
            "Other"
        )
    )

    val flow = MutableStateFlow<ReviewOptionsStatus>(
        ReviewOptionsStatus.Success(reasons)
    )
    Box(modifier = Modifier.background(Color.White)) {
        FeedbackScreenContent(
            title = "Title",
            reasonsFlow = flow,
            onDone = { _, _ -> },
            onSkip = {}
        )
    }
}

@Composable
internal fun FeedbackScreenContent(
    title: String,
    reasonsFlow: Flow<ReviewOptionsStatus>,
    onSkip: () -> Unit,
    onDone: (rating: Int, msg: String) -> Unit
) {
    val reasonsStatus = reasonsFlow.collectAsState(
        initial = ApiStatus.Loading
    )
    var feedback by remember { mutableStateOf<Emoji?>(null) }
    var showNote by remember { mutableStateOf(false) }
    var reason by remember { mutableStateOf("") }
    var reasonId by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
    ) {
        MyColumn(horizontalAlignment = Alignment.CenterHorizontally) {

            LayoutDirectionLtr {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.close),
                        contentDescription = "",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                onSkip()
                            }
                    )
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = stringResource(id = R.string.feedback_msg),
                style = MaterialTheme.typography.bodyLarge,
                color = MyColors.subColor
            )

            SectionEmojis(
                selected = { feedback },
                onSelect = {
                    feedback = it
                    reason = ""
                    reasonId = ""
                    showNote = false
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            when (val state = reasonsStatus.value) {
                is ReviewOptionsStatus.Loading -> ProgressView()
                is ReviewOptionsStatus.Error -> {}
                is ReviewOptionsStatus.Success -> {
                    SectionReasons(
                        reasons = {
                            val rating: Int = feedback?.id ?: return@SectionReasons null
                            state.reasons[rating.toString()]
                        },
                        selectedReason = { reasonId },
                        onReason = {
                            reason = it
                            reasonId = it
                            showNote = false
                        },
                        onOther = {
                            reason = ""
                            reasonId = it
                            showNote = true
                        }
                    )
                }
            }

            if (showNote) MyTextField(
                value = reason,
                placeholder = stringResource(R.string.add_a_note),
                onValueChange = { reason = it },
                modifier = Modifier.fillMaxWidth(),
                lines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))
            ButtonLarge(
                modifier = Modifier.fillMaxWidth(),
                enabled = { feedback != null && reason.isNotEmpty() },
                onClick = { onDone(feedback!!.id, reason) },
                text = stringResource(id = R.string.done),
                background = MaterialTheme.colorScheme.secondary,
                color = MyColors.white
            )
        }
    }
}


@Composable
private fun SectionEmojis(
    selected: () -> Emoji?,
    onSelect: (Emoji) -> Unit
) {
    val emoji = selected()
    MyColumn(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            emojis.forEach {
                val label = stringResource(id = it.label)
                Box(modifier = Modifier
                    .size(50.dp)
                    .background(
                        shape = CircleShape,
                        color = if (emoji?.id == it.id) MyColors.blueLight
                        else Color.Transparent
                    )

                    .clickable { onSelect(it) }) {
                    Icon(
                        painter = painterResource(id = it.icon),
                        contentDescription = label,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
        emoji?.label?.let {
            Text(
                text = stringResource(id = it),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SectionReasons(
    reasons: () -> List<String>?,
    selectedReason: () -> String,
    onReason: (String) -> Unit,
    onOther: (String) -> Unit
) {
    val reasonList = reasons() ?: return

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .defaultMinSize(minHeight = 150.dp)
    ) {
        reasonList.forEachIndexed { index, reason ->
            ChipItem(
                reason = reason,
                isSelected = reason == selectedReason(),
                onClick = {
                    if (index == reasonList.lastIndex) onOther(reason)
                    else onReason(reason)
                }
            )
        }
    }
}

@Composable
private fun ChipItem(
    isSelected: Boolean,
    reason: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MyColors.borderColor
        ),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = reason,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(
                vertical = 12.dp,
                horizontal = 16.dp
            )
        )
    }
}
