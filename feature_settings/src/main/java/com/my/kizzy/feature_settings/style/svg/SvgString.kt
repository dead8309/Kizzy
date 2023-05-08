/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * SvgString.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_settings.style.svg

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.kyant.monet.TonalPalettes

fun String.parseDynamicColor(
    tonalPalettes: TonalPalettes,
    isDarkTheme: Boolean): String =
    replace("fill=\"(.+?)\"".toRegex()) {
        val value = it.groupValues[1]
        if (value.startsWith("#")) return@replace it.value
        try {
            val (scheme, tone) = value.split("(?<=\\d)(?=\\D)|(?=\\d)(?<=\\D)".toRegex())
            val argb = when (scheme) {
                "p" -> tonalPalettes accent1 tone.autoToDarkTone(isDarkTheme)
                "s" -> tonalPalettes accent2 tone.autoToDarkTone(isDarkTheme)
                "t" -> tonalPalettes accent3 tone.autoToDarkTone(isDarkTheme)
                "n" -> tonalPalettes neutral1 tone.autoToDarkTone(isDarkTheme)
                "nv" -> tonalPalettes neutral2 tone.autoToDarkTone(isDarkTheme)
                else -> Color.Transparent
            }.toArgb()
        "fill=\"${String.format("#%06X", 0xFFFFFF and argb)}\""
        } catch (e: Exception) {
            e.printStackTrace()
            it.value
        }
    }

internal fun String.autoToDarkTone(isDarkTheme: Boolean): Double =
    if (!isDarkTheme) this.toDouble()
    else when (this.toDouble()) {
        10.0 -> 99.0
        20.0 -> 95.0
        25.0 -> 90.0
        30.0 -> 90.0
        40.0 -> 80.0
        50.0 -> 60.0
        60.0 -> 50.0
        70.0 -> 40.0
        80.0 -> 40.0
        90.0 -> 30.0
        95.0 -> 20.0
        98.0 -> 10.0
        99.0 -> 10.0
        100.0 -> 20.0
        else -> this.toDouble()
    }