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

package com.my.kizzy.ui.svg

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import material.io.color.palettes.CorePalette

fun String.parseDynamicColor(color: Int,isDarkTheme: Boolean): String =
    replace("fill=\"(.+?)\"".toRegex()) {
        val corePalette: CorePalette = CorePalette.of(color)
        val value = it.groupValues[1]
        if (value.startsWith("#")) return@replace it.value
        try {
            val (scheme, tone) = value.split("(?<=\\d)(?=\\D)|(?=\\d)(?<=\\D)".toRegex())
            val argb: Int = when (scheme) {
                "p" -> corePalette.a1.tone(tone.toInt().autoToDarkTone(isDarkTheme))
                "s" -> corePalette.a2.tone(tone.toInt().autoToDarkTone(isDarkTheme))
                "t" -> corePalette.a3.tone(tone.toInt().autoToDarkTone(isDarkTheme))
                "n" -> corePalette.n1.tone(tone.toInt().autoToDarkTone(isDarkTheme))
                "nv" -> corePalette.n2.tone(tone.toInt().autoToDarkTone(isDarkTheme))
                "e" -> corePalette.error.tone(tone.toInt().autoToDarkTone(isDarkTheme))
                else -> Color.Transparent.toArgb()
            }
        "fill=\"${String.format("#%06X", 0xFFFFFF and argb)}\""
        } catch (e: Exception) {
            e.printStackTrace()
            it.value
        }
    }

internal fun Int.autoToDarkTone(isDarkTheme: Boolean): Int =
    if (!isDarkTheme) this
    else when (this) {
        10 -> 99
        20 -> 95
        25 -> 90
        30 -> 90
        40 -> 80
        50 -> 60
        60 -> 50
        70 -> 40
        80 -> 40
        90 -> 30
        95 -> 20
        98 -> 10
        99 -> 10
        100 -> 20
        else -> this
    }