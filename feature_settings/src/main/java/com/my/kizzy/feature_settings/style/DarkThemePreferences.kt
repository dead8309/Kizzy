/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * DarkThemePreferences.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_settings.style

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.my.kizzy.resources.R
import com.my.kizzy.preference.DarkThemePreference.Companion.FOLLOW_SYSTEM
import com.my.kizzy.preference.DarkThemePreference.Companion.OFF
import com.my.kizzy.preference.DarkThemePreference.Companion.ON
import com.my.kizzy.preference.modifyDarkThemePreference
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.Subtitle
import com.my.kizzy.ui.components.preference.PreferenceSingleChoiceItem
import com.my.kizzy.ui.components.preference.PreferenceSwitch
import com.my.kizzy.ui.theme.LocalDarkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkThemePreferences(onBackPressed: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )
    val darkThemePreference = LocalDarkTheme.current
    val isHighContrastModeEnabled = darkThemePreference.isHighContrastModeEnabled
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = stringResource(id = R.string.dark_theme),
                    )
                }, navigationIcon = {
                    BackButton(modifier = Modifier.padding(start = 8.dp)) {
                        onBackPressed()
                    }
                }, scrollBehavior = scrollBehavior
            )
        }, content = {
            LazyColumn(modifier = Modifier.padding(it)) {
                item {
                    PreferenceSingleChoiceItem(
                        text = stringResource(R.string.follow_system),
                        selected = darkThemePreference.darkThemeValue == FOLLOW_SYSTEM
                    ) { modifyDarkThemePreference(FOLLOW_SYSTEM) }
                }
                item {
                    PreferenceSingleChoiceItem(
                        text = stringResource(id = R.string.android_on),
                        selected = darkThemePreference.darkThemeValue == ON
                    ) { modifyDarkThemePreference(ON) }
                }
                item {
                    PreferenceSingleChoiceItem(
                        text = stringResource(id = R.string.android_off),
                        selected = darkThemePreference.darkThemeValue == OFF
                    ) { modifyDarkThemePreference(OFF) }
                }
                item {
                    Subtitle(text = stringResource(R.string.advance_settings))
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(R.string.amoled),
                        icon = Icons.Outlined.Contrast,
                        isChecked = isHighContrastModeEnabled, onClick = {
                            modifyDarkThemePreference(isHighContrastModeEnabled = !isHighContrastModeEnabled)
                        }
                    )
                }
            }
        })
}