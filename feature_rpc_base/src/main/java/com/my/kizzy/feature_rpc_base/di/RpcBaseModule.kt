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

import android.content.ComponentName
import android.content.Context
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
}