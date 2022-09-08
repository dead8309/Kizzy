package com.my.kizzy.ui.screen.rpc.custom

import android.content.Context
import android.os.Environment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.blankj.utilcode.util.FileIOUtils
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.my.kizzy.R
import com.my.kizzy.ui.common.SingleChoiceItem
import java.io.File
import java.io.FilenameFilter

object Menu {

    var PATH = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
        "Kizzy"
    )
    private val gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()
    private val FILE_FILTER = FilenameFilter { _: File?, f: String -> f.endsWith(".json") }

    init {
        PATH.mkdirs()
    }

    @Composable
    fun LoadConfig(onConfigSelected: (Rpc) -> Unit, context: Context) {
        var show by remember {
            mutableStateOf(true)
        }

        val listfiles = PATH.list(FILE_FILTER)
        if (listfiles != null) {
            for (i in listfiles.indices)
                listfiles[i] = listfiles[i].substring(0, listfiles[i].length - 5)
        }
        if (show) {
            SingleSelectDialog(
                title = stringResource(id = R.string.select_a_config),
                optionsList = listfiles,
                defaultSelected = -1,
                submitButtonText = "Select",
                onDismissRequest = { show = !show },
                onSubmitButtonClick = {
                    val data = FileIOUtils.readFile2String(
                        PATH.toString() + "/"
                                + listfiles!![it]
                                + ".json"
                    )
                    returnConfig(data)?.let { it1 -> onConfigSelected(it1) }

                }
            )
        }
    }
        @Composable
        fun SaveConfig(rpc: Rpc) {
            Box(modifier = Modifier.fillMaxWidth())
        }

        @Composable
        fun DeleteConfig() {

        }
    }

fun returnConfig(s: String): Rpc? {
    val values = Gson().fromJson<Map<String, String>>(
        s,
        object : TypeToken<HashMap<String, String>>() {}.type
    )
    return values["name"]?.let {
        Rpc(
            it,
            values["details"],
            values["state"],
            values["timeatampsStart"]?.toLong(),
            values["timeatampsStop"]?.toLong(),
            values["status"],
            values["button1"],
            values["button2"],
            values["button1link"],
            values["button2link"],
            values["largeImg"],
            values["smallImg"],
            values["type"]?.toInt()
        )
    }
}


@Composable
fun SingleSelectDialog(
    title: String,
    optionsList: Array<String>?,
    defaultSelected: Int,
    submitButtonText: String,
    onSubmitButtonClick: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {

    var selectedOption by remember {
        mutableStateOf(defaultSelected)
    }

    Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
        Surface(
            modifier = Modifier.width(300.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {

                Text(text = title)

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn {
                    optionsList?.size?.let {
                        items(it) { item ->
                            SingleChoiceItem(text = optionsList[item], selected = false) {
                                selectedOption = item
                            }
                        }
                    }
                }
                Button(
                    onClick = {
                        onSubmitButtonClick.invoke(selectedOption)
                        onDismissRequest.invoke()
                    },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = submitButtonText)
                }
            }
        }
    }
}



