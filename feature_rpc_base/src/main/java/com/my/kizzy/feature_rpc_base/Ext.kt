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

package com.my.kizzy.feature_rpc_base

import android.app.Notification
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import com.my.kizzy.data.rpc.RpcImage
import com.my.kizzy.data.utils.getAppInfo
import com.my.kizzy.data.utils.toBitmap
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal suspend fun Notification.Builder.setLargeIcon(
    rpcImage: RpcImage?,
    context: Context,
): Notification.Builder = suspendCancellableCoroutine { continuation ->
    if (rpcImage == null) {
        continuation.resume(this)
        return@suspendCancellableCoroutine
    }

    val data = when (rpcImage) {
        is RpcImage.ApplicationIcon -> context.getAppInfo(rpcImage.packageName).toBitmap(context)
        is RpcImage.BitmapImage -> rpcImage.bitmap
        is RpcImage.DiscordImage -> "https://cdn.discordapp.com/${rpcImage.image}"
        is RpcImage.ExternalImage -> rpcImage.image
    }

    val imageLoader = coil.ImageLoader(context)
    val request = coil.request.ImageRequest.Builder(context)
        .data(data)
        .target(
            onSuccess = { drawable ->
                val bitmap = when (drawable) {
                    is BitmapDrawable -> drawable.bitmap
                    else -> createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight).apply {
                        val canvas = android.graphics.Canvas(this)
                        drawable.setBounds(0, 0, canvas.width, canvas.height)
                        drawable.draw(canvas)
                    }
                }

                val maxSize = 256
                val scaledBitmap = if (bitmap.width > maxSize || bitmap.height > maxSize) {
                    val scale = maxSize.toFloat() / maxOf(bitmap.width, bitmap.height)
                    bitmap.scale(
                        width = (bitmap.width * scale).toInt(),
                        height = (bitmap.height * scale).toInt()
                    )
                } else bitmap

                val optimizedBitmap = if (scaledBitmap.config != Bitmap.Config.ARGB_8888) {
                    scaledBitmap.copy(Bitmap.Config.ARGB_8888, false)
                } else scaledBitmap

                continuation.resume(this@setLargeIcon.setLargeIcon(optimizedBitmap))
            },
            onError = {
                continuation.resume(this@setLargeIcon)
            }
        )
        .build()

    val disposable = imageLoader.enqueue(request)

    continuation.invokeOnCancellation {
        disposable.dispose()
    }
}