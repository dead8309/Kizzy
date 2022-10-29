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

import android.content.Context
import com.android.girish.vlog.Vlog
import com.my.kizzy.BuildConfig
import com.my.kizzy.data.remote.ApiService
import com.my.kizzy.repository.KizzyRepository
import com.my.kizzy.repository.KizzyRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(ServiceComponent::class)
object AppModule {

    @Provides
    fun providesVlogInstance(
        @ApplicationContext context: Context
    ): Vlog {
        return Vlog.getInstance(context)
    }

    @Provides
    fun providesOkHttpClient(
        vlog: Vlog
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG)
            builder.addInterceptor(HttpLoggingInterceptor {
                vlog.d("Retrofit",it)
            }.setLevel(HttpLoggingInterceptor.Level.BODY))
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
}