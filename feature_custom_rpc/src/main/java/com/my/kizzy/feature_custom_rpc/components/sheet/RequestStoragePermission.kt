/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * RequestStoragePermission.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_custom_rpc.components.sheet

import android.Manifest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.my.kizzy.resources.R

@Composable
@OptIn(ExperimentalPermissionsApi::class)
internal fun RequestStoragePermissionDialog(
    onDismiss: () -> Unit
) {
    val storagePermissionState = rememberPermissionState(
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    when (storagePermissionState.status) {
        PermissionStatus.Granted -> {}
        is PermissionStatus.Denied -> {
            val reqText =
                if ((storagePermissionState.status as PermissionStatus.Denied).shouldShowRationale) {
                    stringResource(id = R.string.text_after_permission_denied)
                } else {
                    stringResource(id = R.string.request_for_permission)
                }
            AlertDialog(
                onDismissRequest = onDismiss,
                confirmButton = {
                    Button(onClick = { storagePermissionState.launchPermissionRequest() }) {
                        Text(text = stringResource(id = R.string.grant_permission))
                    }
                },
                title = {
                    Text(text = stringResource(id = R.string.permission_required))
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = "storage"
                    )
                },
                text = {
                    Text(text = reqText)
                }
            )
        }
    }
}