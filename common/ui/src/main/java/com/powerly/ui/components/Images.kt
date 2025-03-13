package com.powerly.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.powerly.resources.R
import com.powerly.ui.extensions.thenIf

@Composable
fun MyIconButton(
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = modifier,
    enabled: () -> Boolean = { true },
    tint: Color = MaterialTheme.colorScheme.secondary,
    description: String = "",
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        enabled = enabled(),
        modifier = modifier
    ) {
        MyIcon(
            icon = icon,
            tint = tint,
            description = description,
            modifier = iconModifier
        )
    }

}

@Composable
fun MyIcon(
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.secondary,
    description: String = ""
) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = description,
        tint = tint,
        modifier = modifier
    )
}

@Composable
fun NetworkImage(
    src: Any,
    modifier: Modifier = Modifier,
    @DrawableRes placeHolder: Int = R.drawable.logo,
    @DrawableRes error: Int = placeHolder,
    description: String = "",
    scale: ContentScale = ContentScale.Fit,
    onClick: (() -> Unit)? = null
) {
    Image(
        modifier = modifier.thenIf(
            onClick != null,
            Modifier.clickable { onClick?.invoke() }
        ),
        contentScale = scale,
        painter = rememberAsyncImagePainter(
            model = src,
            placeholder = painterResource(placeHolder),
            error = painterResource(error)
        ),
        contentDescription = description,
    )
}

@Composable
fun NetworkImage(
    src: Any,
    modifier: Modifier = Modifier,
    @DrawableRes placeHolder: Int = R.drawable.logo,
    @DrawableRes error: Int = placeHolder,
    description: String = "",
    scale: ContentScale = ContentScale.Fit,
    allowHardware: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val request = ImageRequest
        .Builder(LocalContext.current)
        .data(src)
        .placeholder(placeHolder)
        .error(error)
        .allowHardware(allowHardware)
        .build()

    AsyncImage(
        model = request,
        modifier = modifier.thenIf(
            onClick != null,
            Modifier.clickable { onClick?.invoke() }
        ),
        contentScale = scale,
        contentDescription = description,
    )
}