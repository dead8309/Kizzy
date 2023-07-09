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
import android.content.Intent
import android.os.IBinder
import com.my.kizzy.data.get_current_data.AppTracker
import com.my.kizzy.data.rpc.KizzyRPC
import com.my.kizzy.domain.interfaces.Logger
import com.my.kizzy.domain.model.rpc.RpcButtons
import com.my.kizzy.feature_rpc_base.Constants
import com.my.kizzy.feature_rpc_base.setLargeIcon
import com.my.kizzy.preference.Prefs
import com.my.kizzy.resources.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ExperimentalRpc : Service() {

    @Inject
    lateinit var scope: CoroutineScope

    @Inject
    lateinit var appTracker: AppTracker

    @Inject
    lateinit var kizzyRPC: KizzyRPC

    @Inject
    lateinit var logger: Logger

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: Notification.Builder
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(Constants.ACTION_STOP_SERVICE)) stopSelf()
        else {
            val stopIntent = Intent(this, ExperimentalRpc::class.java)
            stopIntent.action = Constants.ACTION_STOP_SERVICE
            val pendingIntent: PendingIntent = PendingIntent.getService(
                this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE
            )
            val rpcButtonsString = Prefs[Prefs.RPC_BUTTONS_DATA, "{}"]
            val rpcButtons = Json.decodeFromString<RpcButtons>(rpcButtonsString)

            scope.launch {
                appTracker.getCurrentAppData().onStart {
                    logger.e(TAG, "Starting Flow")
                }.collect { collectedData ->
                    logger.i(TAG, "Flow Data Received")
                    if (kizzyRPC.isRpcRunning()) {
                        kizzyRPC.updateRPC(collectedData)
                    } else kizzyRPC.apply {
                        setName(collectedData.name)
                        setStartTimestamps(System.currentTimeMillis())
                        setStatus(Prefs[Prefs.CUSTOM_ACTIVITY_STATUS,"dnd"])
                        setLargeImage(collectedData.largeImage)
                        setSmallImage(collectedData.smallImage)
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
            }
            startForeground(
                Constants.NOTIFICATION_ID,
                notificationBuilder
                    .setSmallIcon(R.drawable.ic_dev_rpc)
                    .setContentTitle("Service enabled")
                    .addAction(R.drawable.ic_dev_rpc, "Exit", pendingIntent)
                    .build()
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        const val TAG = "ExperimentalRpcService"
    }

    override fun onDestroy() {
        scope.cancel()
        kizzyRPC.closeRPC()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}