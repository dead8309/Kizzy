/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Ext.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.utils

import android.content.ContentResolver
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.util.DisplayMetrics
import android.webkit.MimeTypeMap
import com.blankj.utilcode.util.AppUtils
import com.my.kizzy.data.remote.ApiResponse
import com.my.kizzy.data.rpc.RpcImage
import com.my.kizzy.preference.Prefs
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import java.io.File
import java.io.FileOutputStream

suspend fun HttpResponse.toImageAsset(): String? {
    return try {
        if (this.status == HttpStatusCode.OK)
            this.body<ApiResponse>().id
        else
            null
    } catch (e: Exception) {
        null
    }
}

/**
 * Converts Bitmap to file
 * @param context Context
 * @param outputPathFolder Folder name for storing the png file eg (images, media, custom)
 */
fun Bitmap?.toFile(context: Context, outputPathFolder: String): File {
    val dir = File(context.filesDir.toString() + File.separator + outputPathFolder)
    dir.mkdirs()
    val image = File(dir, "Temp.png")
    FileOutputStream(image).use {
        this?.compress(Bitmap.CompressFormat.PNG, 100, it)
    }
    return image
}

@Suppress("DEPRECATION")
fun Context.getAppInfo(packageName: String): ApplicationInfo {
    return this.packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
}

@Suppress("DEPRECATION")
fun ApplicationInfo.toBitmap(context: Context): Bitmap? {
    val res = context.packageManager.getResourcesForApplication(this)
    val icon = if (Prefs[Prefs.RPC_USE_LOW_RES_ICON, false])
        AppUtils.getAppIcon(this.packageName)
    else res.getDrawableForDensity(
        this.icon,
        DisplayMetrics.DENSITY_XXXHIGH
    )

    val bitmap = icon?.let {
        Bitmap.createBitmap(
            it.intrinsicWidth,
            icon.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
    }
    val canvas = bitmap?.let { Canvas(it) }
    if (icon != null) {
        if (canvas != null) {
            icon.setBounds(0, 0, canvas.width, canvas.height)
            icon.draw(canvas)
        }
    }
    return bitmap
}

fun String.toRpcImage(): RpcImage {
    return if (this.startsWith("attachments"))
        RpcImage.DiscordImage(this)
    else
        RpcImage.ExternalImage(this)
}

fun Context.getFileName(uri: Uri): String = "temp_file.${getFileExtension(this, uri)}"

private fun getFileExtension(context: Context, uri: Uri): String? =
    if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
        MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri))
    } else {
        uri.path?.let { MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(it)).toString()) }
    }

fun Context.uriToFile(uri: Uri): File {
    val file = File(cacheDir,getFileName(uri))
    val inputStream = contentResolver.openInputStream(uri)
    inputStream?.use { input ->
        file.outputStream().use { out ->
            input.copyTo(out)
        }
    }
    return file
}