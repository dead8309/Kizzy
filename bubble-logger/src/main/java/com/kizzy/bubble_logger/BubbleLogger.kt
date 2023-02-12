package com.kizzy.bubble_logger

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.content.LocusIdCompat
import androidx.core.graphics.drawable.IconCompat
import com.kizzy.bubble_logger.screen.BubbleActivity

@Keep
object BubbleLogger {

    /**
     * Add a log to bubble.
     *
     * @param context Context for the bubble notification.
     * @param title The title text of the og.
     * @param message The message text of the log.
     * @param notificationId The ID for the bubble notification.
     * @param notificationChannelId The channel ID for the bubble notification.
     * Adaptive icon resource for Android 11 (R, API 30) or later.
     * @param notificationIconResource Icon resource for bubble notification.
     */
    fun log(
        context: Context,
        logType: LogType,
        title: CharSequence?,
        message: CharSequence,
        notificationId: Int,
        notificationChannelId: String,
        @Dimension(unit = Dimension.DP) desiredHeight: Int = 640,
        @DrawableRes notificationIconResource: Int = R.drawable.notification,
        shortcutId: String = "logger"
    ) {
        BubbleDataHelper.log(type = logType, title = title, message = message)
        val target = Intent(context, BubbleActivity::class.java)
        val bubbleIntent =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(context, 2, target, PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getActivity(context, 2, target, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }
        val bubbleIcon = IconCompat.createWithResource(
            context,
            notificationIconResource
        )
        val bubbleMetadata =
            NotificationCompat.BubbleMetadata.Builder()
                .setIcon(bubbleIcon)
                .setIntent(bubbleIntent)
                .setDesiredHeight(desiredHeight)
                .build()
        val notificationTitle = title ?: context.getString(R.string.bubble_logger)
        val bot = Person.Builder()
            .setIcon(bubbleIcon)
            .setBot(true)
            .setName(notificationTitle)
            .setImportant(true)
            .build()

        val builder = NotificationCompat.Builder(context, notificationChannelId)
            .setStyle(
                NotificationCompat.MessagingStyle(bot)
                    .setConversationTitle(notificationTitle)
                    .addMessage(
                        NotificationCompat.MessagingStyle.Message(
                            message, System.currentTimeMillis(), bot
                        )
                    )
            )
            .setContentTitle(notificationTitle)
            .setContentText(message)
            .setSmallIcon(notificationIconResource)
            .setBubbleMetadata(bubbleMetadata)
            .setShortcutId(shortcutId)
            .setLocusId(LocusIdCompat(shortcutId))
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return
        NotificationManagerCompat.from(context).notify(notificationId, builder.build())
    }

}
