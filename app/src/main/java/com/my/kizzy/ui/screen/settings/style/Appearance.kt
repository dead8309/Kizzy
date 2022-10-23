package com.my.kizzy.ui.screen.settings.style

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.my.kizzy.R
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.SettingItem
import com.my.kizzy.ui.common.SingleChoiceItem
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.THEME
import me.rerere.md3compat.ThemeChooser


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Appearance(
    onBackPressed: () -> Unit,
    navigateToLanguages: () -> Unit,
    onThemeModeChanged: (Boolean) -> Unit
) {
    var showModeDialog by remember {
        mutableStateOf(false)
    }
    val isDark = isSystemInDarkTheme()
    var darkTheme by remember {
        mutableStateOf(Prefs.get(THEME,isDark ))
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.theme),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = { BackButton { onBackPressed() } }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ThemeChooser()
            SettingItem(
                title = stringResource(id = R.string.dark_theme),
                description = "Light,Dark",
                icon = Icons.Outlined.DarkMode) {
                showModeDialog = true
            }
            SettingItem(
                title = stringResource(id = R.string.language),
                description = "English,Turkish,Dutch..",
                icon = Icons.Outlined.Translate
            ) {
                navigateToLanguages()
            }
        }
        if (showModeDialog)
            AlertDialog(onDismissRequest = {
                showModeDialog = false
            }, confirmButton = {
                TextButton(onClick = {
                    showModeDialog = false
                    onThemeModeChanged(darkTheme)
                }) {
                    Text(text = "Ok")
                }
            }, icon = { Icon(Icons.Outlined.DarkMode, null) },
                title = { Text(stringResource(R.string.dark_theme)) }, text = {
                    Column {
                        SingleChoiceItem(
                            text = "On",
                            selected = darkTheme
                        ) {
                            darkTheme = true
                        }
                        SingleChoiceItem(
                            text = "Off",
                            selected = !darkTheme
                        ) {
                           darkTheme = false
                        }
                    }
                })
    }

}