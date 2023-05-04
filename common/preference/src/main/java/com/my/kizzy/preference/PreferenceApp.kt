/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * PreferenceApp.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.preference

import android.app.Application
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

open class PreferenceApp: Application() {
    companion object {
        lateinit var applicationScope: CoroutineScope
    }
    override fun onCreate() {
        super.onCreate()
        applicationScope = CoroutineScope(SupervisorJob())
        MMKV.initialize(this)
    }
}