package com.my.kizzy.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import com.android.girish.vlog.Vlog
import com.google.gson.Gson
import com.my.kizzy.R
import com.my.kizzy.rpc.Constants
import com.my.kizzy.rpc.KizzyRPC
import com.my.kizzy.ui.screen.custom.IntentRpcData
import com.my.kizzy.utils.toRpcImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CustomRpcService : Service() {
    private var rpcData: IntentRpcData? = null
    private var wakeLock: WakeLock? = null

    @Inject
    lateinit var kizzyRPC: KizzyRPC

    @Inject
    lateinit var scope: CoroutineScope

    @Inject
    lateinit var vlog: Vlog

    @SuppressLint("WakelockTimeout")
    @Suppress("DEPRECATION")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(ACTION_STOP_SERVICE))
            stopSelf()
        else {
            val string = intent?.getStringExtra("RPC")
            string?.let {
                rpcData = Gson().fromJson(it, IntentRpcData::class.java)
            }
            vlog.d(CHANNEL_NAME,kizzyRPC.isUserTokenValid().toString())
            if (!kizzyRPC.isUserTokenValid())
                stopSelf()

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
            builder.setContentText(rpcData?.name ?: "")
            builder.setSmallIcon(R.drawable.ic_rpc_placeholder)
            builder.addAction(R.drawable.ic_rpc_placeholder, "Exit", pendingIntent)
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK)
            wakeLock?.acquire()
            startForeground(7744, builder.build())
            scope.launch {
              kizzyRPC.apply {
                    vlog.d(CHANNEL_NAME,"inside rpc block")
                        rpcData?.let {
                            setName(it.name.ifEmpty { null })
                            setDetails(it.details.ifEmpty { null })
                            setState(it.state.ifEmpty { null })
                            setStatus(it.status.ifEmpty { Constants.ONLINE })
                            setType(it.type.toIntOrNull() ?: 0)
                            setStartTimestamps(it.startTime.toLongOrNull())
                            setStopTimestamps(it.StopTime.toLongOrNull())
                            setButton1(it.button1.ifEmpty { null })
                            setButton1URL(it.button1Url.ifEmpty { null })
                            setButton2(it.button2.ifEmpty { null })
                            setButton2URL(it.button2Url.ifEmpty { null })
                            setLargeImage(if (it.largeImg.isEmpty()) null else it.largeImg.toRpcImage())
                            setSmallImage(if (it.smallImg.isEmpty()) null else it.smallImg.toRpcImage())
                            build()
                        }
                    }
                }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        scope.cancel()
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
