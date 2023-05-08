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

package com.my.kizzy.feature_settings.style.svg

import android.graphics.drawable.PictureDrawable
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.kyant.monet.LocalTonalPalettes
import com.kyant.monet.TonalPalettes
import com.my.kizzy.ui.theme.LocalDarkTheme

@Composable
fun DynamicSVGImage(
    modifier: Modifier = Modifier,
    contentDescription: String,
    svgString: String,
    tonalPalettes: TonalPalettes = LocalTonalPalettes.current,
    isDarkTheme: Boolean = LocalDarkTheme.current.isDarkTheme()

) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val pic by remember(size, isDarkTheme, tonalPalettes) {
        mutableStateOf(
            PictureDrawable(
                SVG.getFromString(
                    svgString.parseDynamicColor(
                        tonalPalettes = tonalPalettes,
                        isDarkTheme = isDarkTheme
                    )
                ).renderToPicture(size.width, size.height)
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
        Crossfade(targetState = pic, label = "svg") {
            SvgImage(
                contentDescription = contentDescription,
                data = it
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
    contentDescription: String = ""
) {
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current).data(data = data).apply(block = fun ImageRequest.Builder.() {
                crossfade(true)
                scale(scale)
                precision(precision)
                size(size)
            }).build()
        ), contentDescription = contentDescription, contentScale = contentScale, modifier = modifier
    )
}