/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ExperimentalRpc.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_rpc_base.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.IBinder
import com.my.kizzy.data.get_current_data.app.GetCurrentlyRunningApp
import com.my.kizzy.data.get_current_data.media.GetCurrentPlayingMedia
import com.my.kizzy.data.rpc.CommonRpc
import com.my.kizzy.data.rpc.KizzyRPC
import com.my.kizzy.data.rpc.Timestamps
import com.my.kizzy.domain.interfaces.Logger
import com.my.kizzy.domain.model.rpc.RpcButtons
import com.my.kizzy.feature_rpc_base.Constants
import com.my.kizzy.feature_rpc_base.setLargeIcon
import com.my.kizzy.preference.Prefs
import com.my.kizzy.preference.Prefs.MEDIA_RPC_ENABLE_TIMESTAMPS
import com.my.kizzy.resources.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ExperimentalRpc : Service() {

    @Inject
    lateinit var scope: CoroutineScope

    @Inject
    lateinit var kizzyRPC: KizzyRPC

    @Inject
    lateinit var getCurrentPlayingMedia: GetCurrentPlayingMedia

    @Inject
    lateinit var getCurrentlyRunningApp: GetCurrentlyRunningApp

    @Inject
    lateinit var logger: Logger

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: Notification.Builder

    private lateinit var mediaSessionManager: MediaSessionManager

    private var currentMediaController: MediaController? = null

    private var isMediaSessionActive = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(Constants.ACTION_STOP_SERVICE)) stopSelf()
        else if (intent?.action.equals(Constants.ACTION_RESTART_SERVICE)) {
            stopSelf()
            startService(Intent(this, ExperimentalRpc::class.java))
        } else {
            val stopIntent = Intent(this, ExperimentalRpc::class.java)
            stopIntent.action = Constants.ACTION_STOP_SERVICE
            val pendingIntent: PendingIntent = PendingIntent.getService(
                this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE
            )
            val restartIntent: Intent = Intent(this, ExperimentalRpc::class.java)
            restartIntent.action = Constants.ACTION_RESTART_SERVICE
            val restartPendingIntent: PendingIntent = PendingIntent.getService(
                this, 0, restartIntent, PendingIntent.FLAG_IMMUTABLE
            )

            startForeground(
                Constants.NOTIFICATION_ID,
                notificationBuilder
                    .setSmallIcon(R.drawable.ic_dev_rpc)
                    .setContentTitle(getString(R.string.service_enabled))
                    .addAction(
                        R.drawable.ic_dev_rpc,
                        getString(R.string.restart),
                        restartPendingIntent
                    )
                    .addAction(R.drawable.ic_dev_rpc, getString(R.string.exit), pendingIntent)
                    .build()
            )


            mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
            mediaSessionManager.addOnActiveSessionsChangedListener(
                ::activeSessionsListener,
                ComponentName(this, NotificationListener::class.java)
            )

            // Register first media session
            activeSessionsListener(
                mediaSessionManager.getActiveSessions(
                    ComponentName(
                        this,
                        NotificationListener::class.java
                    )
                ), false
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startAppDetectionCoroutine() {
        logger.d(TAG, "Starting app detection coroutine")

        // Socket needs to be closed before starting new RPC for whatever reason
        kizzyRPC.closeRPC()
        var currentPackageName = ""

        scope.launch {
            while (isActive) {
                val getCurrentApp = getCurrentlyRunningApp()
                if (getCurrentApp.name.isNotEmpty() && getCurrentApp.packageName != currentPackageName) {
                    currentPackageName = getCurrentApp.packageName
                    updatePresence(getCurrentApp.copy(time = Timestamps(start = System.currentTimeMillis())))
                } else if (getCurrentApp.name.isEmpty()) {
                    currentPackageName = ""
                    // updatePresence(CommonRpc())
                }
                delay(5000)
            }
        }
    }

    private suspend fun updatePresence(collectedData: CommonRpc) {
        val rpcButtonsString = Prefs[Prefs.RPC_BUTTONS_DATA, "{}"]
        val rpcButtons = Json.decodeFromString<RpcButtons>(rpcButtonsString)

        if (kizzyRPC.isRpcRunning()) {
            kizzyRPC.updateRPC(collectedData)
            if (collectedData.name.isEmpty()) {
                logger.d(TAG, "Updating RPC with empty data, stopping RPC")
                kizzyRPC.closeRPC()
                notificationManager.notify(
                    Constants.NOTIFICATION_ID, notificationBuilder
                        .setContentTitle(getString(R.string.service_enabled))
                        .setContentText("")
                        .build()
                )
                return
            }
        } else kizzyRPC.apply {
            if (collectedData.name.isEmpty()) {
                logger.d(TAG, "Skipping RPC update with empty data")
                return
            }
            setName(collectedData.name)
            setType(Prefs[Prefs.CUSTOM_ACTIVITY_TYPE, 0])
            setStatus(Prefs[Prefs.CUSTOM_ACTIVITY_STATUS, "dnd"])
            setDetails(collectedData.details)
            setState(collectedData.state)
            setStartTimestamps(if (Prefs[MEDIA_RPC_ENABLE_TIMESTAMPS, false]) collectedData.time?.start else null)
            setStopTimestamps(if (Prefs[MEDIA_RPC_ENABLE_TIMESTAMPS, false]) collectedData.time?.end else null)
            setLargeImage(collectedData.largeImage, collectedData.largeText)
            setSmallImage(collectedData.smallImage, collectedData.smallText)
            if (Prefs[Prefs.USE_RPC_BUTTONS, false]) {
                with(rpcButtons) {
                    setButton1(button1.takeIf { it.isNotEmpty() })
                    setButton1URL(button1Url.takeIf { it.isNotEmpty() })
                    setButton2(button2.takeIf { it.isNotEmpty() })
                    setButton2URL(button2Url.takeIf { it.isNotEmpty() })
                }
            }
            build()
        }
        notificationManager.notify(
            Constants.NOTIFICATION_ID, notificationBuilder
                .setContentTitle(collectedData.name)
                .setContentText(collectedData.details)
                .setLargeIcon(
                    rpcImage = collectedData.largeImage,
                    context = this@ExperimentalRpc
                )
                .build()
        )
    }

    private val mediaControllerCallback = MediaControllerCallback()
    private fun activeSessionsListener(
        mediaSessions: List<MediaController>?,
        isEvent: Boolean = true,
    ) {
        logger.d(TAG, "Active sessions changed")

        // For some reason, event is occasionally fired before session list is actually updated
        if (isEvent) runBlocking { delay(1500) }

        if (mediaSessions?.isNotEmpty() == true) {
            currentMediaController?.unregisterCallback(mediaControllerCallback)
            currentMediaController = mediaSessionManager.getActiveSessions(
                ComponentName(
                    this,
                    NotificationListener::class.java
                )
            ).firstOrNull()
            currentMediaController?.registerCallback(mediaControllerCallback)
        } else {
            currentMediaController?.unregisterCallback(mediaControllerCallback)
            currentMediaController = null
        }

        scope.coroutineContext.cancelChildren()
        scope.launch {
            val media = getCurrentPlayingMedia()
            isMediaSessionActive = media.name.isNotEmpty()
            if (isMediaSessionActive) {
                updatePresence(media)
            } else {
                startAppDetectionCoroutine()
            }
        }
    }

    private inner class MediaControllerCallback : MediaController.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackState?) {
            super.onPlaybackStateChanged(state)

            // Cancel all previous jobs and start new job to prevent conflict/spam
            scope.coroutineContext.cancelChildren()
            scope.launch {
                delay(1000)
                val media = getCurrentPlayingMedia()
                isMediaSessionActive = media.name.isNotEmpty()
                if (isMediaSessionActive) {
                    updatePresence(media)
                } else {
                    startAppDetectionCoroutine()
                }
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadata?) {
            super.onMetadataChanged(metadata)

            scope.coroutineContext.cancelChildren()
            scope.launch {
                delay(1000)
                val media = getCurrentPlayingMedia()
                isMediaSessionActive = media.name.isNotEmpty()
                if (isMediaSessionActive) {
                    updatePresence(media)
                } else {
                    startAppDetectionCoroutine()
                }
            }
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()

            scope.coroutineContext.cancelChildren()
            scope.launch {
                val media = getCurrentPlayingMedia()
                isMediaSessionActive = media.name.isNotEmpty()
                if (isMediaSessionActive) {
                    updatePresence(media)
                } else {
                    startAppDetectionCoroutine()
                }
            }
        }
    }

    companion object {
        const val TAG = "ExperimentalRPC"
    }

    override fun onDestroy() {
        mediaSessionManager.removeOnActiveSessionsChangedListener(::activeSessionsListener)
        currentMediaController?.unregisterCallback(mediaControllerCallback)
        scope.cancel()
        kizzyRPC.closeRPC()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}