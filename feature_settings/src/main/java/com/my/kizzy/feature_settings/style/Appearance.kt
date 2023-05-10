@file:Suppress("DEPRECATION")

package com.my.kizzy.feature_settings.style

/**
 * source: https://github.com/JunkFood02/Seal
 */

import android.graphics.Color.parseColor
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.android.material.color.DynamicColors
import com.my.kizzy.resources.R
import com.kyant.monet.Hct
import com.kyant.monet.LocalTonalPalettes
import com.kyant.monet.PaletteStyle
import com.kyant.monet.TonalPalettes
import com.kyant.monet.TonalPalettes.Companion.toTonalPalettes
import com.kyant.monet.a1
import com.kyant.monet.a2
import com.kyant.monet.a3
import com.kyant.monet.n2
import com.kyant.monet.toColor
import com.my.kizzy.feature_settings.style.svg.DynamicSVGImage
import com.my.kizzy.feature_settings.style.svg.PALETTE
import com.my.kizzy.preference.DEFAULT_SEED_COLOR
import com.my.kizzy.preference.Prefs
import com.my.kizzy.preference.Prefs.CUSTOM_THEME_COLOR
import com.my.kizzy.preference.modifyThemeSeedColor
import com.my.kizzy.preference.palettesMap
import com.my.kizzy.preference.switchDynamicColor
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.SettingItem
import com.my.kizzy.ui.components.dialog.TextFieldDialog
import com.my.kizzy.ui.components.preference.PreferenceSwitch
import com.my.kizzy.ui.theme.LocalDarkTheme
import com.my.kizzy.ui.theme.LocalDynamicColorSwitch
import com.my.kizzy.ui.theme.LocalPaletteStyleIndex
import com.my.kizzy.ui.theme.LocalSeedColor
import com.my.kizzy.ui.theme.autoDark

val colorList = listOf(
    Color(DEFAULT_SEED_COLOR),
    Color.Blue,
    Hct(60.0, 100.0, 70.0).toSrgb().toColor(),
    Hct(125.0, 50.0, 60.0).toSrgb().toColor(),
    Color.Cyan,
    Color.Red,
    Color.Yellow,
    Color.Magenta
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Appearance(
    onBackPressed: () -> Unit, navigateToDarkTheme: () -> Unit
) {

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

                val pagerState = rememberPagerState(initialPage = colorList.indexOf(Color(
                    LocalSeedColor.current))
                        .run { if (equals(-1)) 1 else this })
                HorizontalPager(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clearAndSetSemantics { }, state = pagerState,
                    count = colorList.size, contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Row {
                        ColorButtons(colorList[it],last = it == colorList.lastIndex)
                    }
                }
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    pageCount = colorList.size,
                    modifier = Modifier
                        .clearAndSetSemantics { }
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 12.dp),
                    activeColor = MaterialTheme.colorScheme.primary,
                    inactiveColor = MaterialTheme.colorScheme.outlineVariant,
                    indicatorHeight = 6.dp,
                    indicatorWidth = 6.dp
                )
            }
            if (DynamicColors.isDynamicColorAvailable()) {
                PreferenceSwitch(title = stringResource(id = R.string.dynamic_color),
                    description = stringResource(
                        id = R.string.dynamic_color_desc
                    ),
                    icon = Icons.Outlined.Palette,
                    isChecked = LocalDynamicColorSwitch.current,
                    onClick = {
                        switchDynamicColor()
                    })
            }

            SettingItem(
                title = stringResource(id = R.string.dark_theme),
                description = LocalDarkTheme.current.getDarkThemeDesc(),
                icon = Icons.Outlined.DarkMode,
            ) { navigateToDarkTheme() }
        }
    }
}

@Composable
fun RowScope.ColorButtons(color: Color, last: Boolean = false) {
    palettesMap.forEach {
        ColorButton(color = color, index = it.key, tonalStyle = it.value, custom = it.key == 3 && last)
    }
}

@Composable
fun RowScope.ColorButton(
    modifier: Modifier = Modifier,
    color: Color = Color.Green,
    index: Int = 0,
    tonalStyle: PaletteStyle = PaletteStyle.TonalSpot,
    custom: Boolean = false
) {
    var showDialog by remember { mutableStateOf(false) }
    var customColorValue by remember { mutableStateOf(Prefs[CUSTOM_THEME_COLOR, ""]) }
    val tonalPalettes =
        if(custom)
            customColorValue.color.toTonalPalettes(tonalStyle)
        else
            color.toTonalPalettes(tonalStyle)
    val isSelect =
        !LocalDynamicColorSwitch.current && LocalSeedColor.current == color.toArgb() && LocalPaletteStyleIndex.current == index

    TextFieldDialog(visible = showDialog,
        title = stringResource(R.string.primary_color),
        icon = Icons.Outlined.Palette,
        value = customColorValue,
        placeholder = stringResource(R.string.primary_color_hint),
        onValueChange = {
            customColorValue = it
        },
        onDismissRequest = {
            showDialog = false
        },
        onConfirm = { hexString ->
                customColorValue = hexString
                switchDynamicColor(enabled = false)
                Prefs[CUSTOM_THEME_COLOR] = hexString
                modifyThemeSeedColor(customColorValue.color.toArgb(), index)
                showDialog = false
        })
    ColorButtonImpl(
        modifier = modifier,
        tonalPalettes = tonalPalettes,
        cardColor = 95.autoDark(LocalDarkTheme.current.isDarkTheme()).n2,
        isSelected = { isSelect }
    ) {
        if (custom) {
            showDialog = true
        } else {
            switchDynamicColor(enabled = false)
            modifyThemeSeedColor(color.toArgb(), index)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.ColorButtonImpl(
    modifier: Modifier = Modifier,
    isSelected: () -> Boolean = { false },
    tonalPalettes: TonalPalettes,
    cardColor: Color,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    onClick: () -> Unit = {}
) {

    val containerSize by animateDpAsState(targetValue = if (isSelected.invoke()) 28.dp else 0.dp,
        label = "containerSizeAnimation"
    )
    val iconSize by animateDpAsState(targetValue = if (isSelected.invoke()) 16.dp else 0.dp,
        label = "iconSizeAnimation"
    )

    Surface(modifier = modifier
        .padding(4.dp)
        .sizeIn(maxHeight = 80.dp, maxWidth = 80.dp, minHeight = 64.dp, minWidth = 64.dp)
        .weight(1f, false)
        .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        color = cardColor,
        onClick = { onClick() }) {
        CompositionLocalProvider(LocalTonalPalettes provides tonalPalettes) {
            val color1 = 80.a1
            val color2 = 90.a2
            val color3 = 60.a3
            Box(Modifier.fillMaxSize()) {
                Box(
                    modifier = modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .drawBehind { drawCircle(color1) }
                        .align(Alignment.Center)
                ) {
                    Surface(
                        color = color2,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .size(24.dp)
                    ) {}
                    Surface(
                        color = color3,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                    ) {}
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .size(containerSize)
                            .drawBehind { drawCircle(containerColor) },
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = null,
                            modifier = Modifier
                                .size(iconSize)
                                .align(Alignment.Center),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                }
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
