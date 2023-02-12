package com.my.kizzy.ui.screen.settings.style

/**
 * source: https://github.com/JunkFood02/Seal
 */

import android.graphics.Color.parseColor
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.google.android.material.color.DynamicColors
import com.my.kizzy.R
import com.my.kizzy.common.LocalDarkTheme
import com.my.kizzy.common.LocalDynamicColorSwitch
import com.my.kizzy.common.LocalSeedColor
import com.my.kizzy.ui.common.*
import com.my.kizzy.ui.svg.PALETTE
import com.my.kizzy.ui.theme.ColorScheme.DEFAULT_SEED_COLOR
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.CUSTOM_THEME_COLOR
import material.io.color.hct.Hct
import material.io.color.palettes.CorePalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Appearance(
    onBackPressed: () -> Unit, navigateToLanguages: () -> Unit, navigateToDarkTheme: () -> Unit
) {
    var showCustomColorDialog by remember { mutableStateOf(false) }
    var savedHex by remember { mutableStateOf(Prefs[CUSTOM_THEME_COLOR, ""]) }

    Scaffold(topBar = {
        LargeTopAppBar(title = {
            Text(
                text = stringResource(id = R.string.display),
                style = MaterialTheme.typography.headlineLarge
            )
        }, navigationIcon = {
            BackButton {
                onBackPressed()
            }
        })
    }) {
        Column(
            Modifier.padding(it),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .aspectRatio(1.38f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            MaterialTheme.colorScheme.inverseOnSurface
                        )
                        .clickable { },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DynamicSVGImage(
                        modifier = Modifier.padding(60.dp),
                        contentDescription = "palette",
                        svgString = PALETTE
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {
                    ColorButton(color = Color(DEFAULT_SEED_COLOR))
                    ColorButton(
                        color = Color(
                            Hct.from(60.0, 100.0, 70.0).toInt()
                        )
                    )
                    ColorButton(
                        color = Color(
                            Hct.from(125.0, 50.0, 60.0).toInt()
                        )
                    )
                    ColorButton(color = Color.Cyan)
                    ColorButton(color = Color.Red)
                    //Custom
                    ColorButton(customColorEnabled = true,
                        customColor = savedHex,
                        showDialog = showCustomColorDialog,
                        onClick = {
                            showCustomColorDialog = true
                        },
                        onDismiss = {
                            showCustomColorDialog = false
                        },
                        color = savedHex.color,
                        onConfirm = { hexString ->
                            savedHex = hexString
                            Prefs.switchDynamicColor(enabled = false)
                            Prefs[CUSTOM_THEME_COLOR] = hexString
                            Prefs.modifyThemeSeedColor(savedHex.color.toArgb())
                            showCustomColorDialog = false
                        })
                }
            }
            if (DynamicColors.isDynamicColorAvailable()) {
                PreferenceSwitch(title = stringResource(id = R.string.dynamic_color),
                    description = stringResource(
                        id = R.string.dynamic_color_desc
                    ),
                    icon = Icons.Outlined.Palette,
                    isChecked = LocalDynamicColorSwitch.current,
                    onClick = {
                        Prefs.switchDynamicColor()
                    })
            }

            SettingItem(
                title = stringResource(id = R.string.dark_theme),
                description = LocalDarkTheme.current.getDarkThemeDesc(),
                icon = Icons.Outlined.DarkMode,
            ) { navigateToDarkTheme() }

            SettingItem(
                title = stringResource(R.string.language),
                icon = Icons.Outlined.Language,
                description = "English,Turkish,Dutch.."
            ) { navigateToLanguages() }
        }
    }
}

const val TAG = "Appearance"
@Composable
fun ColorButton(
    modifier: Modifier = Modifier,
    color: Color = Color.Green,
    onClick: () -> Unit = {},
    showDialog: Boolean = false,
    customColorEnabled: Boolean = false,
    customColor: String = "",
    onDismiss: () -> Unit = {},
    onConfirm: (String) -> Unit = {}
) {
    val corePalette = CorePalette.of(color.toArgb())
    val seedColor = corePalette.a2.tone(60)
    Log.d(
        TAG, "ColorButton() called with: color = ${color.toArgb()}, seed color = $seedColor"
    )

    var customColorValue by remember { mutableStateOf(customColor) }

    TextFieldDialog(visible = showDialog,
        title = stringResource(R.string.primary_color),
        icon = Icons.Outlined.Palette,
        value = customColorValue,
        placeholder = stringResource(R.string.primary_color_hint),
        onValueChange = {
            customColorValue = it
        },
        onDismissRequest = {
            onDismiss()
        },
        onConfirm = {
            onConfirm(it)
        })

    ColorButtonImpl(
        modifier = modifier,
        isSelected = !LocalDynamicColorSwitch.current && LocalSeedColor.current == seedColor,
        color = color,
        corePalette = corePalette,
        isDarkTheme = LocalDarkTheme.current.isDarkTheme()
    ) {
        if (customColorEnabled) onClick()
        else {
            Prefs.switchDynamicColor(enabled = false)
            Prefs.modifyThemeSeedColor(seedColor)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorButtonImpl(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    color: Color = Color.Green,
    corePalette: CorePalette = CorePalette.of(color.toArgb()),
    isDarkTheme: Boolean = false,
    onClick: () -> Unit = {}
) {
    val lightColor = corePalette.a2.tone(80)
    val darkColor = corePalette.a2.tone(60)
    val showColor = if (isDarkTheme) darkColor else lightColor
    val state = animateDpAsState(targetValue = if (isSelected) 48.dp else 36.dp)
    val state2 = animateDpAsState(targetValue = if (isSelected) 18.dp else 0.dp)

    ElevatedCard(modifier = modifier
        .clearAndSetSemantics { }
        .padding(4.dp)
        .size(72.dp), onClick = { onClick() }) {
        Box(Modifier.fillMaxSize()) {
            Box(
                modifier = modifier
                    .size(state.value)
                    .clip(CircleShape)
                    .background(Color(showColor))
                    .align(Alignment.Center)
            ) {
                Icon(
                    Icons.Outlined.Check,
                    null,
                    modifier = Modifier
                        .size(state2.value)
                        .align(Alignment.Center)
                        .clip(CircleShape),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

val String.color
    get() = try {
        Color(parseColor(this))
    } catch (ex: Exception) {
        Color.Transparent
    }
