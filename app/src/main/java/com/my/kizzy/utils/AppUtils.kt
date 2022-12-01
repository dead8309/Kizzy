@file:Suppress("DEPRECATION")

package com.my.kizzy.utils

import android.app.ActivityManager
import android.content.Context
import com.android.girish.vlog.Vlog
import com.my.kizzy.service.AppDetectionService
import com.my.kizzy.service.CustomRpcService
import com.my.kizzy.service.MediaRpcService
import javax.inject.Singleton

@Singleton
object AppUtils {
    lateinit var activityManager: ActivityManager

    fun init(context: Context){
        activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    }
        fun appDetectionRunning(): Boolean {
            for (runningServiceInfo in activityManager.getRunningServices(
                Int.MAX_VALUE)) {
                if (AppDetectionService::class.java.name == runningServiceInfo.service.className) {
                    return true
                }
            }
            return false
        }

        fun mediaRpcRunning(): Boolean {
            for (runningServiceInfo in activityManager.getRunningServices(
                Int.MAX_VALUE)) {
                if (MediaRpcService::class.java.name == runningServiceInfo.service.className) {
                    return true
                }
            }
            return false
        }

        fun customRpcRunning(): Boolean {
            for (runningServiceInfo in activityManager.getRunningServices(
                Int.MAX_VALUE)) {
                if (CustomRpcService::class.java.name == runningServiceInfo.service.className)
                    return true
            }
            return false
        }
}

object Log{
    lateinit var vlog: Vlog
    fun init(context: Context){
        vlog = Vlog.getInstance(context)
    }
}