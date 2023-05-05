/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ServiceModule.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.di

import android.content.ComponentName
import android.content.Context
import com.my.kizzy.utils.Log.logger
import com.my.kizzy.domain.repository.KizzyRepository
import com.my.kizzy.preference.Prefs
import com.my.kizzy.services.NotificationListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kizzy.gateway.DiscordWebSocket
import kizzy.gateway.DiscordWebSocketImpl
import kizzy.gateway.entities.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {
    @Provides
    fun providesDiscordWebsocket(): DiscordWebSocket {
        return object : DiscordWebSocketImpl(Prefs[Prefs.TOKEN, ""]) {
            override fun log(message: Any?, logLevel: LogLevel) {
                super.log(message, logLevel)
                when (logLevel) {
                    LogLevel.INFO -> logger.i("Gateway", message.toString())
                    LogLevel.DEBUG -> logger.d("Gateway", message.toString())
                    LogLevel.WARN -> logger.w("Gateway", message.toString())
                    LogLevel.ERROR -> logger.e("Gateway", message.toString())
                }
            }
        }
    }
    @Provides
    fun provideKizzyRpc(
        kizzyRepository: KizzyRepository,
        discordWebSocket: DiscordWebSocket
    ) = com.my.kizzy.data.rpc.KizzyRPC(Prefs[Prefs.TOKEN, ""], kizzyRepository, discordWebSocket)

    @Provides
    fun providesCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
    @Provides
    fun providesComponentName(
        @ApplicationContext context: Context
    ) = ComponentName(context,NotificationListener::class.java)
}