@file:Suppress("DEPRECATION")

package com.my.kizzy.service

import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.blankj.utilcode.util.AppUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.my.kizzy.R
import com.my.kizzy.common.Constants
import com.my.kizzy.rpc.KizzyRPC
import com.my.kizzy.rpc.RpcImage
import com.my.kizzy.ui.screen.settings.rpc_settings.RpcButtons
import com.my.kizzy.utils.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AppDetectionService : Service() {
    private var notifset = false
    private var context: Context? = null

    @Inject
    lateinit var kizzyRPC: KizzyRPC

    @Inject
    lateinit var scope: CoroutineScope

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(ACTION_STOP_SERVICE)) stopSelf()
        else {
            context = this
            notifset = false
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
            val rpcButtonsString = Prefs[Prefs.RPC_BUTTONS_DATA,"{}"]
            val rpcButtons = Gson().fromJson(rpcButtonsString,RpcButtons::class.java)
            scope.launch {
                while (isActive) {
                    val usageStatsManager =
                        (this@AppDetectionService).getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
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
                            if (enabledPackages.contains(packageName)) {
                                if (!kizzyRPC.isRpcRunning()) {
                                    kizzyRPC.apply {
                                        setName(AppUtils.getAppName(packageName))
                                        setStartTimestamps(System.currentTimeMillis())
                                        setStatus(Constants.DND)
                                        setLargeImage(RpcImage.ApplicationIcon(packageName, this@AppDetectionService))
                                        if (Prefs[Prefs.USE_RPC_BUTTONS,false]){
                                            with(rpcButtons){
                                                setButton1(button1.takeIf { it.isNotEmpty() })
                                                setButton1URL(button1Url.takeIf { it.isNotEmpty() })
                                                setButton2(button2.takeIf { it.isNotEmpty() })
                                                setButton2URL(button2Url.takeIf { it.isNotEmpty() })
                                            }
                                        }
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
                                if (kizzyRPC.isRpcRunning()) {
                                    kizzyRPC.closeRPC()
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
                    delay(5000)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
       scope.cancel()
        kizzyRPC.closeRPC()
        super.onDestroy()
    }

    companion object{
        const val ACTION_STOP_SERVICE = "Stop RPC"
        const val CHANNEL_ID = "background"
        const val CHANNEL_NAME = "App Detection Notification"
    }
}