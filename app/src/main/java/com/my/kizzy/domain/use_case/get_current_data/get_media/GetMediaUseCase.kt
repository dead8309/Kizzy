/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * GetMediaUseCase.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.domain.use_case.get_current_data.get_media

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaSessionManager
import com.blankj.utilcode.util.AppUtils
import com.my.kizzy.domain.use_case.get_current_data.SharedRpc
import com.my.kizzy.rpc.RpcImage
import com.my.kizzy.rpc.model.Timestamps
import com.my.kizzy.service.MediaRpcService
import com.my.kizzy.service.NotificationListener
import com.my.kizzy.utils.Prefs

fun getCurrentRunningMedia(context: Context): SharedRpc {
    var largeIcon: RpcImage?
    val smallIcon: RpcImage?
    var timestamps: Timestamps? = null
    val mediaSessionManager =
        context.getSystemService(Service.MEDIA_SESSION_SERVICE) as MediaSessionManager
    val component = ComponentName(context, NotificationListener::class.java)
    val sessions = mediaSessionManager.getActiveSessions(component)
    if (sessions.size > 0) {
        val mediaController = sessions[0]
        val metadata = mediaController.metadata
        val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
        val appName = AppUtils.getAppName(mediaController.packageName)
        val author =  if (Prefs[Prefs.MEDIA_RPC_ARTIST_NAME, false]) metadata?.getString(MediaRpcService.getArtistOrAuthor(metadata)) else null
        val bitmap = metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
        val duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION)
        duration?.let {
            if (it != 0L)
                timestamps = Timestamps(end = System.currentTimeMillis()+duration, start = System.currentTimeMillis())
        }
        if (title != null) {
            largeIcon = if (Prefs[Prefs.MEDIA_RPC_APP_ICON, false]) RpcImage.ApplicationIcon(
                mediaController.packageName, context
            ) else null
            if (bitmap != null){
                smallIcon = largeIcon
                largeIcon = RpcImage.BitmapImage(
                    context = context,
                    bitmap = bitmap,
                    packageName = mediaController.packageName,
                    title = title
                )
            } else smallIcon = null
            return SharedRpc(
                name = appName,
                details = title,
                state = author,
                large_image = largeIcon,
                small_image = smallIcon,
                package_name = "$title::${mediaController.packageName}",
                time = timestamps.takeIf { it != null }
            )
        }
    }
    return SharedRpc()
}