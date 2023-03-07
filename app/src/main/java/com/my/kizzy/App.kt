package com.my.kizzy

import android.app.Application
import com.developer.crashx.config.CrashConfig
import com.google.android.material.color.DynamicColors
import com.my.kizzy.data.utils.AppUtils
import com.my.kizzy.data.utils.Log
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        applicationScope = CoroutineScope(SupervisorJob())
        DynamicColors.applyToActivitiesIfAvailable(this)
        CrashConfig.Builder.create()
            .errorActivity(CrashHandler::class.java)
            .apply()
        Log.init()
        AppUtils.init(this)
    }

    companion object{
        lateinit var applicationScope: CoroutineScope
    }
}