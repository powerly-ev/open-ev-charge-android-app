package com.powerly.ui.dialogs

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.powerly.resources.R
import com.powerly.ui.containers.MyCardColum
import com.powerly.ui.containers.MyColumn
import com.powerly.ui.containers.MySurfaceRow
import com.powerly.ui.components.MyIcon
import com.powerly.ui.components.NetworkImage
import com.powerly.ui.extensions.thenIf
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDialog(
    state: MyDialogState? = null,
    modifier: Modifier = Modifier,
    spacing: Dp = 8.dp,
    dismissOnClickOutside: Boolean = true,
    dismissOnBackPress: Boolean = true,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    onDismiss: () -> Unit = {},
    header: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (state != null && state.visible.not()) return
    BasicAlertDialog(
        onDismissRequest = {
            onDismiss()
            state?.dismiss()
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = dismissOnClickOutside,
            dismissOnBackPress = dismissOnBackPress
        )
    ) {
        Box(Modifier.fillMaxWidth()) {
            MyCardColum(spacing = 16.dp) {
                header?.invoke()
                Column(
                    horizontalAlignment = horizontalAlignment,
                    verticalArrangement = Arrangement.spacedBy(spacing),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .then(modifier),
                    content = content
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBottomSheet(
    state: MyDialogState? = null,
    modifier: Modifier = Modifier,
    spacing: Dp = 8.dp,
    verticalScroll: Boolean = false,
    background: Color = Color.White,
    shape: Shape = BottomSheetDefaults.ExpandedShape, // or HiddenShape
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    onDismiss: () -> Unit = {},
    dismissOnBackPress: Boolean = true,
    header: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (state != null && state.visible.not()) return

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val properties = ModalBottomSheetProperties(
        shouldDismissOnBackPress = dismissOnBackPress
    )

    val hideDialog: () -> Unit = {
        coroutineScope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismiss()
                state?.dismiss()
            }
        }
    }

    ModalBottomSheet(
        containerColor = background,
        sheetState = sheetState,
        onDismissRequest = hideDialog,
        shape = shape,
        properties = properties,
        dragHandle = { header?.invoke() },
        content = {
            MyColumn(
                horizontalAlignment = horizontalAlignment,
                spacing = spacing,
                modifier = modifier
                    .background(background)
                    .padding(bottom = 32.dp)
                    .thenIf(verticalScroll, Modifier.verticalScroll(rememberScrollState())),
                content = content
            )
        },
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBasicBottomSheet(
    state: MyDialogState? = null,
    modifier: Modifier = Modifier,
    dismissOnBackPress: Boolean = true,
    shape: Shape = BottomSheetDefaults.ExpandedShape, // or HiddenShape
    background: Color = Color.White,
    onDismiss: () -> Unit = {},
    header: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (state != null && state.visible.not()) return

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val properties = ModalBottomSheetProperties(
        shouldDismissOnBackPress = dismissOnBackPress
    )

    val hideDialog: () -> Unit = {
        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismiss()
                state?.dismiss()
            }
        }
    }
    ModalBottomSheet(
        containerColor = background,
        sheetState = sheetState,
        modifier = modifier,
        shape = shape,
        onDismissRequest = hideDialog,
        properties = properties,
        dragHandle = { header?.invoke() },
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyScreenBottomSheet(
    state: MyDialogState? = null,
    modifier: Modifier = Modifier,
    shape: Shape = BottomSheetDefaults.HiddenShape,
    background: Color = Color.White,
    dismissOnBackPress: Boolean = true,
    draggable: Boolean = true,
    onDismiss: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    if (state != null && state.visible.not()) return

    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { draggable || (it != SheetValue.Hidden) }
    )

    val properties = ModalBottomSheetProperties(
        shouldDismissOnBackPress = dismissOnBackPress,
    )

    val hideDialog: () -> Unit = {
        coroutineScope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                onDismiss()
                state?.dismiss()
            }
        }
    }

    ModalBottomSheet(
        containerColor = background,
        sheetState = sheetState,
        modifier = modifier,
        shape = shape,
        onDismissRequest = hideDialog,
        properties = properties,
        dragHandle = {},
        content = content,
    )
}

@Composable
fun MyDropdownMenu(
    modifier: Modifier = Modifier,
    background: Color = Color.White,
    maxHeight: Float? = null,
    spacing: Dp = 8.dp,
    widthPercent: Float = 0.9f,
    usePlatformDefaultWidth: Boolean = false,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    onDismiss: () -> Unit = {},
    expanded: () -> Boolean = { false },
    content: @Composable ColumnScope.() -> Unit
) {
    DropdownMenu(
        expanded = expanded(),
        properties = PopupProperties(
            usePlatformDefaultWidth = usePlatformDefaultWidth
        ),
        modifier = Modifier
            .background(color = background)
            .fillMaxWidth(widthPercent)
            .thenIf(
                maxHeight != null,
                Modifier.fillMaxHeight(maxHeight ?: 1f),
                Modifier.wrapContentHeight()
            ),
        onDismissRequest = onDismiss,
    ) {
        Column(
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = Arrangement.spacedBy(spacing),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .then(modifier),
            content = content
        )
    }
}

@Composable
fun MyDropdownMenu(
    dialogState: MyDialogState,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier,
    background: Color = Color.White,
    maxHeight: Float? = null,
    spacing: Dp = 8.dp,
    widthPercent: Float = 0.9f,
    usePlatformDefaultWidth: Boolean = false,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable ColumnScope.() -> Unit
) {
    DropdownMenu(
        expanded = dialogState.visible,
        properties = PopupProperties(
            usePlatformDefaultWidth = usePlatformDefaultWidth
        ),
        modifier = Modifier
            .background(color = background)
            .fillMaxWidth(widthPercent)
            .thenIf(
                maxHeight != null,
                Modifier.fillMaxHeight(maxHeight ?: 1f),
                Modifier.wrapContentHeight()
            ),
        onDismissRequest = {
            onDismiss()
            dialogState.dismiss()
        },
    ) {
        Column(
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = Arrangement.spacedBy(spacing),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .then(modifier),
            content = content
        )
    }
}

@Composable
fun ItemOptionsMenu(
    title: String,
    padding: PaddingValues = PaddingValues(vertical = 4.dp, horizontal = 8.dp),
    textAlign: TextAlign = TextAlign.Start,
    background: Color = Color.White,
    onClick: () -> Unit
) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .background(background, shape = RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onClick)
            .padding(padding),
        textAlign = textAlign
    )
}

@Composable
fun ItemOptionsMenu(
    title: String,
    image: String? = null,
    @DrawableRes icon: Int? = null,
    padding: PaddingValues = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    background: Color = MaterialTheme.colorScheme.surface,
    selected: () -> Boolean,
    onSelect: () -> Unit
) {
    MySurfaceRow(
        modifier = modifier.padding(padding),
        onClick = onSelect,
        color = background,
        verticalAlignment = Alignment.CenterVertically
    ) {
        image?.let {
            NetworkImage(
                src = image,
                modifier = Modifier.size(30.dp)
            )
        }
        icon?.let {
            Image(
                painter = painterResource(icon),
                modifier = Modifier.size(30.dp),
                contentDescription = ""
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f),
            textAlign = textAlign
        )
        if (selected()) MyIcon(
            icon = R.drawable.ic_true,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ProgressView() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun MyProgressView(show: () -> Boolean = { true }) {
    if (show()) Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .zIndex(3f),
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
fun ProgressDialog(
    background: Color = Color.Transparent,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = background)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.padding(6.dp, 0.dp, 0.dp, 0.dp),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
