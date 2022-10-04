package com.my.kizzy.ui.screen.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SettingsSuggest
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.my.kizzy.R
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.PreferencesHint
import com.my.kizzy.ui.common.SettingItem

@SuppressLint("BatteryLife")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    navigateToProfile: () -> Unit,
    navigateToStyleAndAppeareance: () -> Unit,
    navigateToAbout: () -> Unit,
    navigateToRpcSettings: () -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    var showBatteryHint by remember { mutableStateOf(!pm.isIgnoringBatteryOptimizations(context.packageName)) }
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
        ) {
            SmallTopAppBar(
                title = {},
                navigationIcon = { BackButton { onBackPressed() } },
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(
                modifier = Modifier.padding(start = 24.dp, top = 48.dp),
                text = stringResource(id = R.string.settings),
                style = MaterialTheme.typography.headlineLarge
            )
            LazyColumn(modifier = Modifier.padding(top = 48.dp)) {
                item {
                    AnimatedVisibility(visible = showBatteryHint) {
                        PreferencesHint(
                            title = stringResource(id = R.string.battery_optimisation),
                            icon = Icons.Default.EnergySavingsLeaf,
                            description = stringResource(id = R.string.battery_optimisation_desc)
                        ) {
                            context.startActivity(Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                data = Uri.parse("package:${context.packageName}")
                            })
                            showBatteryHint =
                                !pm.isIgnoringBatteryOptimizations(context.packageName)
                        }
                    }
                }
                item {
                    SettingItem(
                        title = stringResource(id = R.string.account),
                        description = stringResource(id = R.string.account_info),
                        icon = Icons.Outlined.Person
                    ) {
                        navigateToProfile()
                    }
                }
                item {
                    SettingItem(
                        title = stringResource(id = R.string.display),
                        description = stringResource(id = R.string.display_desc),
                        icon = Icons.Outlined.Palette
                    ) {
                       navigateToStyleAndAppeareance()
                    }
                }
                item {
                    SettingItem(
                        title = stringResource(id = R.string.rpc_settings),
                        description = stringResource(id = R.string.rpc_settings_desc),
                        icon = Icons.Outlined.SettingsSuggest
                    ) {
                        navigateToRpcSettings()
                    }
                }
                item {
                    SettingItem(
                        title = stringResource(id = R.string.about),
                        description = stringResource(id = R.string.about_desc),
                        icon = Icons.Outlined.Info
                    ) {
                        navigateToAbout()
                    }
                }

            }
        }
    }
}

