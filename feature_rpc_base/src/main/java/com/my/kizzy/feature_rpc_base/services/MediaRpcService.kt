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
import com.my.kizzy.data.rpc.KizzyRPC
import com.my.kizzy.domain.interfaces.Logger
import com.my.kizzy.domain.model.rpc.RpcButtons
import com.my.kizzy.feature_rpc_base.Constants
import com.my.kizzy.feature_rpc_base.setLargeIcon
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

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: Notification.Builder

    @SuppressLint("WakelockTimeout")
    override fun onCreate() {
        super.onCreate()
        val token = Prefs[TOKEN, ""]
        if (token.isEmpty()) stopSelf()
        // TODO add time left later
        val time = System.currentTimeMillis()
        setupWakeLock()
        val intent = Intent(this, MediaRpcService::class.java)
        intent.action = Constants.ACTION_STOP_SERVICE
        val pendingIntent = PendingIntent.getService(
            this,
            0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        startForeground(
            Constants.NOTIFICATION_ID, notificationBuilder
                .setSmallIcon(R.drawable.ic_media_rpc)
                .addAction(R.drawable.ic_media_rpc, "Exit", pendingIntent)
                .setContentText("Browsing Home Page..")
                .build()
        )

        scope.launch {
            while (isActive) {
                val enableTimestamps = Prefs[MEDIA_RPC_ENABLE_TIMESTAMPS, false]
                val playingMedia = getCurrentPlayingMedia()

                notificationManager.notify(
                    Constants.NOTIFICATION_ID,
                    notificationBuilder
                        .setContentText(
                            (playingMedia.details ?: "").ifEmpty { "Browsing Home Page.." })
                        .setLargeIcon(
                            rpcImage = playingMedia.largeImage,
                            context = this@MediaRpcService
                        )
                        .build()
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
                            setStatus(Prefs[Prefs.CUSTOM_ACTIVITY_STATUS,"dnd"])
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

    @SuppressLint("WakelockTimeout")
    private fun setupWakeLock() {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "kizzy:MediaRPC")
        wakeLock?.acquire()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            it.action?.let { ac ->
                if (ac == Constants.ACTION_STOP_SERVICE)
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
}
