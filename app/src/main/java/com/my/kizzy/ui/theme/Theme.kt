package com.my.kizzy.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.color.DynamicColors
import com.my.kizzy.ui.theme.ColorScheme.colorSchemeFromColor


private tailrec fun Context.findWindow(): Window? =
    when (this) {
        is Activity -> window
        is ContextWrapper -> baseContext.findWindow()
        else -> null
    }

@Composable
fun KizzyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isHighContrastModeEnabled: Boolean = false,
    seedColor: Int,
    isDynamicColorEnabled: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        DynamicColors.isDynamicColorAvailable() && isDynamicColorEnabled -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
        }
        else -> colorSchemeFromColor(seedColor, darkTheme)
    }.run {
        if (isHighContrastModeEnabled && darkTheme) copy(
            surface = Color.Black,
            background = Color.Black,
        )
        else this
    }
    val window = LocalView.current.context.findWindow()
    val view = LocalView.current

    window?.let {
        WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = darkTheme
    }

    rememberSystemUiController(window).setSystemBarsColor(Color.Transparent, !darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}