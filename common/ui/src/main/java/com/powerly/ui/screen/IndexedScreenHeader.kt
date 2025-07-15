package com.powerly.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.powerly.resources.R
import com.powerly.ui.containers.LayoutDirectionAny
import com.powerly.ui.containers.LayoutDirectionLtr
import com.powerly.ui.components.MyTextDynamic
import com.powerly.ui.theme.MyColors

@Composable
fun IndexedScreenHeader(
    title: String,
    index: Int,
    pages: Int = 9,
    showDivider: Boolean = true,
    paddingHorizontal: Dp = 16.dp,
    paddingVertical: Dp = 16.dp,
    textAlign: TextAlign = TextAlign.Start,
    onClose: () -> Unit,
) {
    LayoutDirectionLtr {
        Column(
            Modifier
                .background(color = Color.White)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = paddingHorizontal,
                        vertical = paddingVertical
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = "",
                    modifier = Modifier.clickable { onClose() },
                    tint = MaterialTheme.colorScheme.secondary
                )
                LayoutDirectionAny {
                    MyTextDynamic(
                        text = title,
                        textAlign = textAlign,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(
                            color = MyColors.viewColor2,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)

                ) {
                    Text(
                        text = "$index/$pages",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            if (showDivider) HorizontalDivider(color = MyColors.dividerColor)
        }
    }
}
