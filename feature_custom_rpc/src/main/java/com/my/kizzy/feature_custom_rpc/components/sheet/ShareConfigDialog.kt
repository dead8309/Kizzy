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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.my.kizzy.data.utils.shareFile
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.dialog.SingleChoiceItem
import java.io.File

@Composable
fun ShareConfig(
    onDismiss: () -> Unit
) {
    val ctx = LocalContext.current
    val dir = ctx.dir()
    dir.mkdirs()
    AlertDialog(onDismissRequest = { onDismiss() },
        confirmButton = {},
        title = { Text(text = stringResource(id = R.string.select_a_config)) }, text = {
            LazyColumn {
                val files = dir.list(FILE_FILTER)?.asList()
                files?.forEach { file ->
                    item {
                        SingleChoiceItem(
                            text = file.dropLast(5),
                            selected = false
                        ) {
                            onDismiss()
                            ctx.shareFile(File(dir,file))
                        }
                    }
                }
            }
        })
}