package com.my.kizzy.ui.screen.rpc.custom

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.my.kizzy.R
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.SwitchBar
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomRPC(navController: NavController) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })
    val context = LocalContext.current
    var isCustomRpcEnabled by remember {
        mutableStateOf(false)
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

    Scaffold(
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
                navigationIcon = { BackButton { navController.popBackStack() } },
                scrollBehavior = scrollBehavior
            )
        }
    )
    { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    SwitchBar(
                        title = "Enable Custom Rpc",
                        checked = isCustomRpcEnabled
                    ) {
                        isCustomRpcEnabled = !isCustomRpcEnabled
                        context.startService(Intent(context, CustomRpcService::class.java))
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
                        onClick = {
                            timestampsPicker(
                                context = context
                            ) {
                                startTimestamps = it
                            }
                        },
                        enabled = false
                    )
                }

                item {
                    RpcField(
                        value = stopTimestamps,
                        label = R.string.activity_stop_timestamps,
                        onClick = {
                            timestampsPicker(
                                context = context
                            ) {
                                stopTimestamps = it
                            }
                        },
                        enabled = false
                    )
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
                    modifier = Modifier.fillMaxWidth()
                        .padding(10.dp)
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
    onClick: () -> Unit = {},
    trailingIcon: @Composable (() -> Unit)? = null,
    @StringRes
    label: Int,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    onValueChange: (String) -> Unit = {},
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clickable { onClick() },
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


fun timestampsPicker(context: Context, onTimeSet: (String) -> Unit) {
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
                    onTimeSet(time.time.time.toString() + "")
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

@Preview
@Composable
fun Custom() {
    val navController = rememberNavController()
    CustomRPC(navController = navController)
}