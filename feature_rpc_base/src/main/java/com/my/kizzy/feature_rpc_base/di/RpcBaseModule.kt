/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * RpcBaseModule.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_rpc_base.di

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import com.my.kizzy.feature_rpc_base.Constants
import com.my.kizzy.feature_rpc_base.services.NotificationListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ServiceComponent::class)
object RpcBaseModule {

    @Provides
    fun providesComponentName(
        @ApplicationContext context: Context
    ) = ComponentName(context, NotificationListener::class.java)

    @Provides
    fun providesNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager {
        return context.getSystemService(NotificationManager::class.java)
    }

    @Provides
    fun providesNotificationBuilder(
        @ApplicationContext context: Context,
        notificationManager: NotificationManager
    ): Notification.Builder {
        val channel = NotificationChannel(
            Constants.CHANNEL_ID,
            Constants.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        channel.description = Constants.CHANNEL_DESCRIPTION
        notificationManager.createNotificationChannel(channel)
        return Notification.Builder(context, Constants.CHANNEL_ID)
    }
}
