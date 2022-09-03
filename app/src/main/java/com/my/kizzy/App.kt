package com.my.kizzy

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.rerere.compose_setting.preference.initComposeSetting

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        initComposeSetting()
    }
}