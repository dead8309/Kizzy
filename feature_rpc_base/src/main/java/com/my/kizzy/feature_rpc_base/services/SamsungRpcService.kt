/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * SamsungRpcService.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

@file:Suppress("DEPRECATION")

package com.my.kizzy.feature_rpc_base.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.IBinder
import com.my.kizzy.data.get_current_data.app.GetCurrentlyRunningApp
import com.my.kizzy.data.rpc.CommonRpc
import com.my.kizzy.domain.interfaces.Logger
import com.my.kizzy.domain.model.samsung_rpc.GalaxyPresence
import com.my.kizzy.domain.model.samsung_rpc.UpdateEvent
import com.my.kizzy.domain.repository.KizzyRepository
import com.my.kizzy.preference.Prefs
import com.my.kizzy.resources.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

@AndroidEntryPoint
class SamsungRpcService : Service() {
    @Inject
    lateinit var scope: CoroutineScope

    @Inject
    lateinit var getCurrentlyRunningApp: GetCurrentlyRunningApp

    @Inject
    lateinit var kizzyRepository: KizzyRepository

    @Inject
    lateinit var logger: Logger
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(ACTION_STOP_SERVICE)) stopSelf()
        else {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description =
                "Background Service which notifies the Current Running game"
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)

            val stopIntent = Intent(this, SamsungRpcService::class.java)
            stopIntent.action = ACTION_STOP_SERVICE
            val pendingIntent: PendingIntent = PendingIntent.getService(
                this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE
            )
            var count = 0
            scope.launch {
                while (scope.isActive) {
                    val currentApp =
                        getCurrentlyRunningApp(System.currentTimeMillis() - 1.days.inWholeMilliseconds)
                    logger.d("SamsungRpcService", currentApp.toString())
                    val update = determineUpdateValue(currentApp)

                    val galaxyPresence = createGalaxyPresence(
                        packageName = currentApp.packageName.ifEmpty { lastGamePackage },
                        update = update
                    ).also {
                        if (it?.update == UpdateEvent.STOP) {
                            logger.d(
                                tag = "SamsungRpcService",
                                event = "{lastGamePackage} value changed to \"\""
                            )
                            lastGamePackage = ""
                        }
                    }

                    galaxyPresence?.let {
                        kizzyRepository.setSamsungGalaxyPresence(
                            galaxyPresence = it,
                            token = Prefs[Prefs.TOKEN]
                        )
                        count++

                    }

                    manager.notify(
                        2293, Notification.Builder(this@SamsungRpcService, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_samsung_logo)
                            .setContentTitle("Playing ${currentApp.name}")
                            .addAction(R.drawable.ic_samsung_logo, "Exit", pendingIntent)
                            .build()
                    )
                    delay(1.minutes)
                }
            }
            startForeground(
                2293,
                Notification.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_samsung_logo)
                    .setContentTitle("Service enabled")
                    .addAction(R.drawable.ic_samsung_logo, "Exit", pendingIntent)
                    .build()
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        createGalaxyPresence(
            packageName = lastGamePackage,
            update = UpdateEvent.UPDATE
        )?.let {
            scope.launch {
                kizzyRepository.setSamsungGalaxyPresence(
                    it,
                    Prefs[Prefs.TOKEN]
                )
                cancel()
            }
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun CommonRpc.isGame(): Boolean {
        val applicationInfo = baseContext.packageManager.getApplicationInfo(this.packageName, 0)
        return applicationInfo.category == ApplicationInfo.CATEGORY_GAME
    }

    private fun determineUpdateValue(currentApp: CommonRpc): UpdateEvent {
        return if (currentApp.packageName.isNotEmpty()) {
            if (currentApp.isGame()) {
                if (currentApp.packageName == lastGamePackage) UpdateEvent.UPDATE
                else {
                    logger.d(
                        "SamsungRpcService",
                        "{lastGamePackage} value changed to ${currentApp.packageName}"
                    )
                    lastGamePackage = currentApp.packageName
                    UpdateEvent.START
                }
            } else UpdateEvent.STOP
        } else {
            UpdateEvent.STOP
        }
    }

    private fun createGalaxyPresence(packageName: String, update: UpdateEvent): GalaxyPresence? {
        return if (packageName.isNotEmpty()) {
            GalaxyPresence(
                packageName = packageName,
                update = update
            )
        } else {
            null
        }
    }

    companion object {
        const val ACTION_STOP_SERVICE = "Stop RPC"
        const val CHANNEL_ID = "samsung_rpc_notification_channel"
        const val CHANNEL_NAME = "Samsung Rpc"
        private var lastGamePackage = ""
    }
}