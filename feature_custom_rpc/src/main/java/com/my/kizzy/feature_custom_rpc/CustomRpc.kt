/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * CustomRpc.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_custom_rpc

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.my.kizzy.data.rpc.Constants
import com.my.kizzy.data.rpc.Constants.MAX_ALLOWED_CHARACTER_LENGTH
import com.my.kizzy.data.utils.uriToFile
import com.my.kizzy.domain.model.user.User
import com.my.kizzy.feature_custom_rpc.components.BottomSheet
import com.my.kizzy.feature_custom_rpc.components.DateTimePickerDialog
import com.my.kizzy.feature_custom_rpc.components.ImagePicker
import com.my.kizzy.feature_custom_rpc.components.sheet.DeleteConfigDialog
import com.my.kizzy.feature_custom_rpc.components.sheet.LoadConfig
import com.my.kizzy.feature_custom_rpc.components.sheet.PreviewDialog
import com.my.kizzy.feature_custom_rpc.components.sheet.RequestStoragePermissionDialog
import com.my.kizzy.feature_custom_rpc.components.sheet.SaveConfigDialog
import com.my.kizzy.feature_custom_rpc.components.sheet.ShareConfig
import com.my.kizzy.feature_custom_rpc.components.sheet.dataToString
import com.my.kizzy.feature_rpc_base.AppUtils
import com.my.kizzy.feature_rpc_base.services.AppDetectionService
import com.my.kizzy.feature_rpc_base.services.CustomRpcService
import com.my.kizzy.feature_rpc_base.services.ExperimentalRpc
import com.my.kizzy.feature_rpc_base.services.MediaRpcService
import com.my.kizzy.preference.Prefs
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.RpcField
import com.my.kizzy.ui.components.SwitchBar
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
fun CustomRPC(
    state: UiState = UiState(),
    onBackPressed: () -> Unit,
    onEvent: (UiEvent) -> Unit = {},
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    CustomRpcScreen(
        onBackPressed = onBackPressed,
        onEvent = onEvent,
        snackBarHostState = snackBarHostState,
        state = state
    )
    if (state.showBottomSheet) {
        BottomSheet(onEvent = onEvent, onDismiss = {
            onEvent(UiEvent.TriggerBottomSheet)
        })
    }
    if (state.showLoadDialog) {
        LoadConfig(
            onDismiss = {
                onEvent(UiEvent.SheetEvent.TriggerLoadDialog)
            },
            onConfigSelected = {
                onEvent(UiEvent.SetFieldsFromConfig(it))
            }
        )
    }
    if (state.showShareDialog) {
        ShareConfig(
            onDismiss = {
                onEvent(UiEvent.SheetEvent.TriggerShareDialog)
            }
        )
    }
    if (state.showStoragePermissionRequestDialog) {
        RequestStoragePermissionDialog(
            onDismiss = { onEvent(UiEvent.SheetEvent.TriggerStoragePermissionRequest) }
        )
    }
    if (state.showSaveDialog) {
        SaveConfigDialog(
            rpc = state.rpcConfig,
            onDismiss = { onEvent(UiEvent.SheetEvent.TriggerSaveDialog) },
            onSaved = {
                scope.launch {
                    snackBarHostState.showSnackbar(it)
                }
            }
        )
    }
    if (state.showDeleteDialog) {
        DeleteConfigDialog(
            onDismiss = {
                onEvent(UiEvent.SheetEvent.TriggerDeleteDialog)
            },
            onFilesDeleted = {
                scope.launch {
                    snackBarHostState.showSnackbar(it)
                }
            }
        )
    }
    if (state.showPreviewDialog) {
        val json = Prefs[Prefs.USER_DATA, "{}"]
        val user = Json.decodeFromString<User>(json)
        PreviewDialog(
            user = user,
            rpc = state.rpcConfig,
            onDismiss = {
                onEvent(UiEvent.SheetEvent.TriggerPreviewDialog)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomRpcScreen(
    snackBarHostState: SnackbarHostState,
    state: UiState,
    onBackPressed: () -> Unit,
    onEvent: (UiEvent) -> Unit,

    ) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
            rememberTopAppBarState(),
            canScroll = { true })

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.main_customRpc),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = { BackButton { onBackPressed() } },
                scrollBehavior = scrollBehavior,
                actions = {
                    IconButton(onClick = { onEvent(UiEvent.TriggerBottomSheet) }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "menu"
                        )
                    }
                })
        }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            RpcTextFieldsColumn(
                snackBarHostState = snackBarHostState,
                uiState = state,
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun RpcTextFieldsColumn(
    onEvent: (UiEvent) -> Unit,
    uiState: UiState,
    snackBarHostState: SnackbarHostState,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isCustomRpcEnabled by remember {
        mutableStateOf(AppUtils.customRpcRunning())
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            SwitchBar(
                title = stringResource(id = R.string.enable_customRpc),
                isChecked = isCustomRpcEnabled
            ) {
                isCustomRpcEnabled = !isCustomRpcEnabled
                when (isCustomRpcEnabled) {
                    true -> {
                        context.stopService(Intent(context, AppDetectionService::class.java))
                        context.stopService(Intent(context, MediaRpcService::class.java))
                        context.stopService(Intent(context, ExperimentalRpc::class.java))
                        val intent = Intent(context, CustomRpcService::class.java)
                        val string = uiState.rpcConfig.dataToString()
                        intent.putExtra("RPC", string)
                        Prefs[Prefs.LAST_RUN_CUSTOM_RPC] = string
                        context.startService(intent)
                    }

                    false -> context.stopService(Intent(context, CustomRpcService::class.java))
                }
            }
        }
        with(uiState.rpcConfig) {
            item {
                RpcField(
                    value = name, label = R.string.activity_name
                ) {
                    onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(name = it)))
                }
            }

            item {
                RpcField(
                    value = details, label = R.string.activity_details
                ) {
                    onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(details = it)))
                }
            }

            item {
                RpcField(
                    value = state, label = R.string.activity_state
                ) {
                    onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(state = it)))
                }
            }

            item {
                Row {
                    RpcField(
                        value = partyCurrentSize,
                        label = R.string.party_current,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError =
                            partyCurrentSize.isNotEmpty() && (
                                    partyCurrentSize.toIntOrNull() == null ||
                                            0 >= partyCurrentSize.toInt() ||
                                            (partyMaxSize.toIntOrNull() != null && partyCurrentSize.toInt() > partyMaxSize.toInt())
                                    ),
                        errorMessage =
                            if (partyCurrentSize.isNotEmpty()) {
                                if (partyCurrentSize.toIntOrNull() == null) {
                                    stringResource(R.string.party_invalid_number)
                                } else if (0 >= partyCurrentSize.toInt()) {
                                    stringResource(R.string.party_less_than_zero)
                                } else if (partyMaxSize.toIntOrNull() != null && partyCurrentSize.toInt() > partyMaxSize.toInt()) {
                                    stringResource(R.string.party_greater_than_max)
                                } else {
                                    ""
                                }
                            } else {
                                ""
                            }

                    ) {
                        onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(partyCurrentSize = it)))
                    }
                }
            }

            item {
                AnimatedVisibility(visible = partyCurrentSize.isNotBlank()) {
                    RpcField(
                        value = partyMaxSize,
                        label = R.string.party_max,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError =
                            partyMaxSize.isNotEmpty() && (
                                    partyMaxSize.toIntOrNull() == null ||
                                            0 >= partyMaxSize.toInt() ||
                                            (partyCurrentSize.toIntOrNull() != null && partyCurrentSize.toInt() > partyMaxSize.toInt())
                                    ),
                        errorMessage =
                            if (partyMaxSize.isNotEmpty()) {
                                if (partyMaxSize.toIntOrNull() == null) {
                                    stringResource(R.string.party_invalid_number)
                                } else if (0 >= partyMaxSize.toInt()) {
                                    stringResource(R.string.party_less_than_zero)
                                } else if (partyCurrentSize.toIntOrNull() != null && partyCurrentSize.toInt() > partyMaxSize.toInt()) {
                                    stringResource(R.string.party_greater_than_max)
                                } else {
                                    ""
                                }
                            } else if (partyCurrentSize.isNotEmpty() && partyMaxSize.isBlank()) {
                                stringResource(R.string.party_max_cannot_be_empty)
                            } else {
                                ""
                            }
                    ) {
                        onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(partyMaxSize = it)))
                    }
                }
            }

            item {
                RpcField(
                    value = timestampsStart,
                    label = R.string.activity_start_timestamps,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.EditCalendar,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                onEvent(UiEvent.TriggerStartTimeStampsDialog)
                            })
                    },
                    content = {
                        if (uiState.showStartTimeStampsPickerDialog) {
                            DateTimePickerDialog(
                                selectedDate = timestampsStart.toLongOrNull(),
                                onDismiss = { onEvent(UiEvent.TriggerStartTimeStampsDialog) }
                            ) {
                                onEvent(
                                    UiEvent.SetFieldsFromConfig(
                                        uiState.rpcConfig.copy(
                                            timestampsStart = it.toString()
                                        )
                                    )
                                )
                            }
                        }
                    }) {
                    onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(timestampsStart = it)))
                }
            }

            item {
                RpcField(
                    value = timestampsStop,
                    label = R.string.activity_stop_timestamps,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.EditCalendar,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                onEvent(UiEvent.TriggerStopTimeStampsDialog)
                            })
                    },
                    content = {
                        if (uiState.showStopTimeStampsPickerDialog) {
                            DateTimePickerDialog(
                                selectedDate = timestampsStop.toLongOrNull(),
                                onDismiss = { onEvent(UiEvent.TriggerStopTimeStampsDialog) }
                            ) {
                                onEvent(
                                    UiEvent.SetFieldsFromConfig(
                                        uiState.rpcConfig.copy(
                                            timestampsStop = it.toString()
                                        )
                                    )
                                )
                            }
                        }
                    }) {
                    onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(timestampsStop = it)))
                }
            }

            val iconStatus = if (uiState.activityTypeIsExpanded)
                Icons.Default.KeyboardArrowUp
            else
                Icons.Default.KeyboardArrowDown

            item {
                RpcField(
                    value = platform,
                    label = R.string.activity_platform,
                    trailingIcon = {
                        Icon(
                            imageVector = iconStatus,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                onEvent(UiEvent.TriggerPlatformDropDownMenu)
                            })
                    },
                    content = {
                        DropdownMenu(
                            expanded = uiState.platformIsExpanded, onDismissRequest = {
                                onEvent(UiEvent.TriggerPlatformDropDownMenu)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Constants.ACTIVITY_PLATFORMS.forEach { (label, value) ->
                                DropdownMenuItem(
                                    text = {
                                        Text(text = label)
                                    },
                                    onClick = {
                                        onEvent(
                                            UiEvent.SetFieldsFromConfig(
                                                uiState.rpcConfig.copy(
                                                    platform = value
                                                )
                                            )
                                        )
                                        onEvent(UiEvent.TriggerPlatformDropDownMenu)
                                    },
                                )
                            }
                        }
                    }
                ) {
                    onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(platform = it)))
                }
            }

            item {
                RpcField(
                    value = status,
                    label = R.string.activity_status_online_idle_dnd,
                    trailingIcon = {
                        Icon(
                            imageVector = iconStatus,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                onEvent(UiEvent.TriggerStatusDropDownMenu)
                            })
                    },
                    content = {
                        DropdownMenu(
                            expanded = uiState.statusIsExpanded, onDismissRequest = {
                                onEvent(UiEvent.TriggerStatusDropDownMenu)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Constants.ACTIVITY_STATUS.forEach { (resId, value) ->
                                DropdownMenuItem(
                                    text = {
                                        Text(text = stringResource(resId))
                                    },
                                    onClick = {
                                        onEvent(
                                            UiEvent.SetFieldsFromConfig(
                                                uiState.rpcConfig.copy(
                                                    status = value
                                                )
                                            )
                                        )
                                        onEvent(UiEvent.TriggerStatusDropDownMenu)
                                    },
                                )
                            }
                        }
                    }
                ) {
                    onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(status = it)))
                }
            }

            item {
                RpcField(
                    value = button1, label = R.string.activity_button1_text,
                    isError = button1.length >= MAX_ALLOWED_CHARACTER_LENGTH,
                    errorMessage = stringResource(R.string.activity_button_max_character)
                ) {
                    onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(button1 = it)))
                }
            }
            item {
                AnimatedVisibility(visible = button1.isNotBlank()) {
                    RpcField(
                        value = button1link,
                        label = R.string.activity_button1_url
                    ) {
                        onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(button1link = it)))
                    }
                }
            }

            item {
                RpcField(
                    value = button2, label = R.string.activity_button2_text,
                    isError = button2.length >= MAX_ALLOWED_CHARACTER_LENGTH,
                    errorMessage = stringResource(R.string.activity_button_max_character)
                ) {
                    onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(button2 = it)))
                }
            }

            item {
                AnimatedVisibility(visible = button2.isNotBlank()) {
                    RpcField(
                        value = button2link,
                        label = R.string.activity_button2_url
                    ) {
                        onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(button2link = it)))
                    }
                }
            }
            item {
                var openPickerDialog by remember {
                    mutableStateOf(false)
                }
                var showProgress by remember {
                    mutableStateOf(false)
                }
                RpcField(
                    value = largeImg,
                    label = R.string.activity_large_image,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "openGallery",
                            modifier = Modifier.clickable {
                                openPickerDialog = true
                            })
                    },
                    content = {
                        ImagePicker(
                            visible = openPickerDialog,
                            onDismiss = { openPickerDialog = false },
                            showProgress = showProgress
                        ) { uri ->
                            showProgress = true
                            onEvent(UiEvent.UploadImage(context.uriToFile(uri)) { result ->
                                showProgress = false
                                openPickerDialog = false
                                onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(largeImg = result)))
                            })
                        }
                    }) {
                    onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(largeImg = it)))
                }
            }
            item {
                AnimatedVisibility(visible = largeImg.isNotBlank()) {
                    RpcField(
                        value = largeText,
                        label = R.string.activity_large_text
                    ) {
                        onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(largeText = it)))
                    }
                }
            }
            item {
                var openPickerDialog by remember {
                    mutableStateOf(false)
                }
                var showProgress by remember {
                    mutableStateOf(false)
                }
                RpcField(
                    value = smallImg,
                    label = R.string.activity_small_image,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "openGallery",
                            modifier = Modifier.clickable {
                                openPickerDialog = true
                            })
                    },
                    content = {
                        ImagePicker(
                            visible = openPickerDialog,
                            onDismiss = { openPickerDialog = false },
                            showProgress = showProgress
                        ) { uri ->
                            showProgress = true
                            onEvent(UiEvent.UploadImage(context.uriToFile(uri)) { result ->
                                showProgress = false
                                openPickerDialog = false
                                onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(smallImg = result)))
                            })
                        }
                    }) {
                    onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(smallImg = it)))
                }
            }
            item {
                AnimatedVisibility(visible = smallImg.isNotBlank()) {
                    RpcField(
                        value = smallText,
                        label = R.string.activity_small_text
                    ) {
                        onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(smallText = it)))
                    }
                }
            }
            val icon = if (uiState.activityTypeIsExpanded)
                Icons.Default.KeyboardArrowUp
            else
                Icons.Default.KeyboardArrowDown

            item {
                RpcField(
                    value = type,
                    label = R.string.activity_type,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                onEvent(UiEvent.TriggerActivityTypeDropDownMenu)
                            })
                    }) {
                    onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(type = it)))
                }

                DropdownMenu(
                    expanded = uiState.activityTypeIsExpanded, onDismissRequest = {
                        onEvent(UiEvent.TriggerActivityTypeDropDownMenu)
                    }, modifier = Modifier.fillMaxWidth()
                ) {
                    Constants.ACTIVITY_TYPE.forEach { (label, value) ->
                        DropdownMenuItem(
                            text = {
                                Text(text = label)
                            },
                            onClick = {
                                onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(type = value.toString())))
                                onEvent(UiEvent.TriggerActivityTypeDropDownMenu)
                            },
                        )
                    }
                }
            }
            item {
                AnimatedVisibility(visible = type == "1") {
                    val streamUrlInfoText = stringResource(id = R.string.stream_url_info)
                    RpcField(
                        value = url,
                        label = R.string.stream_url,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        snackBarHostState.showSnackbar(
                                            message = streamUrlInfoText
                                        )
                                    }
                                })
                        }
                    ) {
                        onEvent(UiEvent.SetFieldsFromConfig(uiState.rpcConfig.copy(url = it)))
                    }
                }
                Spacer(modifier = Modifier.size(100.dp))
            }
        }
    }
}

@Preview
@Composable
fun CustomRpcPreview() {
    CustomRPC(onBackPressed = {})
}