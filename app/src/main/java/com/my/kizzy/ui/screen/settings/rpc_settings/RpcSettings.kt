/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * RpcSettings.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.screen.settings.rpc_settings

import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.CodeOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.google.gson.Gson
import com.my.kizzy.BuildConfig
import com.my.kizzy.R
import com.my.kizzy.common.Constants
import com.my.kizzy.ui.common.*
import com.my.kizzy.ui.screen.custom.RpcField
import com.my.kizzy.utils.Log
import com.my.kizzy.utils.Prefs

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun RpcSettings(onBackPressed: () -> Boolean) {
    val context = LocalContext.current
    var isLowResIconsEnabled by remember { mutableStateOf(Prefs[Prefs.RPC_USE_LOW_RES_ICON, false]) }
    var configsDir by remember { mutableStateOf(Prefs[Prefs.CONFIGS_DIRECTORY, "Directory to store Custom Rpc configs"]) }
    var showDirConfigDialog by remember { mutableStateOf(false) }
    var useButtonConfigs by remember { mutableStateOf(Prefs[Prefs.USE_RPC_BUTTONS, false]) }
    var showButtonsConfigDialog by remember { mutableStateOf(false) }
    var rpcButtons by remember {
        mutableStateOf(Gson().fromJson(Prefs[Prefs.RPC_BUTTONS_DATA, "{}"], RpcButtons::class.java))
    }
    var customActivityType by remember {
        mutableStateOf(Prefs[Prefs.CUSTOM_ACTIVITY_TYPE, 0].toString())
    }
    var showActivityTypeDialog by remember {
        mutableStateOf(false)
    }

    var vlogEnabled by remember {
        mutableStateOf(Log.vlog.isEnabled())
    }
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        LargeTopAppBar(title = {
            Text(
                text = stringResource(id = R.string.rpc_settings),
                style = MaterialTheme.typography.headlineLarge,
            )
        }, navigationIcon = { BackButton { onBackPressed() } })
    }) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            item {
                PreferenceSubtitle(text = "General")
            }
            item {
                SettingItem(
                    title = "Configs Directory",
                    description = configsDir,
                    icon = Icons.Default.Storage,
                ) {
                    showDirConfigDialog = true
                }
            }
            item {
                PreferenceSwitch(
                    title = stringResource(id = R.string.use_custom_buttons),
                    description = stringResource(id = R.string.use_custom_buttons_desc),
                    icon = Icons.Default.Tune,
                    isChecked = useButtonConfigs,
                ) {
                    useButtonConfigs = !useButtonConfigs
                    Prefs[Prefs.USE_RPC_BUTTONS] = useButtonConfigs
                }
            }
            item {
                AnimatedVisibility(visible = useButtonConfigs) {
                    SettingItem(
                        title = "Rpc Buttons",
                        description = stringResource(id = R.string.rpc_settings_button_configs),
                        icon = Icons.Default.SmartButton
                    ) {
                        showButtonsConfigDialog = true
                    }
                }
            }
            item {
                SettingItem(
                    title = "Custom Activity Type",
                    description = "Overrides the default activity type. Works for media and experimental rpc only",
                    icon = Icons.Default.Code
                ) {
                    showActivityTypeDialog = true
                }
            }
            item {
                PreferenceSubtitle(text = stringResource(id = R.string.advance_settings))
            }
            item {
                PreferenceSwitch(
                    title = stringResource(id = R.string.use_low_res_icon),
                    description = stringResource(id = R.string.use_low_res_icon_desc),
                    icon = Icons.Default.HighQuality,
                    isChecked = isLowResIconsEnabled,
                ) {
                    isLowResIconsEnabled = !isLowResIconsEnabled
                    Prefs[Prefs.RPC_USE_LOW_RES_ICON] = isLowResIconsEnabled
                }
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.delete_saved_icon_urls),
                    description = stringResource(id = R.string.delete_saved_icon_urls_desc),
                    icon = Icons.Default.DeleteForever
                ) {
                    Prefs.remove(Prefs.SAVED_IMAGES)
                    Prefs.remove(Prefs.SAVED_ARTWORK)
                    Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show()
                }
            }
            if (BuildConfig.DEBUG) {
                item {
                    PreferenceSwitch(
                        title = "Show Logs", icon = if (vlogEnabled) Icons.Outlined.Code
                        else Icons.Outlined.CodeOff, isChecked = vlogEnabled
                    ) {
                        vlogEnabled = if (!vlogEnabled) {
                            Log.vlog.start()
                            !vlogEnabled
                        } else {
                            Log.vlog.stop()
                            !vlogEnabled
                        }

                    }
                }
            }
        }
        if (showDirConfigDialog && Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            AlertDialog(
                onDismissRequest = { showDirConfigDialog = false },
                confirmButton = {},
                icon = {
                    Icon(
                        imageVector = Icons.Default.Storage, contentDescription = null
                    )
                },
                title = { Text("Select Directory") },
                text = {
                    Column {
                        SingleChoiceItem(
                            text = Constants.APP_DIRECTORY,
                            selected = configsDir == Constants.APP_DIRECTORY
                        ) {
                            configsDir = Constants.APP_DIRECTORY
                            Prefs[Prefs.CONFIGS_DIRECTORY] = configsDir
                            showDirConfigDialog = false
                        }
                        SingleChoiceItem(
                            text = Constants.DOWNLOADS_DIRECTORY,
                            selected = configsDir == Constants.DOWNLOADS_DIRECTORY
                        ) {
                            configsDir = Constants.DOWNLOADS_DIRECTORY
                            Prefs[Prefs.CONFIGS_DIRECTORY] = configsDir
                            showDirConfigDialog = false
                        }
                    }
                })
        }
        if (showButtonsConfigDialog) {
            AlertDialog(
                properties = DialogProperties(usePlatformDefaultWidth = false),
                modifier = Modifier.padding(20.dp),
                onDismissRequest = { showButtonsConfigDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        Prefs[Prefs.RPC_BUTTONS_DATA] = Gson().toJson(rpcButtons)
                        Log.vlog.d("Gson", rpcButtons.toString())
                        showButtonsConfigDialog = false
                    }
                    ) {
                        Text("Save")
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Storage, contentDescription = null
                    )
                },
                title = { Text("Enter Details") },
                text = {
                    Column {
                        RpcField(
                            value = rpcButtons.button1,
                            label = R.string.activity_button1_text
                        ) {
                            rpcButtons = rpcButtons.copy(button1 = it)
                        }
                        AnimatedVisibility(visible = rpcButtons.button1.isNotEmpty()) {
                            RpcField(
                                value = rpcButtons.button1Url,
                                label = R.string.activity_button1_url
                            ) {
                                rpcButtons = rpcButtons.copy(button1Url = it)
                            }
                        }
                        RpcField(
                            value = rpcButtons.button2,
                            label = R.string.activity_button2_text
                        ) {
                            rpcButtons = rpcButtons.copy(button2 = it)
                        }
                        AnimatedVisibility(visible = rpcButtons.button2.isNotEmpty()) {
                            RpcField(
                                value = rpcButtons.button2Url,
                                label = R.string.activity_button2_url
                            ) {
                                rpcButtons = rpcButtons.copy(button2Url = it)
                            }
                        }
                    }
                })
        }

        if (showActivityTypeDialog) {
            AlertDialog(
                onDismissRequest = {
                    showActivityTypeDialog = false
                },
                text = {
                    var activityTypeisExpanded by remember {
                        mutableStateOf(false)
                    }
                    val icon = if (activityTypeisExpanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown
                        RpcField(value = customActivityType,
                            label = R.string.activity_type,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            trailingIcon = {
                                Icon(imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier.clickable {
                                        activityTypeisExpanded = !activityTypeisExpanded
                                    })
                            }) {
                            customActivityType = it
                        }
                    DropdownMenu(
                        expanded = activityTypeisExpanded, onDismissRequest = {
                            activityTypeisExpanded = !activityTypeisExpanded
                        }, modifier = Modifier.fillMaxWidth()
                    ) {
                        val rpcTypes = listOf(
                            Pair("Playing", 0),
                            Pair("Streaming", 1),
                            Pair("Listening", 2),
                            Pair("Watching", 3),
                            Pair("Competing", 5)
                        )
                        rpcTypes.forEach {
                            DropdownMenuItem(
                                text = {
                                    Text(text = it.first)
                                },
                                onClick = {
                                    customActivityType = it.second.toString()
                                    activityTypeisExpanded = false
                                },
                            )
                        }
                    }

                },
                confirmButton = {
                    TextButton(onClick = {
                            if (customActivityType.toInt() in 0..5) {
                                Prefs[Prefs.CUSTOM_ACTIVITY_TYPE] = customActivityType.toInt()
                                showActivityTypeDialog = false
                            }
                    }) {
                        Text(text = "Save")
                    }
                }
            )
        }
    }
}