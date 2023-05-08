/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Menu.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.screen.home.custom

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.google.gson.GsonBuilder
import com.kizzy.strings.R
import com.my.kizzy.data.rpc.Constants
import com.my.kizzy.data.utils.getFileName
import com.my.kizzy.domain.model.RpcConfig
import com.my.kizzy.domain.model.User
import com.my.kizzy.feature_profile.ui.component.ProfileCard
import com.my.kizzy.preference.Prefs
import com.my.kizzy.ui.components.BrowseFilesButton
import com.my.kizzy.ui.components.dialog.MultiChoiceItem
import com.my.kizzy.ui.components.dialog.SingleChoiceItem
import com.my.kizzy.utils.Log.logger
import java.io.File
import java.io.FilenameFilter

private val gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()
private val FILE_FILTER = FilenameFilter { _: File?, f: String ->
    f.endsWith(".json")
}
private fun Context.dir() = if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU)
    File(this.filesDir,"Configs")
else{
    val selected = Prefs[Prefs.CONFIGS_DIRECTORY, Constants.DOWNLOADS_DIRECTORY]
    if (selected == Constants.DOWNLOADS_DIRECTORY)
        File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Kizzy"
        )
    else
        File(this.filesDir, "Configs")
}
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
                           logger.d("Directory",dir.absolutePath)
                           FileIOUtils.readFile2String(File(dir,file)).also {
                               onConfigSelected(it.stringToData())
                           }
                       }
                   }
               }
           }
       })
}

fun Context.handleUriResult(uri: Uri?, onSuccess: (json: String) -> Unit) {
    if (uri == null)
        return
    val fileName = this.getFileName(uri)
    if (!fileName.endsWith(".json"))
        return

    val file = File(this.cacheDir, "tmp.json")
    val inputStream = this.contentResolver.openInputStream(uri)
    inputStream?.use { input ->
        file.outputStream().use { out ->
            input.copyTo(out)
        }
    }
    FileIOUtils.readFile2String(file).also { json ->
        onSuccess(json)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveConfig(
    rpc: RpcConfig,
    onDismiss: () -> Unit,
    onSaved: (String) -> Unit
) {
    val ctx = LocalContext.current
    var configName by remember {
        mutableStateOf("")
    }
    val dir = ctx.dir()
    dir.mkdirs()

    AlertDialog(onDismissRequest = { onDismiss() },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    onDismiss()
                    FileIOUtils
                        .writeFileFromString(
                            "$dir/$configName.json",
                            rpc.dataToString()
                        ).also {
                            when (it) {
                                true -> onSaved("Saved $configName Successfully")
                                false -> onSaved("Error Saving Config")
                            }
                        }
                }) {
                Text(text = "Save")
            }
        },
        title = { Text(text = stringResource(id = R.string.save_config)) },
        text = {
            TextField(
                onValueChange = {
                    configName = it
                },
                label = { Text(text = "Config Name") },
                value = configName,
            )
        })
}


@Composable
fun DeleteConfig(
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
fun RpcConfig.dataToString(): String {
    return gson.toJson(this)
}

fun String.stringToData(): RpcConfig {
    return try {
        gson.fromJson(this, RpcConfig::class.java)
    } catch (ex: Exception) {
        RpcConfig()
    }
}


@Composable
fun PreviewDialog(
    user: User,
    rpc: RpcConfig? = null,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = {
        onDismiss()
    }) {
        ProfileCard(
            user = user, padding = 0.dp,
            rpcConfig = rpc,
            type = rpc?.type.getType(rpc?.name),
            showTs = false
        )
    }
}

private fun String?.getType(name: String?): String {
    val type: Int = try {
        if (!this.isNullOrEmpty()) this.toDouble().toInt()
        else 0
    } catch (ex: NumberFormatException) {
        0
    }
    return when (type) {
        1 -> "Streaming on $name"
        2 -> "Listening $name"
        3 -> "Watching $name"
        4 -> ""
        5 -> "Competing in $name"
        else -> "Playing a game"
    }
}

@Preview
@Composable
fun PreviewLoadConfig() {
    LoadConfig(onDismiss = {}, onConfigSelected = {})
}