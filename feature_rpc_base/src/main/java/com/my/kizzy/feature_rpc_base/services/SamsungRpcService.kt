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
import com.my.kizzy.feature_rpc_base.Constants
import com.my.kizzy.feature_rpc_base.setLargeIcon
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
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: Notification.Builder

    @Inject
    lateinit var logger: Logger
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(Constants.ACTION_STOP_SERVICE)) stopSelf()
        else {
            val stopIntent = Intent(this, SamsungRpcService::class.java)
            stopIntent.action = Constants.ACTION_STOP_SERVICE
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

                    notificationManager.notify(
                        Constants.NOTIFICATION_ID, notificationBuilder.apply {
                            if (currentApp.packageName.isNotEmpty()) {
                                setContentTitle("Playing ${currentApp.name}")
                            }
                            setLargeIcon(
                                rpcImage = currentApp.largeImage,
                                context = this@SamsungRpcService
                            )
                        }.build()
                    )
                    delay(1.minutes)
                }
            }
            startForeground(
                Constants.NOTIFICATION_ID,
                notificationBuilder
                    .setSmallIcon(R.drawable.ic_samsung_logo)
                    .setContentTitle("Service enabled")
                    .addAction(R.drawable.ic_samsung_logo, "Exit", pendingIntent)
                    .build()
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        scope.cancel()
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
        private var lastGamePackage = ""
    }
}