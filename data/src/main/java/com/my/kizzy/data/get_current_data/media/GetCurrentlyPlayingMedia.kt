/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * GetCurrentlyPlayingMedia.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.get_current_data.media

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaSessionManager
import com.blankj.utilcode.util.AppUtils
import com.my.kizzy.data.rpc.CommonRpc
import com.my.kizzy.data.rpc.RpcImage
import com.my.kizzy.preference.Prefs
import dagger.hilt.android.qualifiers.ApplicationContext
import kizzy.gateway.entities.presence.Timestamps
import javax.inject.Inject

class GetCurrentPlayingMedia @Inject constructor(
    private val metadataResolver: MetadataResolver,
    private val componentName: ComponentName,
    @ApplicationContext private val context: Context
) {
    operator fun invoke(): CommonRpc {
        var largeIcon: RpcImage?
        val smallIcon: RpcImage?
        var timestamps: Timestamps? = null
        val mediaSessionManager =
            context.getSystemService(Service.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val sessions = mediaSessionManager.getActiveSessions(componentName)
        if (sessions.size > 0) {
            val mediaController = sessions[0]
            val metadata = mediaController.metadata
            val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
            val appName = AppUtils.getAppName(mediaController.packageName)
            val author =
                if (Prefs[Prefs.MEDIA_RPC_ARTIST_NAME, false])
                metadata?.let { metadataResolver.getArtistOrAuthor(it) }
                else null
            val bitmap = metadata?.let { metadataResolver.getCoverArt(it) }
            val duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION)
            duration?.let {
                if (it != 0L) timestamps = Timestamps(
                    end = System.currentTimeMillis() + duration, start = System.currentTimeMillis()
                )
            }
            if (title != null) {
                largeIcon =
                    if (Prefs[Prefs.MEDIA_RPC_APP_ICON, false]) RpcImage.ApplicationIcon(
                        mediaController.packageName, context
                    ) else null
                if (bitmap != null) {
                    smallIcon = largeIcon
                    largeIcon = RpcImage.BitmapImage(
                        context = context,
                        bitmap = bitmap,
                        packageName = mediaController.packageName,
                        title = title
                    )
                } else smallIcon = null
                return CommonRpc(name = appName,
                    details = title,
                    state = author,
                    largeImage = largeIcon,
                    smallImage = smallIcon,
                    packageName = "$title::${mediaController.packageName}",
                    time = timestamps.takeIf { it != null })
            }
        }
        return CommonRpc()
    }
}