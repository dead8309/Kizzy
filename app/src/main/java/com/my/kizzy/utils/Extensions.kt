/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Extensions.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.utils

import android.app.AppOpsManager
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.gson.Gson
import com.my.kizzy.data.rpc.RpcImage
import com.my.kizzy.domain.model.User
import java.io.File

fun String.toRpcImage(): RpcImage {
    return if (this.startsWith("attachments"))
            RpcImage.DiscordImage(this)
        else
            RpcImage.ExternalImage(this)
}

fun Context.getFileName(uri: Uri): String = "temp_file.${getFileExtension(this,uri)}"

private fun getFileExtension(context: Context, uri: Uri): String? =
    if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))
    } else {
        uri.path?.let { MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(it)).toString()) }
    }

fun Gson.fromJson(value: String): User? {
    return when {
        value.isNotEmpty() -> this.fromJson(value, User::class.java)
        else -> null
    }
}

@Suppress("DEPRECATION")
fun Context.hasUsageAccess(): Boolean {
    return try {
        val packageManager: PackageManager = this.packageManager
        val applicationInfo = packageManager.getApplicationInfo(this.packageName, 0)
        val appOpsManager = this.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            applicationInfo.uid,
            applicationInfo.packageName)
        mode == AppOpsManager.MODE_ALLOWED
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}