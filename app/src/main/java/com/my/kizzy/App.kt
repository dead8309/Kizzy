package com.my.kizzy

import android.app.Application
import com.my.kizzy.utils.Prefs
import me.rerere.compose_setting.preference.initComposeSetting

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        initComposeSetting()
        Prefs.init(this)
    }
}