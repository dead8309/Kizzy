/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ThemePrefs.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.preference

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kyant.monet.PaletteStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.my.kizzy.resources.R

// TODO get seed color from colorscheme.kt
const val DEFAULT_SEED_COLOR =  0xFFAF92F1.toInt()
data class AppSettings(
    val darkTheme: DarkThemePreference = DarkThemePreference(),
    val isDynamicColorEnabled: Boolean = false,
    val seedColor: Int = DEFAULT_SEED_COLOR,
    val paletteStyleIndex: Int = 0
)
val palettesMap = mapOf(
    0 to PaletteStyle.TonalSpot,
    1 to PaletteStyle.Spritz,
    2 to PaletteStyle.FruitSalad,
    3 to PaletteStyle.Vibrant,
)

private val mutableAppSettingsStateFlow = MutableStateFlow(
    AppSettings(
        DarkThemePreference(
            darkThemeValue = Prefs[Prefs.DARK_THEME, DarkThemePreference.FOLLOW_SYSTEM],
            isHighContrastModeEnabled = Prefs[Prefs.HIGH_CONTRAST, false]
        ),
        isDynamicColorEnabled = Prefs[Prefs.DYNAMIC_COLOR, false],
        seedColor = Prefs[Prefs.THEME_COLOR, DEFAULT_SEED_COLOR],
        paletteStyleIndex = Prefs[Prefs.PALETTE_STYLE, 0]
    )
)
val AppSettingsStateFlow = mutableAppSettingsStateFlow.asStateFlow()
fun modifyDarkThemePreference(
    darkThemeValue: Int = AppSettingsStateFlow.value.darkTheme.darkThemeValue,
    isHighContrastModeEnabled: Boolean = AppSettingsStateFlow.value.darkTheme.isHighContrastModeEnabled
) {
    PreferenceConfig.applicationScope.launch(Dispatchers.IO) {
        mutableAppSettingsStateFlow.update {
            it.copy(
                darkTheme = AppSettingsStateFlow.value.darkTheme.copy(
                    darkThemeValue = darkThemeValue,
                    isHighContrastModeEnabled = isHighContrastModeEnabled
                )
            )
        }
        Prefs[Prefs.DARK_THEME] = darkThemeValue
        Prefs[Prefs.HIGH_CONTRAST] = isHighContrastModeEnabled
    }
}

fun modifyThemeSeedColor(colorArgb: Int, paletteStyleIndex: Int) {
    PreferenceConfig.applicationScope.launch(Dispatchers.IO) {
        mutableAppSettingsStateFlow.update {
            it.copy(seedColor = colorArgb,
            paletteStyleIndex = paletteStyleIndex)
        }
        Prefs[Prefs.THEME_COLOR] = colorArgb
        Prefs[Prefs.PALETTE_STYLE] = paletteStyleIndex
    }
}

fun switchDynamicColor(enabled: Boolean = !mutableAppSettingsStateFlow.value.isDynamicColorEnabled) {
    PreferenceConfig.applicationScope.launch(Dispatchers.IO) {
        mutableAppSettingsStateFlow.update {
            it.copy(isDynamicColorEnabled = enabled)
        }
        Prefs[Prefs.DYNAMIC_COLOR] = enabled
    }
}

data class DarkThemePreference(
    val darkThemeValue: Int = FOLLOW_SYSTEM,
    val isHighContrastModeEnabled: Boolean = false
) {
    companion object {
        const val FOLLOW_SYSTEM = 1
        const val ON = 2
        const val OFF = 3
    }

    @Composable
    fun isDarkTheme(): Boolean {
        return if (darkThemeValue == FOLLOW_SYSTEM)
            isSystemInDarkTheme()
        else darkThemeValue == ON
    }

    @Composable
    fun getDarkThemeDesc(): String {
        return when (darkThemeValue) {
            FOLLOW_SYSTEM -> stringResource(R.string.follow_system)
            ON -> stringResource(id = R.string.android_on)
            else -> stringResource(id = R.string.android_off)
        }
    }
}