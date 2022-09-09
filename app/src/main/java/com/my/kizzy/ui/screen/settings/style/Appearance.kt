package com.my.kizzy.ui.screen.settings.style

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.screen.settings.SettingItem
import me.rerere.md3compat.ThemeChooser


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Appearance(onBackPressed: () -> Unit,
navigateToLanguages: () -> Unit) {
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Theme",
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
                title = "Language",
                description = "English,Arabic,Chinese..",
                icon = Icons.Outlined.Translate
            ) {
                navigateToLanguages()
            }
        }
    }

}