/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * utils.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_custom_rpc.components.sheet

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import com.blankj.utilcode.util.FileIOUtils
import com.google.gson.GsonBuilder
import com.my.kizzy.data.rpc.Constants
import com.my.kizzy.data.utils.getFileName
import com.my.kizzy.domain.model.RpcConfig
import com.my.kizzy.preference.Prefs
import java.io.File
import java.io.FilenameFilter

internal val gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()
internal val FILE_FILTER = FilenameFilter { _: File?, f: String ->
    f.endsWith(".json")
}

internal fun Context.dir() =
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU)
        File(this.filesDir, "Configs")
    else {
        val selected = Prefs[Prefs.CONFIGS_DIRECTORY, Constants.DOWNLOADS_DIRECTORY]
        if (selected == Constants.DOWNLOADS_DIRECTORY)
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "Kizzy")
        else
            File(this.filesDir, "Configs")
    }

internal fun Context.handleUriResult(uri: Uri?, onSuccess: (json: String) -> Unit) {
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

fun RpcConfig.dataToString(): String {
    return gson.toJson(this)
}

fun String.stringToData(): RpcConfig {
    return try {
        var config = gson.fromJson(this, RpcConfig::class.java)
        //Gson will set value of url as null if its not present in json
        if (config.url == null)
            config = config.copy(url = "")
        return config
    } catch (ex: Exception) {
        RpcConfig()
    }
}