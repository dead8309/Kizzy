/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * DataModule.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data

import com.my.kizzy.data.remote.Base
import com.my.kizzy.data.remote.Discord
import com.my.kizzy.data.remote.Github
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    @Base
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Provides
    @Singleton
    @Discord
    fun provideDiscordBaseUrl() = "https://discord.com/api/v10"

    @Provides
    @Singleton
    @Github
    fun provideGithubBaseUrl() = "https://api.github.com"
}