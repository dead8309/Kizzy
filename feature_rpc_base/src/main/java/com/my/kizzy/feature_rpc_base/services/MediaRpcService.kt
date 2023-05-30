/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * MediaRpcService.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_rpc_base.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import com.my.kizzy.data.get_current_data.media.GetCurrentPlayingMedia
import com.my.kizzy.data.rpc.Constants
import com.my.kizzy.data.rpc.KizzyRPC
import com.my.kizzy.domain.interfaces.Logger
import com.my.kizzy.domain.model.rpc.RpcButtons
import com.my.kizzy.preference.Prefs
import com.my.kizzy.preference.Prefs.MEDIA_RPC_ENABLE_TIMESTAMPS
import com.my.kizzy.preference.Prefs.TOKEN
import com.my.kizzy.resources.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@AndroidEntryPoint
class MediaRpcService : Service() {

    @Inject
    lateinit var kizzyRPC: KizzyRPC

    @Inject
    lateinit var scope: CoroutineScope

    @Inject
    lateinit var getCurrentPlayingMedia: GetCurrentPlayingMedia

    @Inject
    lateinit var logger: Logger

    private var wakeLock: WakeLock? = null

    @SuppressLint("WakelockTimeout")
    override fun onCreate() {
        super.onCreate()
        val token = Prefs[TOKEN, ""]
        if (token.isEmpty()) stopSelf()
        // TODO add time left later
        val time = System.currentTimeMillis()
        setupWakeLock()
        startForeground(NOTIFICATION_ID, getNotification())
        scope.launch {
            while (isActive) {
                val enableTimestamps = Prefs[MEDIA_RPC_ENABLE_TIMESTAMPS, false]
                val playingMedia = getCurrentPlayingMedia()
                getNotificationManager()?.notify(
                    NOTIFICATION_ID,
                    getNotification(playingMedia.details?:"")
                )
                val rpcButtonsString = Prefs[Prefs.RPC_BUTTONS_DATA, "{}"]
                val rpcButtons = Json.decodeFromString<RpcButtons>(rpcButtonsString)
                when (kizzyRPC.isRpcRunning()) {
                    true -> {
                        logger.d("MediaRPC", "Updating Rpc")
                        kizzyRPC.updateRPC(
                            name = playingMedia.name.ifEmpty { "YouTube" },
                            details = playingMedia.details,
                            state = playingMedia.state,
                            large_image = playingMedia.largeImage,
                            small_image = playingMedia.smallImage,
                            enableTimestamps = enableTimestamps,
                            time = time
                        )
                    }
                    false -> {
                        kizzyRPC.apply {
                            setName(playingMedia.name.ifEmpty { "YouTube" })
                            setDetails(playingMedia.details)
                            setStatus(Constants.DND)
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
                }
                delay(5000)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun getNotification(notificationTitle: String = "Browsing Home Page.."): Notification {
        getNotificationManager().apply {
            this?.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Background Service",
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
        val builder = Notification.Builder(this, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_media_rpc)
        val intent = Intent(this, MediaRpcService::class.java)
        intent.action = ACTION_STOP_SERVICE
        val pendingIntent = PendingIntent.getService(
            this,
            0, intent, PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(R.drawable.ic_media_rpc, "Exit", pendingIntent)
        builder.setContentText(notificationTitle.ifEmpty { "Browsing Home Page.." })
        return builder.build()
    }

    @SuppressLint("WakelockTimeout")
    private fun setupWakeLock() {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "kizzy:MediaRPC")
        wakeLock?.acquire()
    }

    private fun getNotificationManager(): NotificationManager? {
        return getSystemService(
            NotificationManager::class.java
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            it.action?.let { ac ->
                if (ac == ACTION_STOP_SERVICE)
                    stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        scope.cancel()
        kizzyRPC.closeRPC()
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        const val CHANNEL_ID = "MediaRPC"
        const val ACTION_STOP_SERVICE = "STOP_RPC"
        const val NOTIFICATION_ID = 8838
    }
}
