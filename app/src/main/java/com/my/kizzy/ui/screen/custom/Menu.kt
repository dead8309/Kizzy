@file:Suppress("ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE")

package com.my.kizzy.ui.screen.custom

import android.os.Environment
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.my.kizzy.R
import com.my.kizzy.ui.common.MultiChoiceItem
import com.my.kizzy.ui.common.SingleChoiceItem
import java.io.File
import java.io.FilenameFilter


private val gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()
private val FILE_FILTER = FilenameFilter { _: File?, f: String ->
    f.endsWith(".json")
}

@Composable
fun LoadConfig(
    onDismiss: () -> Unit,
    onConfigSelected: (IntentRpcData) -> Unit
) {
    val dir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "Kizzy"
    )
    var data: String?
    dir.mkdirs()

    AlertDialog(onDismissRequest = { onDismiss() },
        confirmButton = {},
        title = { Text(text = stringResource(id = R.string.select_a_config)) }, text = {
            var selected by remember {
                mutableStateOf("")
            }
            LazyColumn {
                val files = dir.list(FILE_FILTER)?.asList()
                files?.forEach { file ->
                    item {
                        SingleChoiceItem(
                            text = file.dropLast(5),
                            selected = file.equals(selected)
                        ) {
                            onDismiss()
                            selected = file
                            FileIOUtils.readFile2String(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .toString() + "/Kizzy/"
                                        + file

                            ).also { data = it }
                            data?.let {
                                onConfigSelected(it.stringToData())
                            }
                        }
                    }
                }
            }
        })

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveConfig(
    rpc: IntentRpcData,
    onDismiss: () -> Unit,
    onSaved: (String) -> Unit
) {

    var configName by remember {
        mutableStateOf("")
    }
    val dir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "Kizzy"
    )
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

    val dir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "Kizzy"
    )
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


fun IntentRpcData.dataToString(): String {
    val value: MutableMap<String, String> = HashMap()
    value["name"] = this.name
    value["details"] = this.details
    value["state"] = this.state
    value["status"] = this.status
    value["button1"] = this.button1
    value["button2"] = this.button2
    value["largeImg"] = this.largeImg
    value["smallImg"] = this.smallImg
    value["type"] = this.type
    value["timeatampsStart"] = this.startTime
    value["timeatampsStop"] = this.StopTime
    value["button1link"] = this.button1Url
    value["button2link"] = this.button2Url
    return gson.toJson(value)
}

fun String.stringToData(): IntentRpcData {
   try {
       val values = gson.fromJson<Map<String, String>>(
           this,
           object : TypeToken<HashMap<String, String>>() {}.type
       )
       return IntentRpcData(
           values["name"] ?: "",
           values["details"] ?: "",
           values["state"] ?: "",
           values["timeatampsStart"] ?: "",
           values["timeatampsStop"] ?: "",
           values["status"] ?: "",
           values["button1"] ?: "",
           values["button2"] ?: "",
           values["button1link"] ?: "",
           values["button2link"] ?: "",
           values["largeImg"] ?: "",
           values["smallImg"] ?: "",
           values["type"] ?: "")
   } catch (ex: Exception){
       return IntentRpcData("", "", "", "", "", "", "", "", "", "", "", "", "")
   }
}


