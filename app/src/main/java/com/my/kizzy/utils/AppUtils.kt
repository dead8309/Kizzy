@file:Suppress("DEPRECATION")

package com.my.kizzy.utils

import android.app.ActivityManager
import android.content.Context
import com.my.kizzy.service.AppDetectionService
import com.my.kizzy.service.CustomRpcService
import com.my.kizzy.service.MediaRpcService

object AppUtils {

        fun appDetectionRunning(context: Context): Boolean {
            for (runningServiceInfo in (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(
                Int.MAX_VALUE)) {
                if (AppDetectionService::class.java.name == runningServiceInfo.service.className) {
                    return true
                }
            }
            return false
        }

        fun mediaRpcRunning(context: Context): Boolean {
            for (runningServiceInfo in (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(
                Int.MAX_VALUE)) {
                if (MediaRpcService::class.java.name == runningServiceInfo.service.className) {
                    return true
                }
            }
            return false
        }

        fun customRpcRunning(context: Context): Boolean {
            for (runningServiceInfo in (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(
                Int.MAX_VALUE)) {
                if (CustomRpcService::class.java.name == runningServiceInfo.service.className)
                    return true
            }
            return false
        }

    }