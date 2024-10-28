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
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.my.kizzy.data.rpc.RpcImage
import com.my.kizzy.data.utils.getAppInfo
import com.my.kizzy.data.utils.toBitmap
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal suspend fun Notification.Builder.setLargeIcon(
    rpcImage: RpcImage?,
    context: Context
): Notification.Builder = suspendCancellableCoroutine { continuation ->
    val customTarget = object : CustomTarget<Bitmap>() {
        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
            continuation.resume(this@setLargeIcon.setLargeIcon(resource))
        }

        override fun onLoadCleared(placeholder: Drawable?) {}
        override fun onLoadFailed(errorDrawable: Drawable?) {
            continuation.resume(this@setLargeIcon)
        }
    }
    if (rpcImage == null) {
        continuation.resume(this)
    } else {
        Glide.with(context)
            .asBitmap()
            .loadRpcImage(rpcImage, context)
            .fitCenter()
            .into(customTarget)
    }

    continuation.invokeOnCancellation {
        Glide.with(context).clear(customTarget)
    }
}

private fun <TranscodeType> RequestBuilder<TranscodeType>.loadRpcImage(
    rpcImage: RpcImage,
    context: Context
): RequestBuilder<TranscodeType> {
    return when (rpcImage) {
        is RpcImage.ApplicationIcon -> load(
            context.getAppInfo(rpcImage.packageName).toBitmap(context)
        )

        is RpcImage.BitmapImage -> load(rpcImage.bitmap)
        is RpcImage.DiscordImage -> load("https://cdn.discordapp.com/${rpcImage.image}")
        is RpcImage.ExternalImage -> load(rpcImage.image)
    }
}