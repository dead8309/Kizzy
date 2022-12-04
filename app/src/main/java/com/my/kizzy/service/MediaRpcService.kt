package com.my.kizzy.service

import android.annotation.SuppressLint
import android.app.*
import android.content.ComponentName
import android.content.Intent
import android.media.MediaMetadata
import android.media.session.MediaSessionManager
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import com.blankj.utilcode.util.AppUtils
import com.google.gson.Gson
import com.my.kizzy.R
import com.my.kizzy.common.Constants
import com.my.kizzy.rpc.KizzyRPC
import com.my.kizzy.rpc.RpcImage
import com.my.kizzy.ui.screen.settings.rpc_settings.RpcButtons
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.MEDIA_RPC_ENABLE_TIMESTAMPS
import com.my.kizzy.utils.Prefs.TOKEN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MediaRpcService : Service() {

    @Inject
    lateinit var kizzyRPC: KizzyRPC

    @Inject
    lateinit var scope: CoroutineScope

    private var wakeLock: WakeLock? = null

    @Suppress("DEPRECATION")
    @SuppressLint("WakelockTimeout")
    override fun onCreate() {
        super.onCreate()
        val token = Prefs[TOKEN, ""]
        if (token.isEmpty()) stopSelf()
        val time = System.currentTimeMillis()
        val notificationManager = getSystemService(
            NotificationManager::class.java)
        notificationManager.createNotificationChannel(NotificationChannel(CHANNEL_ID,
            "Background Service",
            NotificationManager.IMPORTANCE_LOW))
        val builder = Notification.Builder(this, CHANNEL_ID)
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
        var appIcon: RpcImage? = null
        var smallIcon: RpcImage? = null
        scope.launch {
            while (isActive) {
                try {
                    val rpcButtonsString = Prefs[Prefs.RPC_BUTTONS_DATA,"{}"]
                    val rpcButtons = Gson().fromJson(rpcButtonsString, RpcButtons::class.java)
                    val mediaSessionManager = this@MediaRpcService.getSystemService(MEDIA_SESSION_SERVICE) as MediaSessionManager
                    val component = ComponentName(this@MediaRpcService, NotificationListener::class.java)
                    val sessions = mediaSessionManager.getActiveSessions(component)
                    if (sessions.size > 0) {
                        val mediaController = sessions[0]
                        val metadata = mediaController.metadata
                        val newTitle = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
                        val bitmap = metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                        if (newTitle != null) {
                            if (newTitle != TITLE) {
                                TITLE = newTitle
                                App_Name = AppUtils.getAppName(mediaController.packageName)
                                author = if (Prefs[Prefs.MEDIA_RPC_ARTIST_NAME, false]) getArtistOrAuthor(metadata)
                                else null
                                appIcon = if (Prefs[Prefs.MEDIA_RPC_APP_ICON, false])
                                    RpcImage.ApplicationIcon(mediaController.packageName, this@MediaRpcService)
                                else null
                                if (bitmap != null){
                                    smallIcon = appIcon
                                    appIcon = RpcImage.BitmapImage(
                                        this@MediaRpcService,
                                        bitmap,
                                        mediaController.packageName,
                                        TITLE
                                    )
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
                    if(kizzyRPC.isUserTokenValid()){
                        if (kizzyRPC.isRpcRunning()) {
                            kizzyRPC.updateRPC(
                                name = App_Name.ifEmpty { "YouTube" },
                                details = TITLE.ifEmpty { "Browsing Home Page.." },
                                state = author,
                                large_image = appIcon,
                                small_image = smallIcon,
                                enableTimestamps = enable_time,
                                time = time
                            )
                        } else {
                            kizzyRPC.apply {
                                setName(App_Name.ifEmpty { "YouTube" })
                                setDetails(TITLE.ifEmpty { "Browsing Home Page.." })
                                setStatus(Constants.DND)
                                if (Prefs[Prefs.USE_RPC_BUTTONS,false]) {
                                    with(rpcButtons) {
                                        setButton1(button1.takeIf { it.isNotEmpty() })
                                        setButton1URL(button1Url.takeIf { it.isNotEmpty() })
                                        setButton2(button2.takeIf { it.isNotEmpty() })
                                        setButton2URL(button2Url.takeIf { it.isNotEmpty() })
                                    }
                                }
                                build()
                            }
                        }
                    }
                    delay(5000)
                } catch (ignored: NullPointerException) {}
            }
        }
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
        scope.cancel()
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

    companion object {
        const val CHANNEL_ID = "MediaRPC"
        var App_Name = ""
        var TITLE = ""
        var enable_time: Boolean = false
        var author: String? = null
        const val ACTION_STOP_SERVICE = "STOP_RPC"

        fun getArtistOrAuthor(metadata: MediaMetadata?): String? {
            return if (metadata!!.getString(MediaMetadata.METADATA_KEY_ARTIST) != null) "by " + metadata.getString(
                MediaMetadata.METADATA_KEY_ARTIST) else if (metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) != null) "by " + metadata.getString(
                MediaMetadata.METADATA_KEY_ARTIST) else null
        }
    }
}
