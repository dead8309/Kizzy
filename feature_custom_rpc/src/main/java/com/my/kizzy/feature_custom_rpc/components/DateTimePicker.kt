/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * DateTimePicker.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_custom_rpc.components

import android.text.format.DateFormat.is24HourFormat
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Keyboard
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.TimePickerSelectionMode
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.my.kizzy.resources.R
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    selectedDate: Long?,
    onDismiss: () -> Unit,
    onDateTimeSelected: (Long) -> Unit
) {
    var showDatePickerDialog by remember { mutableStateOf(true) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate
    )
    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePickerDialog = false
                onDismiss()
            },
            confirmButton = {
                TextButton(onClick = {
                    showDatePickerDialog = false
                    showTimePickerDialog = true
                }) {
                    Text(stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDatePickerDialog = false
                    onDismiss()
                }) {
                    Text(stringResource(id = R.string.cancel))
                }
            })
        {
            DatePicker(state = datePickerState,
                title = {
                    Text(
                        text = stringResource(R.string.set_timestamps),
                        modifier = Modifier.padding(PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp))
                    )
                })
        }
    }
    if (showTimePickerDialog) {
        TimePickerDialog(
            onTimeSelected = { hour, minutes ->
                showTimePickerDialog = false
                datePickerState.selectedDateMillis?.let {
                    onDateTimeSelected(
                        getDateTimeInMillis(
                            it,
                            hour,
                            minutes
                        )
                    )
                }
                onDismiss()
            },
            onDismiss = {
                showTimePickerDialog = false
                onDismiss()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    onTimeSelected: (hours: Int, minutes: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val ctx = LocalContext.current
    val timePickerState: TimePickerStateImpl =
        rememberSaveable(saver = TimePickerStateImpl.Saver()) {
            TimePickerStateImpl(
                initialHour = 0,
                initialMinute = 0,
                is24Hour = is24HourFormat(ctx)
            )
        }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.set_timestamps),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                )
                TimePicker(
                    state = timePickerState,
                    layoutType = TimePickerLayoutType.Vertical,
                )
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    IconButton(onClick = { timePickerState.is24hour = !timePickerState.is24hour }) {
                        Icon(
                            imageVector = Icons.Outlined.Keyboard,
                            contentDescription = "Toggle time picker type",
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onDismiss) {
                        Text(
                            stringResource(id = R.string.cancel)
                        )
                    }
                    TextButton(onClick = {
                        onTimeSelected(timePickerState.hour, timePickerState.minute)
                    }) {
                        Text(
                            stringResource(id = R.string.confirm)
                        )
                    }
                }
            }
        }
    }
}

internal fun getDateTimeInMillis(
    dateMillis: Long,
    hours: Int,
    minutes: Int,
): Long {
    val calender = Calendar.getInstance().apply {
        timeInMillis = dateMillis
        set(Calendar.HOUR_OF_DAY, hours % 12 + if (hours >= 12) 12 else 0)
        set(Calendar.MINUTE, minutes)
    }
    return calender.timeInMillis
}

@Preview
@Composable
fun DateTimePickerPreview() {
    DateTimePickerDialog(
        selectedDate = null,
        onDismiss = {},
        onDateTimeSelected = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
private class TimePickerStateImpl(
    initialHour: Int,
    initialMinute: Int,
    is24Hour: Boolean,
) : TimePickerState {
    init {
        require(initialHour in 0..23) { "initialHour should in [0..23] range" }
        require(initialMinute in 0..59) { "initialMinute should be in [0..59] range" }
    }

    override var is24hour by mutableStateOf(is24Hour)

    override var selection by mutableStateOf(TimePickerSelectionMode.Hour)

    override var isAfternoon by mutableStateOf(initialHour >= 12)

    val hourState = mutableIntStateOf(initialHour % 12)

    val minuteState = mutableIntStateOf(initialMinute)

    override var minute: Int
        get() = minuteState.intValue
        set(value) {
            minuteState.intValue = value
        }

    override var hour: Int
        get() = hourState.intValue + if (isAfternoon) 12 else 0
        set(value) {
            isAfternoon = value >= 12
            hourState.intValue = value % 12
        }

    companion object {
        /** The default [Saver] implementation for [TimePickerState]. */
        fun Saver(): Saver<TimePickerStateImpl, *> =
            Saver(
                save = { listOf(it.hour, it.minute, it.is24hour) },
                restore = { value ->
                    TimePickerStateImpl(
                        initialHour = value[0] as Int,
                        initialMinute = value[1] as Int,
                        is24Hour = value[2] as Boolean
                    )
                }
            )
    }
}