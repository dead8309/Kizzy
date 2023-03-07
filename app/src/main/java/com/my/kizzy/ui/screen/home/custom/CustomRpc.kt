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

package com.my.kizzy.ui.screen.home.custom

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.gson.Gson
import com.my.kizzy.R
import com.my.kizzy.data.remote.User
import com.my.kizzy.domain.services.AppDetectionService
import com.my.kizzy.domain.services.CustomRpcService
import com.my.kizzy.domain.services.ExperimentalRpc
import com.my.kizzy.domain.services.MediaRpcService
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.SwitchBar
import com.my.kizzy.data.utils.AppUtils
import com.my.kizzy.data.preference.Prefs
import kotlinx.coroutines.launch
import java.util.*

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CustomRPC(onBackPressed: () -> Unit, viewModel: CustomScreenViewModel) {
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val storagePermissionState = rememberPermissionState(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState(),
            canScroll = { true })
    val context = LocalContext.current

    var isCustomRpcEnabled by remember {
        mutableStateOf(AppUtils.customRpcRunning())
    }
    with(viewModel) {
        Scaffold(snackbarHost = { SnackbarHost(snackBarHostState) },
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(title = {
                    Text(
                        text = "Custom Rpc",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                    navigationIcon = { BackButton { onBackPressed() } },
                    scrollBehavior = scrollBehavior,
                    actions = {
                        IconButton(onClick = { menuClicked = !menuClicked }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert, contentDescription = "menu"
                            )
                            DropdownMenu(expanded = menuClicked,
                                onDismissRequest = { menuClicked = !menuClicked }) {
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                    when (storagePermissionState.status) {
                                        PermissionStatus.Granted -> {}
                                        is PermissionStatus.Denied -> {
                                            val reqText =
                                                if ((storagePermissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                                                    stringResource(id = R.string.text_after_permission_denied)
                                                } else {
                                                    stringResource(id = R.string.request_for_permission)
                                                }
                                            AlertDialog(onDismissRequest = {}, confirmButton = {
                                                Button(onClick = { storagePermissionState.launchPermissionRequest() }) {
                                                    Text(text = stringResource(id = R.string.grant_permission))
                                                }
                                            }, title = {
                                                Text(text = stringResource(id = R.string.permission_required))
                                            }, icon = {
                                                Icon(
                                                    imageVector = Icons.Default.Folder,
                                                    contentDescription = "storage"
                                                )
                                            }, text = {
                                                Text(text = reqText)
                                            })
                                        }
                                    }
                                }
                                MenuItem(
                                    title = stringResource(id = R.string.load_config),
                                    icon = Icons.Default.FileOpen
                                ) {
                                    menuClicked = !menuClicked
                                    showLoadDialog = !showLoadDialog
                                }
                                MenuItem(
                                    title = stringResource(id = R.string.save_config),
                                    icon = Icons.Default.SaveAs
                                ) {
                                    menuClicked = !menuClicked
                                    showSaveDialog = true
                                }

                                MenuItem(
                                    title = stringResource(id = R.string.delete_configs),
                                    icon = Icons.Default.Delete
                                ) {
                                    menuClicked = !menuClicked
                                    showDeleteDialog = true
                                }


                                DropdownMenuItem(text = { Text(stringResource(id = R.string.preview_rpc)) },
                                    onClick = {
                                        menuClicked = !menuClicked
                                        showPreviewDialog = true
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_rpc_placeholder),
                                            contentDescription = null,
                                            modifier = Modifier.size(
                                                Icons.Default.Delete.defaultWidth,
                                                Icons.Default.Delete.defaultHeight
                                            )
                                        )
                                    })
                            }
                        }
                    })
            }) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                val rpc = RpcIntent(
                    name = name,
                    details = details,
                    state = state,
                    timeatampsStart = startTimestamps,
                    timeatampsStop = stopTimestamps,
                    status = status,
                    button1 = button1,
                    button2 = button2,
                    button1link = button1Url,
                    button2link = button2Url,
                    largeImg = largeImg,
                    largeText = largeImgText,
                    smallImg = smallImg,
                    smallText = smallImgText,
                    type = type
                )

                if (showLoadDialog) {
                    LoadConfig(onDismiss = {
                        showLoadDialog = false
                    }) {
                        name = it.name
                        details = it.details
                        state = it.state
                        startTimestamps = it.timeatampsStart
                        stopTimestamps = it.timeatampsStop
                        status = it.status
                        button1 = it.button1
                        button2 = it.button2
                        button1Url = it.button1link
                        button2Url = it.button2link
                        largeImg = it.largeImg
                        largeImgText = it.largeText
                        smallImg = it.smallImg
                        smallImgText = it.smallText
                        type = it.type
                    }
                } else if (showSaveDialog) {
                    SaveConfig(
                        rpc = rpc,
                        onDismiss = { showSaveDialog = false }
                    ) {
                        scope.launch {
                            snackBarHostState.showSnackbar(it)
                        }
                    }
                } else if (showDeleteDialog) {
                    DeleteConfig(onDismiss = {
                        showDeleteDialog = false
                    }) {
                        scope.launch {
                            snackBarHostState.showSnackbar(it)
                        }
                    }
                } else if(showPreviewDialog) {
                    val json = Prefs[Prefs.USER_DATA, "{}"]
                    val user = Gson().fromJson(json, User::class.java)
                    PreviewDialog(user = user, rpc, onDismiss = {
                        showPreviewDialog = false
                    })
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
                                    context.stopService(
                                        Intent(
                                            context, AppDetectionService::class.java
                                        )
                                    )
                                    context.stopService(
                                        Intent(
                                            context, MediaRpcService::class.java
                                        )
                                    )
                                    context.stopService(Intent(context, ExperimentalRpc::class.java))
                                    val intent = Intent(context, CustomRpcService::class.java)
                                    val string = Gson().toJson(rpc)
                                    intent.putExtra("RPC", string)
                                    Prefs[Prefs.LAST_RUN_CUSTOM_RPC] = string
                                    context.startService(intent)
                                }
                                false -> context.stopService(
                                    Intent(
                                        context, CustomRpcService::class.java
                                    )
                                )
                            }
                        }
                    }

                    item {
                        RpcField(
                            value = name, label = R.string.activity_name
                        ) {
                            name = it
                        }
                    }

                    item {
                        RpcField(
                            value = details, label = R.string.activity_details
                        ) {
                            details = it
                        }
                    }

                    item {
                        RpcField(
                            value = state, label = R.string.activity_state
                        ) {
                            state = it
                        }
                    }

                    item {
                        RpcField(value = startTimestamps,
                            label = R.string.activity_start_timestamps,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            trailingIcon = {
                                Icon(imageVector = Icons.Default.EditCalendar,
                                    contentDescription = null,
                                    modifier = Modifier.clickable {
                                        timestampsPicker(
                                            context = context
                                        ) {
                                            startTimestamps = it.toString()
                                        }
                                    })
                            }) {
                            startTimestamps = it
                        }
                    }

                    item {
                        RpcField(value = stopTimestamps,
                            label = R.string.activity_stop_timestamps,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            trailingIcon = {
                                Icon(imageVector = Icons.Default.EditCalendar,
                                    contentDescription = null,
                                    modifier = Modifier.clickable {
                                        timestampsPicker(
                                            context = context
                                        ) {
                                            stopTimestamps = it.toString()
                                        }
                                    })
                            }) {
                            stopTimestamps = it
                        }
                    }

                    item {
                        RpcField(
                            value = status, label = R.string.activity_status_online_idle_dnd
                        ) {
                            status = it
                        }
                    }

                    item {
                        RpcField(
                            value = button1, label = R.string.activity_button1_text
                        ) {
                            button1 = it
                        }
                    }
                    item {
                        AnimatedVisibility(visible = button1.isNotBlank()) {
                            RpcField(
                                value = button1Url, label = R.string.activity_button1_url
                            ) {
                                button1Url = it
                            }
                        }
                    }

                    item {
                        RpcField(
                            value = button2, label = R.string.activity_button2_text
                        ) {
                            button2 = it
                        }
                    }

                    item {
                        AnimatedVisibility(visible = button2.isNotBlank()) {
                            RpcField(
                                value = button2Url, label = R.string.activity_button2_url
                            ) {
                                button2Url = it
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
                        RpcField(value = largeImg,
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
                                    viewModel.uploadImage(uri) {
                                        showProgress = false
                                        openPickerDialog = false
                                        largeImg = it
                                    }
                                }
                            }) {
                            largeImg = it
                        }
                    }
                    item {
                        AnimatedVisibility(visible = largeImg.isNotBlank()) {
                            RpcField(
                                value = largeImgText, label = R.string.activity_large_text
                            ) {
                                largeImgText = it
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
                        RpcField(value = smallImg,
                            label = R.string.activity_small_image,
                            trailingIcon = {
                                Icon(imageVector = Icons.Default.Image,
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
                                    viewModel.uploadImage(uri) {
                                        showProgress = false
                                        openPickerDialog = false
                                        smallImg = it
                                    }
                                }
                            }) {
                            smallImg = it
                        }
                    }
                    item {
                        AnimatedVisibility(visible = smallImg.isNotBlank()) {
                            RpcField(
                                value = smallImgText, label = R.string.activity_small_text
                            ) {
                                smallImgText = it
                            }
                        }
                    }
                    val icon = if (activityTypeisExpanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown

                    item {
                        RpcField(value = type,
                            label = R.string.activity_type,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            trailingIcon = {
                                Icon(imageVector = icon,
                                    contentDescription = null,
                                    modifier = Modifier.clickable {
                                        activityTypeisExpanded = !activityTypeisExpanded
                                    })
                            }) {
                            type = it
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
                                        type = it.second.toString()
                                        activityTypeisExpanded = false
                                    },
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(100.dp))
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RpcField(
    value: String,
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    @StringRes label: Int,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    content: @Composable (() -> Unit) = {},
    onValueChange: (String) -> Unit = {}
) {
    Column {
        TextField(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            enabled = enabled,
            label = { Text(stringResource(id = label)) },
            keyboardOptions = keyboardOptions,
            trailingIcon = trailingIcon
        )
        content()
    }
}

fun timestampsPicker(context: Context, onTimeSet: (Long) -> Unit) {
    val currentDate = Calendar.getInstance()
    val time = Calendar.getInstance()

    DatePickerDialog(
        context, { _, year, month, day ->
            time.set(year, month, day)
            TimePickerDialog(
                context, { _, hourOfDay, minute ->
                    time.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    time.set(Calendar.MINUTE, minute)
                    onTimeSet(time.time.time)
                }, currentDate[Calendar.HOUR_OF_DAY], currentDate[Calendar.MINUTE], false
            ).show()
        }, currentDate[Calendar.YEAR], currentDate[Calendar.MONTH], currentDate[Calendar.DATE]
    ).show()
}

@Composable
fun MenuItem(
    title: String, icon: ImageVector, onClick: () -> Unit
) {
    DropdownMenuItem(text = { Text(title) }, onClick = { onClick() }, leadingIcon = {
        Icon(
            imageVector = icon, contentDescription = null
        )
    })
}
