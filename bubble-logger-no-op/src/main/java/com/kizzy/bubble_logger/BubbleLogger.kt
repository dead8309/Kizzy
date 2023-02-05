/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * BubbleLogger.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.kizzy.bubble_logger

import android.content.Context
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes

object BubbleLogger {

    fun log(
        context: Context,
        logType: LogType,
        title: CharSequence?,
        message: CharSequence,
        notificationId: Int,
        notificationChannelId: String,
        @Dimension(unit = Dimension.DP) desiredHeight: Int = 640,
        @DrawableRes notificationIconResource: Int = androidx.core.R.drawable.notification_bg,
        shortcutId: String = "logger"
    )
    {

    }

}
enum class LogType{
    VERBOSE, DEBUG, INFO, WARN, ERROR
}