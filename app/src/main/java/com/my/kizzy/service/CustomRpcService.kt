package com.my.kizzy.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import com.google.gson.Gson
import com.my.kizzy.R
import com.my.kizzy.rpc.Constants
import com.my.kizzy.rpc.KizzyRPC
import com.my.kizzy.ui.screen.custom.Rpc
import com.my.kizzy.utils.Prefs

class CustomRpcService : Service() {
    lateinit var rpc: Rpc
    private var wakeLock: WakeLock? = null
    private lateinit var kizzyRPC: KizzyRPC

    @SuppressLint("WakelockTimeout")
    @Suppress("DEPRECATION")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(ACTION_STOP_SERVICE)) stopSelf()
        val string =
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU)
                intent?.getStringExtra("RPC")
        else
            intent?.getStringExtra("RPC")

        rpc = Gson().fromJson(string, Rpc::class.java)
        val token = Prefs[Prefs.TOKEN,""]
        if (token.isEmpty()) stopSelf()
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        channel.description = CHANNEL_NAME
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
        val stopIntent = Intent(this,CustomRpcService::class.java)
        stopIntent.action = ACTION_STOP_SERVICE
        val pendingIntent: PendingIntent = PendingIntent.getService(this,
            0,stopIntent, PendingIntent.FLAG_IMMUTABLE)
        val builder = Notification.Builder(this, CHANNEL_ID)
        builder.setContentTitle("$CHANNEL_NAME is running")
        builder.setContentText(rpc.name)
        builder.setSmallIcon(R.drawable.ic_rpc_placeholder)
        builder.addAction(R.drawable.ic_rpc_placeholder, "Exit", pendingIntent)
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK)
        wakeLock?.acquire()
        startForeground(7744, builder.build())
        Thread{
                kizzyRPC = KizzyRPC(token = token)
                kizzyRPC.setName(rpc.name.ifEmpty { null })
                    .setDetails(rpc.details.ifEmpty { null })
                    .setState(rpc.state.ifEmpty { null })
                    .setStatus(rpc.status.ifEmpty { Constants.ONLINE })
                    .setType(rpc.type.toIntOrNull()?:0)
                    .setStartTimestamps(rpc.startTime.toLongOrNull())
                    .setStopTimestamps(rpc.StopTime.toLongOrNull())
                    .setButton1(rpc.button1.ifEmpty { null })
                    .setButton1URL(rpc.button1Url.ifEmpty { null })
                    .setButton2(rpc.button2.ifEmpty { null })
                    .setButton2URL(rpc.button2Url.ifEmpty { null })
                    .setLargeImage(rpc.largeImg.ifEmpty { null })
                    .setSmallImage(rpc.smallImg.ifEmpty { null })
                    .build()
            }.start()
            return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        if (kizzyRPC.isRpcRunning())
            kizzyRPC.closeRPC()
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        super.onDestroy()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object{
        const val ACTION_STOP_SERVICE = "Stop RPC"
        const val CHANNEL_ID = "background"
        const val CHANNEL_NAME = "Custom Rpc"
        const val WAKELOCK = "kizzy:CustomRPC"
    }

}