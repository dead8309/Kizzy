package com.my.kizzy

import android.app.Application
import com.my.kizzy.ui.screen.nintendo.Games
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.LANGUAGE
import com.yariksoffice.lingver.Lingver
import me.rerere.compose_setting.preference.initComposeSetting

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        initComposeSetting()
        Prefs.init(this)
        Games.init(this)
        Lingver.init(this,Prefs[LANGUAGE,"en"])
    }
}