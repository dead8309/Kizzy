/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * GetCurrentPlayingMediaAll.kt is part of Kizzy
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
import android.media.session.PlaybackState
import com.blankj.utilcode.util.AppUtils
import com.my.kizzy.data.rpc.Constants.APPLICATION_ID
import com.my.kizzy.data.rpc.RpcImage
import com.my.kizzy.data.rpc.Timestamps
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetCurrentPlayingMediaAll @Inject constructor(
    private val metadataResolver: MetadataResolver,
    private val componentName: ComponentName,
    @ApplicationContext private val context: Context
) {
    object Assets {
        val PLAY = "app-assets/$APPLICATION_ID/1300361266212241430.png"
        val PAUSE = "app-assets/$APPLICATION_ID/1300361619490209802.png"
        val STOP = "app-assets/$APPLICATION_ID/1300361702621188160.png"
    }

    private fun getPlaybackStateIcon(playbackState: Int): RpcImage {
        return when (playbackState) {
            PlaybackState.STATE_PLAYING -> RpcImage.DiscordImage(Assets.PLAY)
            PlaybackState.STATE_PAUSED -> RpcImage.DiscordImage(Assets.PAUSE)
            PlaybackState.STATE_STOPPED -> RpcImage.DiscordImage(Assets.STOP)
            else -> RpcImage.DiscordImage(Assets.PAUSE)
        }
    }

    operator fun invoke(): RichMediaMetadata {
        val mediaSessionManager =
            context.getSystemService(Service.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val sessions = mediaSessionManager.getActiveSessions(componentName)
        for (mediaController in sessions) {
            val metadata = mediaController.metadata
            val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
            val appName = AppUtils.getAppName(mediaController.packageName)
            val author = metadata?.let { metadataResolver.getArtistOrAuthor(it) }
            val album = metadata?.let { metadataResolver.getAlbum(it) }
            val bitmap = metadata?.let { metadataResolver.getCoverArt(it) }
            val duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION)
            val position = mediaController.playbackState?.position
            val playbackState = mediaController.playbackState?.state

            var timestamps: Timestamps? = null
            duration?.let {
                if (it != 0L && playbackState == PlaybackState.STATE_PLAYING) timestamps = Timestamps(
                    end = System.currentTimeMillis() + duration - (position ?: 0L),
                    start = System.currentTimeMillis() - (position ?: 0L)
                )
            }

            if (title != null) {
                val appIcon = RpcImage.ApplicationIcon(
                    mediaController.packageName, context
                )

                var coverArt: RpcImage? = null
                if (bitmap != null) {
                    coverArt = RpcImage.BitmapImage(
                        context = context,
                        bitmap = bitmap,
                        packageName = mediaController.packageName,
                        title = "${metadata.let { metadataResolver.getAlbumArtists(it) }}|${metadata.let { metadataResolver.getAlbum(it) } ?: title}"
                    )
                }

                val playbackStateIcon = getPlaybackStateIcon(playbackState ?: PlaybackState.STATE_PAUSED)

                return RichMediaMetadata(
                    appName = appName,
                    packageName = mediaController.packageName,
                    title = title,
                    artist = author,
                    album = album,
                    coverArt = coverArt,
                    appIcon = appIcon,
                    playbackStateIcon = playbackStateIcon,
                    playbackState = playbackState,
                    duration = duration,
                    position = position,
                    timestamps = timestamps
                )
            }
        }
        return RichMediaMetadata()
    }
}

data class RichMediaMetadata(
    val appName: String? = null,
    val packageName: String? = null,
    val title: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val coverArt: RpcImage? = null,
    val appIcon: RpcImage? = null,
    val playbackStateIcon: RpcImage? = null,
    val playbackState: Int? = null,
    val duration: Long? = null,
    val position: Long? = null,
    val timestamps: Timestamps? = null,
)

