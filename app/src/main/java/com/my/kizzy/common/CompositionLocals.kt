/**
 * source: https://github.com/JunkFood02/Seal
 */
package com.my.kizzy.common

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import com.my.kizzy.ui.theme.ColorScheme.DEFAULT_SEED_COLOR
import com.my.kizzy.utils.Prefs

val LocalDarkTheme = compositionLocalOf { Prefs.DarkThemePreference() }
val LocalSeedColor = compositionLocalOf { DEFAULT_SEED_COLOR }
val settingFlow = Prefs.AppSettingsStateFlow
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