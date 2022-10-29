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

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.DisplayMetrics
import com.blankj.utilcode.util.AppUtils
import com.my.kizzy.model.ApiResponse
import com.my.kizzy.rpc.RpcImage
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

fun Response<ApiResponse>.toImageAsset(): String?{
    return try {
        if (this.isSuccessful)
            this.body()?.id
        else
            null
    } catch (e: Exception){
        null
    }
}

fun Bitmap?.toFile(context: Context,path: String): File {
    val dir = File(context.filesDir.toString() + File.separator + path)
    dir.mkdirs()
    val image = File(dir, "Temp.png")
    FileOutputStream(image).use {
        this?.compress(Bitmap.CompressFormat.PNG, 100, it)
    }
    return image
}

fun Context.getAppInfo(packageName: String): ApplicationInfo{
    return this.packageManager.getApplicationInfo(packageName,PackageManager.GET_META_DATA)
}

fun ApplicationInfo.toBitmap(context: Context): Bitmap?{
    val res = context.packageManager.getResourcesForApplication(this)
    val icon = if (Prefs[Prefs.RPC_USE_LOW_RES_ICON, false])
        AppUtils.getAppIcon(this.packageName)
    else res.getDrawableForDensity(
        this.icon,
        DisplayMetrics.DENSITY_XXXHIGH)

    val bitmap = icon?.let {
        Bitmap.createBitmap(it.intrinsicWidth,
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


fun String.toRpcImage(): RpcImage{
    return if (this.startsWith("attachments"))
            RpcImage.DiscordImage(this)
        else
            RpcImage.ExternalImage(this)
}