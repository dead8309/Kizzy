package com.my.kizzy

import com.developer.crashx.config.CrashConfig
import com.google.android.material.color.DynamicColors
import com.my.kizzy.utils.AppUtils
import com.my.kizzy.utils.Log
import com.my.kizzy.preference.PreferenceApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: PreferenceApp() {

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        CrashConfig.Builder.create()
            .errorActivity(CrashHandler::class.java)
            .apply()
        Log.init()
        AppUtils.init(this)
    }
}