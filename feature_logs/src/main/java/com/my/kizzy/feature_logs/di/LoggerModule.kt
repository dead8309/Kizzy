/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * LoggerModule.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_logs.di

import com.my.kizzy.domain.interfaces.Logger
import com.my.kizzy.feature_logs.LoggerProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object LoggerModule {

    @Provides
    fun providesLogger(): Logger = LoggerProvider.logger
}