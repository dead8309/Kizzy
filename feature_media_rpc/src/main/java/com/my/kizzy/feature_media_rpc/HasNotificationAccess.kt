/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * HasNotificationAccess.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_media_rpc

import android.content.Context
import android.provider.Settings

fun Context.hasNotificationAccess(): Boolean {
    val enabledNotificationListeners = Settings.Secure.getString(
        this.contentResolver, "enabled_notification_listeners"
    )
    return enabledNotificationListeners != null && enabledNotificationListeners.contains(this.packageName)
}