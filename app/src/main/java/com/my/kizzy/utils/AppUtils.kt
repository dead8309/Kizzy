@file:Suppress("DEPRECATION")

package com.my.kizzy.utils

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.content.LocusIdCompat
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.kizzy.bubble_logger.BubbleLogger
import com.kizzy.bubble_logger.LogType
import com.my.kizzy.BuildConfig
import com.my.kizzy.MainActivity
import com.my.kizzy.service.AppDetectionService
import com.my.kizzy.service.CustomRpcService
import com.my.kizzy.service.ExperimentalRpc
import com.my.kizzy.service.MediaRpcService
import javax.inject.Singleton

@Singleton
object AppUtils {
    private lateinit var activityManager: ActivityManager
    fun init(context: Context) {
        activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    }

    fun appDetectionRunning(): Boolean {
        for (runningServiceInfo in activityManager.getRunningServices(
            Int.MAX_VALUE
        )) {
            if (AppDetectionService::class.java.name == runningServiceInfo.service.className) {
                return true
            }
        }
        return false
    }

    fun mediaRpcRunning(): Boolean {
        for (runningServiceInfo in activityManager.getRunningServices(
            Int.MAX_VALUE
        )) {
            if (MediaRpcService::class.java.name == runningServiceInfo.service.className) {
                return true
            }
        }
        return false
    }

    fun customRpcRunning(): Boolean {
        for (runningServiceInfo in activityManager.getRunningServices(
            Int.MAX_VALUE
        )) {
            if (CustomRpcService::class.java.name == runningServiceInfo.service.className)
                return true
        }
        return false
    }

    fun sharedRpcRunning(): Boolean {
        for (runningServiceInfo in activityManager.getRunningServices(
            Int.MAX_VALUE
        )) {
            if (ExperimentalRpc::class.java.name == runningServiceInfo.service.className)
                return true
        }
        return false
    }
}

object Log {
    lateinit var vlog: Logger
    const val CHANNEL_ID_BUBBLE_LOGGER = "CHANNEL_ID_BUBBLE_LOGGER"
    const val NOTIFICATION_ID = 8000
    fun init(context: Context) {
        if (BuildConfig.DEBUG){
            NotificationManagerCompat.from(context).createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID_BUBBLE_LOGGER,
                    "Logger",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        setAllowBubbles(true)
                    }
                    setSound(null, null)
                    enableLights(false)
                    enableVibration(false)
                    description = "Channel for bubble logger"
                }
            )

            //shortcut
            val bubbleIcon = IconCompat.createWithResource(
                context,
                com.kizzy.bubble_logger.R.drawable.ic_bubble_adaptive
            )
            // Setup shortcuts
            val shortcut = ShortcutInfoCompat.Builder(context, "logger")
                .setLocusId(LocusIdCompat("locus_id"))
                .setShortLabel("Logger")
                .setActivity(ComponentName(context, MainActivity::class.java))
                .setIntent(
                    Intent(context, MainActivity::class.java)
                        .setAction(Intent.ACTION_VIEW))
                .setIcon(bubbleIcon)
                .setLongLived(true)
                .setCategories(setOf("com.example.android.bubbles.category.TEXT_SHARE_TARGET"))
                .setPerson(
                    Person.Builder()
                        .setName("Logger")
                        .setIcon(bubbleIcon)
                        .build()
                )
                .build()
            ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
        }
        vlog = Logger.getInstance(context)
    }
}

class Logger private constructor(private val mApplicationContext: Context) {
    private val key = "bubble_logger"
    fun start() {
        Prefs[key] = true
    }

    fun stop() {
        Prefs[key] = false
    }

    fun isEnabled(): Boolean {
        return Prefs[key, false]
    }

    private fun logger(tag: String, msg: String, logType: LogType){
        if (Prefs[key, false]) {
            BubbleLogger.log(
                context = mApplicationContext,
                title = tag,
                message = msg,
                logType = logType,
                notificationId = Log.NOTIFICATION_ID,
                notificationChannelId = Log.CHANNEL_ID_BUBBLE_LOGGER
            )
        }
    }

    fun v(tag: String, msg: String) {
        logger(tag,msg,LogType.VERBOSE)
    }


    fun d(tag: String, msg: String) {
        logger(tag,msg,LogType.DEBUG)
    }


    fun i(tag: String, msg: String) {
        logger(tag,msg,LogType.INFO)
    }


    fun w(tag: String, msg: String) {
        logger(tag,msg,LogType.WARN)
    }

    fun e(tag: String, msg: String) {
        logger(tag,msg,LogType.ERROR)
    }

    companion object {
        private var instance: Logger? = null

        @JvmStatic
        fun getInstance(context: Context): Logger {
            synchronized(this) {
                if (instance == null) {
                    instance = Logger(context)
                }

                return instance!!
            }
        }
    }
}