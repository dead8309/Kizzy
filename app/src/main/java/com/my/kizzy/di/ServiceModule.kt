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

import com.my.kizzy.domain.repository.KizzyRepository
import com.my.kizzy.data.rpc.KizzyRPC
import com.my.kizzy.data.utils.Log
import com.my.kizzy.preference.Prefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
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
                    LogLevel.INFO -> Log.logger.i("Gateway", message.toString())
                    LogLevel.DEBUG -> Log.logger.d("Gateway", message.toString())
                    LogLevel.WARN -> Log.logger.w("Gateway", message.toString())
                    LogLevel.ERROR -> Log.logger.e("Gateway", message.toString())
                }
            }
        }
    }
    @Provides
    fun provideKizzyRpc(
        kizzyRepository: KizzyRepository,
        discordWebSocket: DiscordWebSocket
    ) = KizzyRPC(Prefs[Prefs.TOKEN, ""],kizzyRepository,discordWebSocket)

    @Provides
    fun providesCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}