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

import com.my.kizzy.BuildConfig
import com.my.kizzy.data.remote.ApiService
import com.my.kizzy.data.repository.KizzyRepositoryImpl
import com.my.kizzy.domain.repository.KizzyRepository
import com.my.kizzy.utils.Log.logger
import com.my.kizzy.utils.Prefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kizzy.gateway.DiscordWebSocket
import kizzy.gateway.DiscordWebSocketImpl
import kizzy.gateway.entities.LogLevel
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        val log = logger
        builder.addInterceptor(HttpLoggingInterceptor {
                log.d("Retrofit",it)
        }.setLevel(HttpLoggingInterceptor.Level.BODY))
        builder.connectTimeout(30, TimeUnit.SECONDS)
        builder.readTimeout(30, TimeUnit.SECONDS)
        builder.writeTimeout(30, TimeUnit.SECONDS)
        return builder.build()
    }

    @Provides
    fun providesRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    fun providesApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    fun providesKizzyRepository(
        apiService: ApiService,
    ): KizzyRepository{
        return KizzyRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun providesDiscordWebsocket(): DiscordWebSocket {
        return object : DiscordWebSocketImpl(Prefs[Prefs.TOKEN, ""]) {
            override fun log(message: Any?, logLevel: LogLevel) {
                super.log(message, logLevel)
                when (logLevel) {
                    LogLevel.INFO -> logger.i("GATEWAY", message.toString())
                    LogLevel.DEBUG -> logger.d("GATEWAY", message.toString())
                    LogLevel.WARN -> logger.w("GATEWAY", message.toString())
                    LogLevel.ERROR -> logger.e("GATEWAY", message.toString())
                }
            }
        }
    }
}