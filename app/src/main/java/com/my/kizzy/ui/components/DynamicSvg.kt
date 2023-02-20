/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * DynamicSvg.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.components

import android.graphics.drawable.PictureDrawable
import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Precision
import coil.size.Scale
import coil.size.Size
import com.caverock.androidsvg.SVG
import com.my.kizzy.utils.LocalDarkTheme
import com.my.kizzy.utils.LocalSeedColor
import com.my.kizzy.ui.svg.parseDynamicColor

@Composable
fun DynamicSVGImage(
    modifier: Modifier = Modifier,
    contentDescription: String,
    svgString: String
) {
    val useDarkTheme = LocalDarkTheme.current.isDarkTheme()
    val seed = LocalSeedColor.current
    var size by remember { mutableStateOf(IntSize.Zero) }
    val pic by remember(useDarkTheme, seed, size) {
        mutableStateOf(
            PictureDrawable(
                SVG.getFromString(svgString.parseDynamicColor(seed, useDarkTheme))
                    .renderToPicture(size.width, size.height)
            )
        )
    }

    Row(
        modifier = modifier
            .aspectRatio(1.38f)
            .onGloballyPositioned {
                if (it.size != IntSize.Zero) {
                    size = it.size
                }
            },
    ) {
        Crossfade(targetState = pic) {
            SvgImage(
                contentDescription = contentDescription, data = it, placeholder = null, error = null
            )
        }
    }
}

@Composable
fun SvgImage(
    modifier: Modifier = Modifier,
    data: Any? = null,
    size: Size = Size.ORIGINAL,
    scale: Scale = Scale.FIT,
    precision: Precision = Precision.AUTOMATIC,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String = "",
    @DrawableRes placeholder: Int? = com.my.kizzy.R.drawable.ic_hourglass_empty_black,
    @DrawableRes error: Int? = com.my.kizzy.R.drawable.broken_image,
) {
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current).data(data = data).apply(block = fun ImageRequest.Builder.() {
                if (placeholder != null) placeholder(placeholder)
                if (error != null) error(error)
                crossfade(true)
                scale(scale)
                precision(precision)
                size(size)
            }).build()
        ), contentDescription = contentDescription, contentScale = contentScale, modifier = modifier
    )
}