/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * DeleteConfigDialog.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_custom_rpc.components.sheet

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.FileUtils
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.dialog.MultiChoiceItem

@Composable
fun DeleteConfigDialog(
    onDismiss: () -> Unit,
    onFilesDeleted: (String) -> Unit
) {

    val configs: MutableState<List<String>> = remember {
        mutableStateOf(listOf())
    }
    val ctx = LocalContext.current
    val dir = ctx.dir()
    dir.mkdirs()

    AlertDialog(onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    configs.value.forEach {
                        FileUtils.delete("$dir/$it")
                        onFilesDeleted("${it.dropLast(5)} was deleted Successfully")
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 2.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(text = "Delete")
            }
        },
        title = { Text(text = stringResource(id = R.string.delete_configs)) },
        text = {
            LazyColumn {
                val files = dir.list(FILE_FILTER)
                files?.let {
                    items(files.size) { item ->
                        MultiChoiceItem(
                            text = files[item].dropLast(5),
                            checked = configs.value.contains(files[item])
                        ) {
                            val newList = mutableListOf<String>()
                            configs.value.forEach {
                                newList.add(it)
                            }
                            if (configs.value.contains(files[item])) newList.remove(files[item])
                            else newList += files[item]
                            newList.also { configs.value = it }
                        }
                    }
                }

            }
        }
    )
}