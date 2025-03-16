package com.powerly.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.powerly.ui.theme.MyColors

/***
 *  [ cell1 ][ cell2 ]
 *  [ cell3 ][ cell4 ]
 */
@Composable
fun FourCellsContainer(
    containerHeight: Dp = 200.dp,
    cell1: @Composable RowScope.() -> Unit,
    cell2: @Composable RowScope.() -> Unit,
    cell3: @Composable RowScope.() -> Unit,
    cell4: @Composable RowScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MyColors.white
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(containerHeight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                ItemRow(cell1, cell2)
                HorizontalDivider(
                    color = MyColors.dividerColor,
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp
                )
                ItemRow(cell3, cell4)
            }
        }
    }
}


@Composable
private fun ColumnScope.ItemRow(
    cell1: @Composable RowScope.() -> Unit,
    cell2: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .weight(0.5f)
    ) {
        Row(
            modifier = Modifier
                .weight(0.5f)
                .align(Alignment.CenterVertically),
            horizontalArrangement = Arrangement.Center,
            content = cell1,
        )
        VerticalDivider(
            color = MyColors.dividerColor,
            modifier = Modifier.fillMaxHeight(),
            thickness = 1.dp
        )
        Row(
            modifier = Modifier
                .weight(0.5f)
                .align(Alignment.CenterVertically),
            horizontalArrangement = Arrangement.Center,
            content = cell2,
        )
    }
}



