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
import com.my.kizzy.rpc.KizzyRPC
import com.my.kizzy.utils.Prefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import kizzy.gateway.DiscordWebSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {
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