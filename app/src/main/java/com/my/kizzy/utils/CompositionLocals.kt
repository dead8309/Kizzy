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
package com.my.kizzy.utils

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import com.my.kizzy.preference.AppSettingsStateFlow
import com.my.kizzy.preference.DarkThemePreference
import com.my.kizzy.ui.theme.ColorScheme.DEFAULT_SEED_COLOR

val LocalDarkTheme = compositionLocalOf { DarkThemePreference() }
val LocalSeedColor = compositionLocalOf { DEFAULT_SEED_COLOR }
val settingFlow = AppSettingsStateFlow
val LocalDynamicColorSwitch = compositionLocalOf { false }
val LocalWindowWidthState = staticCompositionLocalOf { WindowWidthSizeClass.Compact }

@Composable
fun SettingsProvider(windowWidthSizeClass: WindowWidthSizeClass,content: @Composable () -> Unit) {
    val appSettingsState = settingFlow.collectAsState().value
    CompositionLocalProvider(
        LocalDarkTheme provides appSettingsState.darkTheme,
        LocalSeedColor provides appSettingsState.seedColor,
        LocalWindowWidthState provides windowWidthSizeClass,
        LocalDynamicColorSwitch provides appSettingsState.isDynamicColorEnabled,
        content = content
    )
}