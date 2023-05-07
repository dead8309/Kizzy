package com.my.kizzy

import android.app.Application
import com.google.android.material.color.DynamicColors
import com.my.kizzy.feature_crash_handler.CrashHandlerConfig
import com.my.kizzy.preference.PreferenceConfig
import com.my.kizzy.utils.AppUtils
import com.my.kizzy.utils.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        CrashHandlerConfig.apply()
        PreferenceConfig.apply(this)
        Log.init()
        AppUtils.init(this)
    }
}