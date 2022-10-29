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

import com.android.girish.vlog.Vlog
import com.my.kizzy.repository.KizzyRepository
import com.my.kizzy.rpc.KizzyRPC
import com.my.kizzy.utils.Prefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(ServiceScoped::class)
object ServiceModule {
    @Singleton
    @Provides
    fun provideKizzyRpc(
        kizzyRepository: KizzyRepository,
        vlog: Vlog
    ) = KizzyRPC(Prefs[Prefs.TOKEN,""],kizzyRepository,vlog)

    @Singleton
    @Provides
    fun providesCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
}