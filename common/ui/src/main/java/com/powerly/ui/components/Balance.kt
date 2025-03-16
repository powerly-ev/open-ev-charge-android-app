package com.powerly.ui.components

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.powerly.resources.R
import com.powerly.ui.containers.LayoutDirectionLtr
import com.powerly.ui.containers.MyRow
import com.powerly.ui.theme.AppTheme

@Preview
@Composable
private fun SectionBalancePreview() {
    AppTheme {
        SectionBalance(
            balance = "200",
            currency = "SAR",
            onClick = {},
            modifier = Modifier.width(IntrinsicSize.Min)
        )
    }
}

@Composable
fun SectionBalance(
    balance: String,
    currency: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.surface
) {
    Surface(
        color = background,
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
    ) {
        LayoutDirectionLtr {
            MyRow(
                modifier = Modifier.padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                ),
                spacing = 4.dp,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MyIcon(
                    icon = R.drawable.ic_sd_wallet,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    color = MaterialTheme.colorScheme.primary,
                    text = balance,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = currency,
                    modifier = Modifier.padding(top = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.tertiary,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}