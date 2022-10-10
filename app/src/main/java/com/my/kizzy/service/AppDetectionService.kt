@file:Suppress("DEPRECATION")

package com.my.kizzy.service

import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.blankj.utilcode.util.AppUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.my.kizzy.R
import com.my.kizzy.rpc.Constants
import com.my.kizzy.rpc.KizzyRPC
import com.my.kizzy.rpc.RpcImage
import com.my.kizzy.utils.Prefs
import java.util.*

class AppDetectionService : Service() {
    companion object{
        const val ACTION_STOP_SERVICE = "Stop RPC"
        const val CHANNEL_ID = "background"
        const val CHANNEL_NAME = "App Detection Notification"
    }

    private var thread: Thread? = null
    private var notifset = false
    private var running = false
    private var context: Context? = null
    private var kizzyRPC: KizzyRPC? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(ACTION_STOP_SERVICE)) stopSelf()
        else {
            context = this
            running = true
            notifset = false
            kizzyRPC = KizzyRPC(token = Prefs[Prefs.TOKEN,""])
            val apps = Prefs[Prefs.ENABLED_APPS, "[]"]
            val enabledPackages: ArrayList<String> = Gson().fromJson(
                apps,
                object : TypeToken<ArrayList<String>?>() {}.type
            )
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Background Service which notifies the Current Running app"
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
            val stopIntent = Intent(this,AppDetectionService::class.java)
            stopIntent.action = ACTION_STOP_SERVICE
            val pendingIntent: PendingIntent = PendingIntent.getService(this,
                0,stopIntent,PendingIntent.FLAG_IMMUTABLE)

            thread = Thread {
                while (running) {
                    val usageStatsManager =
                        (this).getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
                    val currentTimeMillis = System.currentTimeMillis()
                    val queryUsageStats = usageStatsManager.queryUsageStats(
                        UsageStatsManager.INTERVAL_DAILY,
                        currentTimeMillis - 10000,
                        currentTimeMillis
                    )
                    if (queryUsageStats != null && queryUsageStats.size > 1) {
                        val treeMap: SortedMap<Long, UsageStats> = TreeMap()
                        for (usageStats in queryUsageStats) {
                            treeMap[usageStats.lastTimeUsed] = usageStats
                        }
                        if (!(treeMap.isEmpty() ||
                                    treeMap[treeMap.lastKey()]?.packageName == "com.my.kizzy" ||
                                    treeMap[treeMap.lastKey()]?.packageName == "com.discord")
                        ) {
                            val packageName = treeMap[treeMap.lastKey()]!!.packageName
                            Objects.requireNonNull(packageName)
                            Log.i("current app", packageName)
                            if (enabledPackages.contains(packageName)) {
                                if (!kizzyRPC!!.isRpcRunning()) {
                                    kizzyRPC!!.apply {
                                        setName(AppUtils.getAppName(packageName))
                                        setStartTimestamps(System.currentTimeMillis())
                                        setStatus(Constants.DND)
                                        setLargeImage(RpcImage.ApplicationIcon(packageName, this@AppDetectionService))
                                        build()
                                    }
                                }
                                startForeground(
                                    1111,
                                    Notification.Builder(context, CHANNEL_ID)
                                        .setContentText(packageName)
                                        .setSmallIcon(R.drawable.ic_apps)
                                        .setContentTitle("Service enabled")
                                        .addAction(R.drawable.ic_apps,"Exit",pendingIntent)
                                        .build()
                                )
                                notifset = true
                            } else {
                                if (kizzyRPC!!.isRpcRunning()) {
                                    kizzyRPC?.closeRPC()
                                }
                                notifset = false
                            }
                        }
                    }
                    if (!notifset) {
                        startForeground(
                            1111,
                            Notification.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_apps)
                                .setContentTitle("Service enabled")
                                .addAction(R.drawable.ic_apps,"Exit",pendingIntent)
                                .build()
                        )
                    }
                    try {
                        Thread.sleep(5000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
            thread?.start()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
       thread?.interrupt()
        running = false
        kizzyRPC?.let {
                if (it.isRpcRunning()) it.closeRPC()
        }
        super.onDestroy()
    }
}