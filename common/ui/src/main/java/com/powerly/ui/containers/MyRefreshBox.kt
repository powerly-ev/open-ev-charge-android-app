package com.powerly.ui.containers

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRefreshBox(
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        modifier = modifier,
        onRefresh = {
            onRefresh()
            coroutineScope.launch {
                isRefreshing = true
                delay(500)
                isRefreshing = false
            }
        }
    ) { content() }
}
