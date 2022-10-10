package com.my.kizzy.ui.screen.custom

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import com.my.kizzy.service.AppDetectionService
import com.my.kizzy.service.CustomRpcService
import com.my.kizzy.service.MediaRpcService
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.SwitchBar
import com.my.kizzy.utils.AppUtils
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun CustomRPC(onBackPressed: () -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val storagePermissionstate = rememberPermissionState(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })
    val context = LocalContext.current

    var isCustomRpcEnabled by remember {
        mutableStateOf(AppUtils.customRpcRunning(context))
    }

    var name by remember {
        mutableStateOf("")
    }
    var details by remember {
        mutableStateOf("")
    }
    var state by remember {
        mutableStateOf("")
    }
    var startTimestamps by remember {
        mutableStateOf("")
    }
    var stopTimestamps by remember {
        mutableStateOf("")
    }
    var status by remember {
        mutableStateOf("")
    }
    var button1 by remember {
        mutableStateOf("")
    }
    var button2 by remember {
        mutableStateOf("")
    }
    var button1Url by remember {
        mutableStateOf("")
    }
    var button2Url by remember {
        mutableStateOf("")
    }
    var largeImg by remember {
        mutableStateOf("")
    }
    var smallImg by remember {
        mutableStateOf("")
    }
    var type by remember {
        mutableStateOf("")
    }

    var activityTypeisExpanded by remember {
        mutableStateOf(false)
    }

    var menuClicked by remember {
        mutableStateOf(false)
    }

    var showLoadDialog by remember {
        mutableStateOf(false)
    }
    var showSaveDialog by remember {
        mutableStateOf(false)
    }
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
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
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "menu"
                        )
                        DropdownMenu(
                            expanded = menuClicked,
                            onDismissRequest = { menuClicked = !menuClicked }) {

                            when (storagePermissionstate.status) {
                                PermissionStatus.Granted -> {}
                                is PermissionStatus.Denied -> {
                                    val reqText = if ((storagePermissionstate.status as PermissionStatus.Denied).shouldShowRationale) {
                                        stringResource(id = R.string.text_after_permission_denied)
                                    } else {
                                        stringResource(id = R.string.request_for_permission)
                                    }
                                    AlertDialog(onDismissRequest = {},
                                        confirmButton = {
                                            Button(onClick = { storagePermissionstate.launchPermissionRequest() }) {
                                                Text(text = stringResource(id = R.string.grant_permission))
                                            }
                                        },
                                        title = {
                                            Text(text = stringResource(id = R.string.permission_required))
                                                },
                                        icon = {
                                            Icon(imageVector = Icons.Default.Folder
                                                , contentDescription = "storage")
                                        },
                                        text = {
                                            Text(text = reqText)
                                        }
                                    )
                                }
                            }

                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.load_config)) },
                                onClick = {
                                    menuClicked = !menuClicked
                                    showLoadDialog = !showLoadDialog
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.FileOpen,
                                        contentDescription = null
                                    )
                                }
                            )

                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.save_config)) },
                                onClick = {
                                    menuClicked = !menuClicked
                                    showSaveDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.SaveAs,
                                        contentDescription = null
                                    )
                                }
                            )

                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.delete_configs)) },
                                onClick = {
                                    menuClicked = !menuClicked
                                    showDeleteDialog = true
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )

                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.preview_rpc)) },
                                onClick = {
                                    menuClicked = !menuClicked
                                    PreviewDialog.showPreview(
                                        rpc = IntentRpcData(
                                            name = name,
                                            details = details,
                                            state = state,
                                            startTime = startTimestamps,
                                            StopTime = stopTimestamps,
                                            status = status,
                                            button1 = button1,
                                            button2 = button2,
                                            button1Url = button1Url,
                                            button2Url = button2Url,
                                            largeImg = largeImg,
                                            smallImg = smallImg,
                                            type = type
                                        ),
                                        context = context
                                    )
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
                                }
                            )
                        }
                    }
                }
            )
        }
    )
    { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (showLoadDialog) {
                LoadConfig(
                    onDismiss = {
                        showLoadDialog = false
                    }
                ) {
                    name = it.name
                    details = it.details
                    state = it.state
                    startTimestamps = it.startTime
                    stopTimestamps = it.StopTime
                    status = it.status
                    button1 = it.button1
                    button2 = it.button2
                    button1Url = it.button1Url
                    button2Url = it.button2Url
                    largeImg = it.largeImg
                    smallImg = it.smallImg
                    type = it.type
                }
            } else if (showSaveDialog) {
                SaveConfig(
                    rpc = IntentRpcData(
                        name = name,
                        details = details,
                        state = state,
                        startTime = startTimestamps,
                        StopTime = stopTimestamps,
                        status = status,
                        button1 = button1,
                        button2 = button2,
                        button1Url = button1Url,
                        button2Url = button2Url,
                        largeImg = largeImg,
                        smallImg = smallImg,
                        type = type
                    ),
                    onDismiss = { showSaveDialog = false }
                ) {
                    scope.launch {
                        snackbarHostState.showSnackbar(it)
                    }
                }
            } else if (showDeleteDialog) {
                DeleteConfig(onDismiss = {
                    showDeleteDialog = false
                }) {
                    scope.launch {
                        snackbarHostState.showSnackbar(it)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    SwitchBar(
                        title = stringResource(id = R.string.enable_customRpc ),
                        checked = isCustomRpcEnabled
                    ) {
                        isCustomRpcEnabled = !isCustomRpcEnabled
                        when(isCustomRpcEnabled){
                            true ->  {
                                context.stopService(Intent(context, AppDetectionService::class.java))
                                context.stopService(Intent(context, MediaRpcService::class.java))
                                val intent = Intent(context, CustomRpcService::class.java)
                                val string = Gson().toJson(
                                    IntentRpcData(
                                        name = name,
                                        details = details,
                                        state = state,
                                        startTime = startTimestamps,
                                        StopTime = stopTimestamps,
                                        status = status,
                                        button1 = button1,
                                        button2 = button2,
                                        button1Url = button1Url,
                                        button2Url = button2Url,
                                        largeImg = largeImg,
                                        smallImg = smallImg,
                                        type = type,
                                    )
                                )
                                intent.putExtra("RPC", string)
                                context.startService(intent)
                            }
                            false -> context.stopService(Intent(context, CustomRpcService::class.java))
                        }
                    }
                }

                item {
                    RpcField(
                        value = name,
                        label = R.string.activity_name
                    ) {
                        name = it
                    }
                }

                item {
                    RpcField(
                        value = details,
                        label = R.string.activity_details
                    ) {
                        details = it
                    }
                }

                item {
                    RpcField(
                        value = state,
                        label = R.string.activity_state
                    ) {
                        state = it
                    }
                }

                item {
                    RpcField(
                        value = startTimestamps,
                        label = R.string.activity_start_timestamps,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.EditCalendar,
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    timestampsPicker(
                                        context = context
                                    ) {
                                        startTimestamps = it.toString()
                                    }
                                }
                            )
                        }
                    ) {
                        startTimestamps = it
                    }
                }

                item {
                    RpcField(
                        value = stopTimestamps,
                        label = R.string.activity_stop_timestamps,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.EditCalendar,
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    timestampsPicker(
                                        context = context
                                    ) {
                                        stopTimestamps = it.toString()
                                    }
                                }
                            )
                        }
                    ) {
                        stopTimestamps = it
                    }
                }

                item {
                    RpcField(
                        value = status,
                        label = R.string.activity_status_online_idle_dnd
                    ) {
                        status = it
                    }
                }

                item {
                    RpcField(
                        value = button1,
                        label = R.string.activity_button1_text
                    ) {
                        button1 = it
                    }
                }

                item {
                    RpcField(
                        value = button2,
                        label = R.string.activity_button2_text
                    ) {
                        button2 = it
                    }
                }

                item {
                    RpcField(
                        value = button1Url,
                        label = R.string.activity_button1_url
                    ) {
                        button1Url = it
                    }
                }

                item {
                    RpcField(
                        value = button2Url,
                        label = R.string.activity_button2_url
                    ) {
                        button2Url = it
                    }
                }


                item {
                    RpcField(
                        value = largeImg,
                        label = R.string.activity_large_image
                    ) {
                        largeImg = it
                    }
                }

                item {
                    RpcField(
                        value = smallImg,
                        label = R.string.activity_small_image
                    ) {
                        smallImg = it
                    }
                }

                val icon = if (activityTypeisExpanded) Icons.Default.KeyboardArrowUp
                else Icons.Default.KeyboardArrowDown

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
                                    activityTypeisExpanded = !activityTypeisExpanded
                                }
                            )
                        }
                    ) {
                        type = it
                    }

                    DropdownMenu(
                        expanded = activityTypeisExpanded,
                        onDismissRequest = {
                            activityTypeisExpanded = !activityTypeisExpanded
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    {
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RpcField(
    value: String,
    enabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    @StringRes
    label: Int,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    onValueChange: (String) -> Unit = {},
) {
    TextField(
        modifier = Modifier
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
}


fun timestampsPicker(context: Context, onTimeSet: (Long) -> Unit) {
    val currentDate = Calendar.getInstance()
    val time = Calendar.getInstance()

    DatePickerDialog(
        context,
        { _, year, month, day ->
            time.set(year, month, day)
            TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    time.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    time.set(Calendar.MINUTE, minute)
                    onTimeSet(time.time.time)
                },
                currentDate[Calendar.HOUR_OF_DAY],
                currentDate[Calendar.MINUTE],
                false
            )
                .show()
        },
        currentDate[Calendar.YEAR],
        currentDate[Calendar.MONTH],
        currentDate[Calendar.DATE]
    )
        .show()

}
