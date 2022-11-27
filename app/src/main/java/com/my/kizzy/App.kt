package com.my.kizzy

import android.app.Application
import com.developer.crashx.config.CrashConfig
import com.google.android.material.color.DynamicColors
import com.my.kizzy.utils.Log
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.LANGUAGE
import com.tencent.mmkv.MMKV
import com.yariksoffice.lingver.Lingver
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
        Lingver.init(this,Prefs[LANGUAGE,"en"])
        CrashConfig.Builder.create()
            .errorActivity(CrashHandler::class.java)
            .apply()
        Log.init(this)
    }

    companion object{
        lateinit var applicationScope: CoroutineScope
    }
}