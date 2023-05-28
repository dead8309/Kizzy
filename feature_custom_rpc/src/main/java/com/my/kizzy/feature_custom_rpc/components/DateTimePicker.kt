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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerLayoutType
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                        text = "Set Timestamps",
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
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        // Show timepicker according to device's time-format and convert it into millis.
        is24Hour = false
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onTimeSelected(timePickerState.hour, timePickerState.minute)
            }) {
                Text(stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        },
        text = {
            TimePicker(
                state = timePickerState,
                layoutType = TimePickerLayoutType.Vertical,
            )
        }
    )
}

internal fun getDateTimeInMillis(
    dateMillis: Long,
    hours: Int,
    minutes: Int
): Long {
    val calender = Calendar.getInstance().apply {
        timeInMillis = dateMillis
        set(Calendar.HOUR_OF_DAY, hours % 12 + if (hours > 12) 12 else 0)
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