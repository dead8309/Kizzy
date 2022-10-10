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
import com.my.kizzy.rpc.RpcImage
import com.my.kizzy.ui.screen.custom.IntentRpcData
import com.my.kizzy.utils.Prefs

class CustomRpcService : Service() {
    private lateinit var rpcData: IntentRpcData
    private var wakeLock: WakeLock? = null
    private lateinit var kizzyRPC: KizzyRPC

    @SuppressLint("WakelockTimeout")
    @Suppress("DEPRECATION")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(ACTION_STOP_SERVICE))
            stopSelf()
        else {
            val string =
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU)
                    intent?.getStringExtra("RPC")
            else
                intent?.getStringExtra("RPC")

            rpcData = Gson().fromJson(string, IntentRpcData::class.java)
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
            builder.setContentText(rpcData.name)
            builder.setSmallIcon(R.drawable.ic_rpc_placeholder)
            builder.addAction(R.drawable.ic_rpc_placeholder, "Exit", pendingIntent)
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK)
            wakeLock?.acquire()
            startForeground(7744, builder.build())
            Thread{
                    kizzyRPC = KizzyRPC(token = token)
                    kizzyRPC.apply {
                        setName(rpcData.name.ifEmpty { null })
                            setDetails(rpcData.details.ifEmpty { null })
                            setState(rpcData.state.ifEmpty { null })
                            setStatus(rpcData.status.ifEmpty { Constants.ONLINE })
                            setType(rpcData.type.toIntOrNull() ?: 0)
                            setStartTimestamps(rpcData.startTime.toLongOrNull())
                            setStopTimestamps(rpcData.StopTime.toLongOrNull())
                            setButton1(rpcData.button1.ifEmpty { null })
                            setButton1URL(rpcData.button1Url.ifEmpty { null })
                            setButton2(rpcData.button2.ifEmpty { null })
                            setButton2URL(rpcData.button2Url.ifEmpty { null })
                            setLargeImage(if (rpcData.largeImg.isEmpty()) null else rpcData.largeImg.toRpcImage)
                            setSmallImage(if (rpcData.smallImg.isEmpty()) null else rpcData.smallImg.toRpcImage)
                            build()
                    }
                }.start()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        try {
            if (kizzyRPC.isRpcRunning())
                kizzyRPC.closeRPC()
        } catch (_: UninitializedPropertyAccessException){}
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


private val String.toRpcImage: RpcImage
    get() {
        return if (this.startsWith("attachments"))
            RpcImage.DiscordImage(this)
        else
            RpcImage.ExternalImage(this)
    }
