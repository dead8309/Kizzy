/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * SharedRpcService.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import com.google.gson.GsonBuilder
import com.my.kizzy.R
import com.my.kizzy.common.Constants
import com.my.kizzy.domain.use_case.get_current_data.AppTracker
import com.my.kizzy.rpc.KizzyRPC
import com.my.kizzy.ui.screen.settings.rpc_settings.RpcButtons
import com.my.kizzy.utils.Log.vlog
import com.my.kizzy.utils.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ExperimentalRpc: Service() {

    @Inject
    lateinit var scope: CoroutineScope

    @Inject
    lateinit var appTracker: AppTracker

    @Inject
    lateinit var kizzyRPC: KizzyRPC

    private val gson = GsonBuilder().setPrettyPrinting().create()
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(ACTION_STOP_SERVICE)) stopSelf()
        else {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description =
                "Background Service which notifies the Current Running app or media"
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)

            val stopIntent = Intent(this,ExperimentalRpc::class.java)
            stopIntent.action = ACTION_STOP_SERVICE
            val pendingIntent: PendingIntent = PendingIntent.getService(
                this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE
            )
            val rpcButtonsString = Prefs[Prefs.RPC_BUTTONS_DATA, "{}"]
            val rpcButtons = gson.fromJson(rpcButtonsString, RpcButtons::class.java)

            scope.launch {
                appTracker.getCurrentAppData().onStart {
                    vlog.e(TAG, "Starting Flow")
                }.collect { collectedData ->
                    vlog.i(TAG,"Flow Data Received")
                    if (kizzyRPC.isRpcRunning()) {
                        kizzyRPC.updateRPC(collectedData)
                    } else kizzyRPC.apply {
                        setName(collectedData.name)
                        setStartTimestamps(System.currentTimeMillis())
                        setStatus(Constants.DND)
                        setLargeImage(collectedData.large_image)
                        setSmallImage(collectedData.small_image)
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
                    manager.notify(2292,Notification.Builder(this@ExperimentalRpc, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_dev_rpc)
                        .setContentTitle(collectedData.name)
                        .setContentText(collectedData.details)
                        .addAction(R.drawable.ic_dev_rpc,"Exit",pendingIntent)
                        .build())
                }
            }
            startForeground(
                2292,
                Notification.Builder(this, AppDetectionService.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_dev_rpc)
                    .setContentTitle("Service enabled")
                    .addAction(R.drawable.ic_dev_rpc,"Exit",pendingIntent)
                    .build())
        }
        return super.onStartCommand(intent, flags, startId)
    }

    companion object {
        const val ACTION_STOP_SERVICE = "Stop RPC"
        const val TAG = "SharedRpcService"
        const val CHANNEL_ID = "background"
        const val CHANNEL_NAME = "Shared Rpc Notification"
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