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
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Build
import android.os.IBinder
import com.my.kizzy.data.get_current_data.app.GetCurrentlyRunningApp
import com.my.kizzy.data.get_current_data.media.GetCurrentPlayingMediaAll
import com.my.kizzy.data.get_current_data.media.RichMediaMetadata
import com.my.kizzy.data.rpc.CommonRpc
import com.my.kizzy.data.rpc.KizzyRPC
import com.my.kizzy.data.rpc.RpcImage
import com.my.kizzy.data.rpc.TemplateKeys
import com.my.kizzy.data.rpc.TemplateProcessor
import com.my.kizzy.data.rpc.Timestamps
import com.my.kizzy.domain.interfaces.Logger
import com.my.kizzy.domain.model.rpc.RpcButtons
import com.my.kizzy.feature_rpc_base.Constants
import com.my.kizzy.feature_rpc_base.setLargeIcon
import com.my.kizzy.preference.Prefs
import com.my.kizzy.resources.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.inject.Inject

private const val TAG = "ExperimentalRPC"

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ExperimentalRpc : Service() {

    @Inject
    lateinit var scope: CoroutineScope

    @Inject
    lateinit var kizzyRPC: KizzyRPC

    @Inject
    lateinit var getCurrentPlayingMediaAll: GetCurrentPlayingMediaAll

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
    private val mediaControllerCallback = MediaControllerCallback()

    private var isMediaSessionActive = false

    private var useAppsRpc = Prefs[Prefs.EXPERIMENTAL_RPC_USE_APPS_RPC, true]
    private var useMediaRpc = Prefs[Prefs.EXPERIMENTAL_RPC_USE_MEDIA_RPC, true]

    private var templateName =
        Prefs[Prefs.EXPERIMENTAL_RPC_TEMPLATE_NAME, TemplateKeys.APP_NAME]
    private var templateDetails =
        Prefs[Prefs.EXPERIMENTAL_RPC_TEMPLATE_DETAILS, TemplateKeys.MEDIA_TITLE]
    private var templateState =
        Prefs[Prefs.EXPERIMENTAL_RPC_TEMPLATE_STATE, TemplateKeys.MEDIA_ARTIST]

    private var appActivityTypes: Map<String, Int> = Prefs.getAppActivityTypes()
    private var enabledExperimentalApps: List<String> = try {
        Json.decodeFromString(Prefs[Prefs.ENABLED_EXPERIMENTAL_APPS, "[]"])
    } catch (_: Exception) {
        emptyList()
    }

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
            val restartIntent = Intent(this, ExperimentalRpc::class.java)
            restartIntent.action = Constants.ACTION_RESTART_SERVICE
            val restartPendingIntent: PendingIntent = PendingIntent.getService(
                this, 0, restartIntent, PendingIntent.FLAG_IMMUTABLE
            )

            val notification = notificationBuilder
                .setSmallIcon(R.drawable.ic_dev_rpc)
                .setContentTitle(getString(R.string.service_enabled))
                .addAction(
                    R.drawable.ic_dev_rpc,
                    getString(R.string.restart),
                    restartPendingIntent
                )
                .addAction(R.drawable.ic_dev_rpc, getString(R.string.exit), pendingIntent)
                .build()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                startForeground(Constants.NOTIFICATION_ID, notification)
            } else {
                startForeground(Constants.NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
            }


            mediaSessionManager = getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
            mediaSessionManager.addOnActiveSessionsChangedListener(
                ::activeSessionsListener,
                ComponentName(this, NotificationListener::class.java)
            )

            // Always reload settings on start
            templateName = Prefs[Prefs.EXPERIMENTAL_RPC_TEMPLATE_NAME, TemplateKeys.APP_NAME]
            templateDetails = Prefs[Prefs.EXPERIMENTAL_RPC_TEMPLATE_DETAILS, TemplateKeys.MEDIA_TITLE]
            templateState = Prefs[Prefs.EXPERIMENTAL_RPC_TEMPLATE_STATE, TemplateKeys.MEDIA_ARTIST]
            useAppsRpc = Prefs[Prefs.EXPERIMENTAL_RPC_USE_APPS_RPC, true]
            useMediaRpc = Prefs[Prefs.EXPERIMENTAL_RPC_USE_MEDIA_RPC, true]
            appActivityTypes = Prefs.getAppActivityTypes()
            enabledExperimentalApps = try {
                Json.decodeFromString(Prefs[Prefs.ENABLED_EXPERIMENTAL_APPS, "[]"])
            } catch (_: Exception) {
                emptyList()
            }

            val initialMediaSessions = mediaSessionManager.getActiveSessions(
                ComponentName(this, NotificationListener::class.java)
            )
            var mediaActiveInitially = false
            if (useMediaRpc && initialMediaSessions.isNotEmpty()) {
                val firstActiveMediaController = initialMediaSessions.firstOrNull {
                    enabledExperimentalApps.contains(it.packageName)
                }
                if (firstActiveMediaController != null) {
                    mediaActiveInitially = true
                    activeSessionsListener(listOf(firstActiveMediaController), false)
                }
            }

            if (!mediaActiveInitially) {
                if (useAppsRpc) {
                    startAppDetectionCoroutine()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startAppDetectionCoroutine() {
        logger.d(TAG, "Starting app detection coroutine")

        var currentPackageName = ""
        val startTimestamps = Timestamps(start = System.currentTimeMillis())

        scope.launch {
            while (isActive) {
                val currentApp = getCurrentlyRunningApp()

                if (
                    currentApp.name.isNotEmpty() &&
                    currentApp.packageName != currentPackageName &&
                    enabledExperimentalApps.contains(currentApp.packageName)
                ) {
                    currentPackageName = currentApp.packageName
                    updatePresence(appInfo = currentApp.copy(time = startTimestamps))
                } else if (currentApp.name.isNotEmpty() && currentApp.packageName != currentPackageName) {
                    currentPackageName = ""
                    if (!isMediaSessionActive || !useMediaRpc) {
                        updatePresence(CommonRpc())
                    }
                }
                delay(5000)
            }
        }
    }

    private suspend fun updatePresence(
        appInfo: CommonRpc? = null,
        richMediaInfo: RichMediaMetadata? = null,
        rawMediaMetadata: MediaMetadata? = null,
    ) {
        val rpcButtonsString = Prefs[Prefs.RPC_BUTTONS_DATA, "{}"]
        val rpcButtons = Json.decodeFromString<RpcButtons>(rpcButtonsString)

        val finalName: String?
        val finalDetails: String?
        val finalState: String?
        var finalLargeImage: RpcImage?
        var finalSmallImage: RpcImage?
        var finalLargeText: String?
        var finalSmallText: String?
        var finalTimestamps: Timestamps?
        var effectivePackageName: String?

        // Hide media on pause if enabled
        if (richMediaInfo != null &&
            Prefs[Prefs.EXPERIMENTAL_RPC_HIDE_ON_PAUSE, false] &&
            (richMediaInfo.playbackState == PlaybackState.STATE_PAUSED ||
                    richMediaInfo.playbackState == PlaybackState.STATE_STOPPED)
        ) {
            if (useAppsRpc) {
                startAppDetectionCoroutine()
            } else if (kizzyRPC.isRpcRunning()) {
                kizzyRPC.closeRPC()
                notificationManager.notify(
                    Constants.NOTIFICATION_ID, notificationBuilder
                        .setContentTitle(getString(R.string.service_enabled))
                        .setContentText(getString(R.string.idling_notification))
                        .build()
                )
            }
            return
        }

        val currentContextIsMedia =
            useMediaRpc && richMediaInfo != null && richMediaInfo.appName != null
        val currentContextIsApp = useAppsRpc && appInfo != null && appInfo.name.isNotEmpty()

        val processor = TemplateProcessor(
            mediaMetadata = rawMediaMetadata,
            mediaPlayerAppName = richMediaInfo?.appName,
            mediaPlayerPackageName = richMediaInfo?.packageName,
            detectedAppInfo = appInfo
        )

        if (currentContextIsMedia) {
            effectivePackageName = richMediaInfo?.packageName

            logger.d(TAG, "Processing Rich Media Context")
            finalName = processor.process(templateName) ?: richMediaInfo?.appName
            finalDetails = processor.process(templateDetails) ?: richMediaInfo?.title
            finalState = processor.process(templateState) ?: richMediaInfo?.artist

            finalLargeImage = when {
                Prefs[Prefs.EXPERIMENTAL_RPC_SHOW_COVER_ART, true] -> richMediaInfo?.coverArt
                Prefs[Prefs.EXPERIMENTAL_RPC_SHOW_APP_ICON, false] -> richMediaInfo?.appIcon
                else -> null
            }

            finalSmallImage = when {
                Prefs[Prefs.EXPERIMENTAL_RPC_SHOW_PLAYBACK_STATE, true] ->
                    richMediaInfo?.playbackStateIcon

                Prefs[Prefs.EXPERIMENTAL_RPC_SHOW_APP_ICON, false] && finalLargeImage != richMediaInfo?.appIcon ->
                    richMediaInfo?.appIcon

                else -> null
            }

            finalLargeText = richMediaInfo?.album
            finalSmallText =
                if (finalSmallImage == richMediaInfo?.appIcon) richMediaInfo?.appName else null

            finalTimestamps = if (Prefs[Prefs.EXPERIMENTAL_RPC_ENABLE_TIMESTAMPS, true])
                richMediaInfo?.timestamps else null

        } else if (currentContextIsApp) {
            effectivePackageName = appInfo?.packageName
            logger.d(TAG, "Processing App Context")
            finalName = processor.process(templateName) ?: appInfo?.name
            finalDetails = processor.process(templateDetails) ?: appInfo?.details
            finalState = processor.process(templateState) ?: appInfo?.state

            finalLargeImage = appInfo?.largeImage
            finalSmallImage = appInfo?.smallImage
            finalLargeText = appInfo?.largeText
            finalSmallText = appInfo?.smallText
            finalTimestamps =
                if (Prefs[Prefs.EXPERIMENTAL_RPC_ENABLE_TIMESTAMPS, true]) appInfo?.time else null
        } else {
            logger.d(TAG, "No active context (App or Media) or both disabled.")
            if (kizzyRPC.isRpcRunning()) {
                kizzyRPC.closeRPC()
            }
            return
        }

        val rpcDataIsEmpty =
            finalName.isNullOrEmpty() && finalDetails.isNullOrEmpty() && finalState.isNullOrEmpty()

        if (kizzyRPC.isRpcRunning()) {
            if (rpcDataIsEmpty) {
                logger.d(TAG, "Calculated RPC data is empty, stopping RPC.")
                kizzyRPC.closeRPC()
                notificationManager.notify(
                    Constants.NOTIFICATION_ID, notificationBuilder
                        .setContentTitle(getString(R.string.service_enabled))
                        .setContentText(getString(R.string.idling_notification))
                        .build()
                )
                return
            }

            kizzyRPC.updateRPC(
                commonRpc = CommonRpc(
                    name = finalName ?: "",
                    type = appActivityTypes[effectivePackageName] ?: 0,
                    details = finalDetails,
                    state = finalState,
                    largeImage = finalLargeImage,
                    smallImage = finalSmallImage,
                    largeText = finalLargeText,
                    smallText = finalSmallText,
                    time = finalTimestamps,
                    packageName = effectivePackageName ?: ""
                ),
                enableTimestamps = Prefs[Prefs.EXPERIMENTAL_RPC_ENABLE_TIMESTAMPS, true]
            )

        } else {
            if (rpcDataIsEmpty) {
                logger.d(TAG, "Calculated RPC data is empty, not starting RPC.")

                notificationManager.notify(
                    Constants.NOTIFICATION_ID, notificationBuilder
                        .setContentTitle(getString(R.string.service_enabled))
                        .setContentText(getString(R.string.idling_notification))
                        .build()
                )
                return
            }

            kizzyRPC.apply {
                setName(finalName)
                setType(appActivityTypes[effectivePackageName] ?: 0)
                setStatus(Prefs[Prefs.CUSTOM_ACTIVITY_STATUS, "dnd"])
                setDetails(finalDetails)
                setState(finalState)
                setStartTimestamps(finalTimestamps?.start)
                setStopTimestamps(finalTimestamps?.end)
                setLargeImage(finalLargeImage, finalLargeText)
                setSmallImage(finalSmallImage, finalSmallText)
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
        }

        val notifTitle = finalName.takeIf { !it.isNullOrEmpty() } ?: getString(R.string.app_name)
        val notifText = finalDetails ?: finalState

        notificationManager.notify(
            Constants.NOTIFICATION_ID, notificationBuilder
                .setContentTitle(notifTitle)
                .setContentText(notifText)
                .setLargeIcon(rpcImage = finalLargeImage, context = this@ExperimentalRpc)
                .build()
        )
    }

    private fun activeSessionsListener(
        mediaSessions: List<MediaController>?,
        isEvent: Boolean = true,
    ) {
        if (!useMediaRpc) {
            logger.i(TAG, "Media part of Experimental RPC is disabled.")
            if (useAppsRpc && !isMediaSessionActive) {
                scope.coroutineContext.cancelChildren()
                startAppDetectionCoroutine()
            } else if (!useAppsRpc) {
                scope.launch {
                    updatePresence(
                        appInfo = null,
                        richMediaInfo = null,
                        rawMediaMetadata = null
                    )
                }
            }
            return
        }
        logger.d(TAG, "Active media sessions changed")
        if (isEvent) runBlocking { delay(1500) }

        currentMediaController?.unregisterCallback(mediaControllerCallback)
        currentMediaController = null

        if (mediaSessions?.isNotEmpty() == true) {
            currentMediaController = mediaSessions.firstOrNull {
                enabledExperimentalApps.contains(it.packageName)
            }
            currentMediaController?.registerCallback(mediaControllerCallback)
        }

        scope.coroutineContext.cancelChildren()
        scope.launch {
            val richMediaData = getCurrentPlayingMediaAll()
            isMediaSessionActive = richMediaData.appName != null && currentMediaController != null

            if (isMediaSessionActive) {
                updatePresence(
                    richMediaInfo = richMediaData,
                    rawMediaMetadata = currentMediaController?.metadata
                )
            } else {
                if (useAppsRpc) {
                    startAppDetectionCoroutine()
                } else {
                    updatePresence(appInfo = null, richMediaInfo = null, rawMediaMetadata = null)
                }
            }
        }
    }

    private inner class MediaControllerCallback : MediaController.Callback() {
        private fun handleMediaUpdate() {
            if (!useMediaRpc) return

            scope.coroutineContext.cancelChildren()
            scope.launch {
                delay(1000)
                val richMediaData = getCurrentPlayingMediaAll()
                isMediaSessionActive =
                    richMediaData.appName != null && currentMediaController != null

                if (isMediaSessionActive) {
                    updatePresence(
                        richMediaInfo = richMediaData,
                        rawMediaMetadata = currentMediaController?.metadata
                    )
                } else {
                    if (useAppsRpc) {
                        startAppDetectionCoroutine()
                    } else {
                        updatePresence(
                            appInfo = null,
                            richMediaInfo = null,
                            rawMediaMetadata = null
                        )
                    }
                }
            }
        }

        override fun onPlaybackStateChanged(state: PlaybackState?) {
            super.onPlaybackStateChanged(state)
            logger.d(TAG, "MediaControllerCallback: onPlaybackStateChanged")
            handleMediaUpdate()
        }

        override fun onMetadataChanged(metadata: MediaMetadata?) {
            super.onMetadataChanged(metadata)
            logger.d(TAG, "MediaControllerCallback: onMetadataChanged")
            handleMediaUpdate()
        }

        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            logger.d(TAG, "MediaControllerCallback: onSessionDestroyed")
            currentMediaController?.unregisterCallback(this)
            currentMediaController = null
            isMediaSessionActive = false

            scope.coroutineContext.cancelChildren()
            scope.launch {
                if (useAppsRpc) {
                    startAppDetectionCoroutine()
                } else {
                    updatePresence(appInfo = null, richMediaInfo = null, rawMediaMetadata = null)
                }
            }
        }
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