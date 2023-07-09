/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * AppUtils.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

@file:Suppress("DEPRECATION")

package com.my.kizzy.feature_rpc_base

import android.app.ActivityManager
import android.content.Context
import com.my.kizzy.feature_rpc_base.services.AppDetectionService
import com.my.kizzy.feature_rpc_base.services.CustomRpcService
import com.my.kizzy.feature_rpc_base.services.ExperimentalRpc
import com.my.kizzy.feature_rpc_base.services.MediaRpcService
import com.my.kizzy.feature_rpc_base.services.SamsungRpcService
import javax.inject.Singleton

@Singleton
object AppUtils {
    private lateinit var activityManager: ActivityManager
    fun init(context: Context) {
        activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    }
    fun appDetectionRunning(): Boolean {
        return checkForRunningService<AppDetectionService>()
    }

    fun mediaRpcRunning(): Boolean {
        return checkForRunningService<MediaRpcService>()
    }

    fun customRpcRunning(): Boolean {
        return checkForRunningService<CustomRpcService>()
    }

    fun experimentalRpcRunning(): Boolean {
        return checkForRunningService<ExperimentalRpc>()
    }
    fun samsungRpcRunning(): Boolean {
        return checkForRunningService<SamsungRpcService>()
    }
    private inline fun <reified T : Any> checkForRunningService(): Boolean {
        for (runningServiceInfo in activityManager.getRunningServices(
            Int.MAX_VALUE
        )) {
            if (T::class.java.name == runningServiceInfo.service.className)
                return true
        }
        return false
    }
}