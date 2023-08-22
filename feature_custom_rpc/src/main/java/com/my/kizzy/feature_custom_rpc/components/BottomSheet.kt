/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * BottomSheet.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_custom_rpc.components

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ClearAll
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FileOpen
import androidx.compose.material.icons.outlined.SaveAs
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.my.kizzy.feature_custom_rpc.UiEvent
import com.my.kizzy.feature_custom_rpc.components.sheet.SheetItem
import com.my.kizzy.resources.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(onEvent: (UiEvent.SheetEvent) -> Unit,onDismiss: () -> Unit) {
    val scope = rememberCoroutineScope()
    val state = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = state,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SheetItem(
                title = stringResource(id = R.string.load_config),
                icon = Icons.Outlined.FileOpen,
                onClick = {
                    scope.launch {
                        state.hide()
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                            onEvent(UiEvent.SheetEvent.TriggerStoragePermissionRequest)
                        }
                        onEvent(UiEvent.SheetEvent.TriggerLoadDialog)
                    }
                }
            )
            SheetItem(
                title = stringResource(id = R.string.save_config),
                icon = Icons.Outlined.SaveAs
            ) {
                scope.launch {
                    state.hide()
                    onEvent(UiEvent.SheetEvent.TriggerSaveDialog)
                }
            }

            SheetItem(
                title = stringResource(id = R.string.delete_configs),
                icon = Icons.Outlined.Delete
            ) {
                scope.launch {
                    state.hide()
                    onEvent(UiEvent.SheetEvent.TriggerDeleteDialog)
                }
            }

            SheetItem(
                title = stringResource(id = R.string.preview_rpc),
                onClick = {
                    scope.launch {
                        state.hide()
                        onEvent(UiEvent.SheetEvent.TriggerPreviewDialog)
                    }
                },
                icon = R.drawable.ic_rpc_placeholder
            )

            SheetItem(
                title = stringResource(id = R.string.clear_all_fields),
                onClick = {
                    scope.launch {
                        state.hide()
                        onEvent(UiEvent.SheetEvent.ClearAllFields)
                    }
                },
                icon = Icons.Outlined.ClearAll
            )

            SheetItem(
                title = stringResource(id = R.string.share_config),
                icon = Icons.Outlined.Share,
                onClick = {
                    scope.launch {
                        state.hide()
                        onEvent(UiEvent.SheetEvent.TriggerShareDialog)
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun BottomSheetPreview() {
    BottomSheet({}){

    }
}