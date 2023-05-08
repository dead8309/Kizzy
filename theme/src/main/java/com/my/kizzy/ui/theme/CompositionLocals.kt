/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * CompositionLocals.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

/**
 * source: https://github.com/JunkFood02/Seal
 */
package com.my.kizzy.ui.theme

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.kyant.monet.LocalTonalPalettes
import com.kyant.monet.PaletteStyle
import com.kyant.monet.TonalPalettes.Companion.toTonalPalettes
import com.my.kizzy.preference.AppSettingsStateFlow
import com.my.kizzy.preference.DEFAULT_SEED_COLOR
import com.my.kizzy.preference.DarkThemePreference
import com.my.kizzy.preference.palettesMap

val LocalDarkTheme = compositionLocalOf { DarkThemePreference() }
val LocalSeedColor = compositionLocalOf { DEFAULT_SEED_COLOR }
val LocalDynamicColorSwitch = compositionLocalOf { false }
val LocalWindowWidthState = staticCompositionLocalOf { WindowWidthSizeClass.Compact }
val LocalPaletteStyleIndex = compositionLocalOf { 0 }

@Composable
fun SettingsProvider(windowWidthSizeClass: WindowWidthSizeClass,content: @Composable () -> Unit) {
    val appSettingsState = AppSettingsStateFlow.collectAsState().value
    CompositionLocalProvider(
        LocalDarkTheme provides appSettingsState.darkTheme,
        LocalSeedColor provides appSettingsState.seedColor,
        LocalPaletteStyleIndex provides appSettingsState.paletteStyleIndex,
        LocalTonalPalettes provides Color(appSettingsState.seedColor).toTonalPalettes(
            palettesMap.getOrElse(appSettingsState.paletteStyleIndex) { PaletteStyle.TonalSpot }
        ),
        LocalWindowWidthState provides windowWidthSizeClass,
        LocalDynamicColorSwitch provides appSettingsState.isDynamicColorEnabled,
        content = content
    )
}