/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * NetworkModule.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.di

import com.my.kizzy.domain.interfaces.Logger
import com.my.kizzy.feature_logs.LoggerProvider.logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun providesLogger(): Logger = logger
}