/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * LoadConfigDialog.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_custom_rpc.components.sheet

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.blankj.utilcode.util.FileIOUtils
import com.my.kizzy.domain.model.rpc.RpcConfig
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.BrowseFilesButton
import com.my.kizzy.ui.components.dialog.SingleChoiceItem
import java.io.File

@Composable
fun LoadConfig(
    onDismiss: () -> Unit,
    onConfigSelected: (RpcConfig) -> Unit
) {
    val ctx = LocalContext.current
    val dir = ctx.dir()
    dir.mkdirs()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        ctx.handleUriResult(uri){
            onDismiss()
            onConfigSelected(it.stringToData())
        }
    }
    AlertDialog(onDismissRequest = { onDismiss() },
        confirmButton = {},
        title = { Text(text = stringResource(id = R.string.select_a_config)) }, text = {
            LazyColumn {
                item {
                    BrowseFilesButton(modifier = Modifier.fillMaxWidth()) {
                        launcher.launch("application/json")
                    }
                }
                val files = dir.list(FILE_FILTER)?.asList()
                files?.forEach { file ->
                    item {
                        SingleChoiceItem(
                            text = file.dropLast(5),
                            selected = false
                        ) {
                            onDismiss()
                            FileIOUtils.readFile2String(File(dir,file)).also {
                                onConfigSelected(it.stringToData())
                            }
                        }
                    }
                }
            }
        })
}