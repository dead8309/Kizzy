/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * CustomRpcService.kt is part of Kizzy
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
import com.my.kizzy.data.utils.toRpcImage
import com.my.kizzy.domain.model.rpc.RpcConfig
import com.my.kizzy.feature_rpc_base.Constants
import com.my.kizzy.feature_rpc_base.setLargeIcon
import com.my.kizzy.resources.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@AndroidEntryPoint
class CustomRpcService : Service() {
    private var rpcData: RpcConfig? = null
    private var wakeLock: WakeLock? = null

    @Inject
    lateinit var kizzyRPC: com.my.kizzy.data.rpc.KizzyRPC

    @Inject
    lateinit var scope: CoroutineScope

    @Inject
    lateinit var notificationBuilder: Notification.Builder

    @Inject
    lateinit var notificationManager: NotificationManager

    @SuppressLint("WakelockTimeout")
    @Suppress("DEPRECATION")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(Constants.ACTION_STOP_SERVICE)) stopSelf()
        else {
            val string = intent?.getStringExtra("RPC")
            string?.let {
                rpcData = Json.decodeFromString(it)
            }

            val stopIntent = Intent(this, CustomRpcService::class.java)
            stopIntent.action = Constants.ACTION_STOP_SERVICE
            val pendingIntent: PendingIntent = PendingIntent.getService(
                this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE
            )

            startForeground(
                Constants.NOTIFICATION_ID, notificationBuilder
                    .setContentTitle("Custom Rpc is running")
                    .setContentText(rpcData?.name ?: "")
                    .setSmallIcon(R.drawable.ic_rpc_placeholder)
                    .addAction(R.drawable.ic_rpc_placeholder, "Exit", pendingIntent)
                    .build()
            )

            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK)
            wakeLock?.acquire()
            scope.launch {
                notificationManager.notify(
                    Constants.NOTIFICATION_ID,
                    notificationBuilder
                        .setLargeIcon(
                            rpcImage = rpcData?.largeImg?.toRpcImage(),
                            context = this@CustomRpcService
                        )
                        .build()
                )

                kizzyRPC.apply {
                    rpcData?.let {
                        setName(it.name.ifEmpty { "" })
                        setDetails(it.details.ifEmpty { null })
                        setState(it.state.ifEmpty { null })
                        setStatus(it.status.ifEmpty { "online" })
                        setType(it.type.toIntOrNull() ?: 0)
                        setStartTimestamps(it.timestampsStart.toLongOrNull())
                        setStopTimestamps(it.timestampsStop.toLongOrNull())
                        setButton1(it.button1.ifEmpty { null })
                        setButton1URL(it.button1link.ifEmpty { null })
                        setButton2(it.button2.ifEmpty { null })
                        setButton2URL(it.button2link.ifEmpty { null })
                        setLargeImage(it.largeImg.toRpcImage(), it.largeText)
                        setSmallImage(it.smallImg.toRpcImage(), it.smallText)
                        setStreamUrl(it.url.ifEmpty { null })
                        build()
                    }
                }
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
        const val WAKELOCK = "kizzy:CustomRPC"
    }
}
