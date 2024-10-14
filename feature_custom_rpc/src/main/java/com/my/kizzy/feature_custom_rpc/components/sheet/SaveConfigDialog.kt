/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * SaveConfigDialog.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_custom_rpc.components.sheet

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.blankj.utilcode.util.FileIOUtils
import com.my.kizzy.domain.model.rpc.RpcConfig
import com.my.kizzy.resources.R

@Composable
fun SaveConfigDialog(
    rpc: RpcConfig,
    onDismiss: () -> Unit,
    onSaved: (String) -> Unit
) {
    val ctx = LocalContext.current
    var configName by remember {
        mutableStateOf("")
    }
    var isError by remember {
        mutableStateOf(false)
    }
    val dir = ctx.dir()
    dir.mkdirs()

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    if (configName.isEmpty()) {
                        isError = true
                        return@OutlinedButton
                    }
                    onDismiss()
                    FileIOUtils
                        .writeFileFromString(
                            "$dir/$configName.json",
                            rpc.dataToString()
                        ).also {
                            when (it) {
                                true -> onSaved(ctx.getString(R.string.saved_config_toast, configName))
                                false -> onSaved(ctx.getString(R.string.error_saving_config_toast))
                            }
                        }
                }) {
                Text(text = stringResource(R.string.save))
            }
        },
        title = { Text(text = stringResource(id = R.string.save_config)) },
        text = {
            TextField(
                onValueChange = {
                    configName = it
                    isError = it.isEmpty()
                },
                label = { Text(text = stringResource(R.string.config_name)) },
                value = configName,
                isError = isError,
                supportingText = { if (isError) Text(stringResource(R.string.config_name_empty)) else Unit }
            )
        })
}