package com.my.kizzy.service

import android.annotation.SuppressLint
import android.app.*
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.MediaSessionManager
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import com.blankj.utilcode.util.AppUtils.getAppName
import com.my.kizzy.R
import com.my.kizzy.rpc.Constants
import com.my.kizzy.rpc.KizzyRPC
import com.my.kizzy.rpc.RpcImage
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.MEDIA_RPC_APP_ICON
import com.my.kizzy.utils.Prefs.MEDIA_RPC_ARTIST_NAME
import com.my.kizzy.utils.Prefs.MEDIA_RPC_ENABLE_TIMESTAMPS
import com.my.kizzy.utils.Prefs.TOKEN
import java.io.File


@Suppress("DEPRECATION")
class MediaRpcService : Service() {
    companion object {
        const val CHANNEL_ID = "MediaRPC"
        var App_Name = ""
        var TITLE = ""
        var enable_time: Boolean = false
        var author: String? = null
        var app_icon: RpcImage? = null
        var smallIcon: RpcImage? = null
        const val ACTION_STOP_SERVICE = "STOP_RPC"
    }

    var context: Context? = null
    private var thread: Thread? = null
    private var wakeLock: WakeLock? = null
    private lateinit var kizzyRPC: KizzyRPC

    @SuppressLint("WakelockTimeout")
    override fun onCreate() {
        super.onCreate()
        context = this
        val token = Prefs[TOKEN, ""]
        if (token.isEmpty()) stopSelf()
        val time = System.currentTimeMillis()
        val notificationManager = getSystemService(
            NotificationManager::class.java)
        notificationManager.createNotificationChannel(NotificationChannel(CHANNEL_ID,
            "Background Service",
            NotificationManager.IMPORTANCE_LOW))
        val builder = Notification.Builder(context, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.ic_media_rpc)
        val intent = Intent(this, MediaRpcService::class.java)
        intent.action = ACTION_STOP_SERVICE
        val pendingIntent = PendingIntent.getService(this,
            0, intent, PendingIntent.FLAG_IMMUTABLE)
        builder.addAction(R.drawable.ic_media_rpc, "Exit", pendingIntent)
        enable_time = Prefs[MEDIA_RPC_ENABLE_TIMESTAMPS, false]
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "kizzy:MediaRPC")
        wakeLock?.acquire()
        thread = Thread {
            while (thread?.isInterrupted == false) {
                try {
                    val mediaSessionManager = this.getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
                    val component = ComponentName(this, NotificationListener::class.java)
                    val sessions = mediaSessionManager.getActiveSessions(component)
                    if (sessions.size > 0) {
                        val mediaController = sessions[0]
                        val metadata = mediaController.metadata
                        val newTitle = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
                        val bitmap = metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                        if (newTitle != null) {
                            if (newTitle != TITLE) {
                                TITLE = newTitle
                                App_Name = getAppName(mediaController.packageName)
                                author = if (Prefs[MEDIA_RPC_ARTIST_NAME, false]) getArtistOrAuthor(metadata)
                                 else null
                                app_icon = if (Prefs[MEDIA_RPC_APP_ICON, false])
                                RpcImage.ApplicationIcon(mediaController.packageName, this)
                                   else null
                                if (bitmap != null){
                                   smallIcon = app_icon
                                   app_icon = RpcImage.BitmapImage(File(this.filesDir.toString() + File.separator + "art"),bitmap,mediaController.packageName,TITLE)
                                  } else smallIcon = null
                            }
                        }
                    } else {
                        App_Name = ""
                        TITLE = ""
                        author = ""
                        smallIcon = null
                    }
                    builder.setContentText(TITLE.ifEmpty { "Browsing Home Page.." })
                    startForeground(8838, builder.build())
                    if (kizzyRPC.isRpcRunning()) {
                        kizzyRPC.updateRPC(
                            name = App_Name.ifEmpty { "YouTube" },
                            details = TITLE.ifEmpty { "Browsing Home Page.." },
                            state = author,
                            large_image = app_icon,
                            small_image = smallIcon,
                            enableTimestamps = enable_time,
                            time = time
                        )
                    }
                    Thread.sleep(5000)
                } catch (ignored: InterruptedException) {
                } catch (ignored: NullPointerException) {
                }
            }
        }
        kizzyRPC = KizzyRPC(token).apply {
            setName("")
            setStatus(Constants.DND)
            build()
        }
        thread?.start()
    }

    private fun getArtistOrAuthor(metadata: MediaMetadata?): String? {
        return if (metadata!!.getString(MediaMetadata.METADATA_KEY_ARTIST) != null) "by " + metadata.getString(
            MediaMetadata.METADATA_KEY_ARTIST) else if (metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) != null) "by " + metadata.getString(
            MediaMetadata.METADATA_KEY_ARTIST) else null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            it.action?.let { ac ->
                if (ac == ACTION_STOP_SERVICE)
                    stopSelf()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        if (kizzyRPC.isRpcRunning())
            kizzyRPC.closeRPC()
        thread?.let {
            if (!it.isInterrupted) it.interrupt()
        }
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
