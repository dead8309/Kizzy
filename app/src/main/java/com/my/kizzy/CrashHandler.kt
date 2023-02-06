package com.my.kizzy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import com.developer.crashx.CrashActivity
import com.my.kizzy.common.LocalDarkTheme
import com.my.kizzy.common.LocalDynamicColorSwitch
import com.my.kizzy.common.LocalSeedColor
import com.my.kizzy.ui.screen.crash.CrashScreen
import com.my.kizzy.ui.theme.KizzyTheme

class CrashHandler : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val report = CrashActivity.getStackTraceFromIntent(intent)
        setContent {
            KizzyTheme(
                darkTheme = LocalDarkTheme.current.isDarkTheme(),
                isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                seedColor = LocalSeedColor.current,
                isDynamicColorEnabled = LocalDynamicColorSwitch.current,
            ){
                CrashScreen(trace = report.buildCrashLog())
            }
        }
    }
    private fun String?.buildCrashLog(): String {
        return """Kizzy crash report
Manufacturer: ${DeviceUtils.getManufacturer()}
Device: ${DeviceUtils.getModel()}
Android version: ${DeviceUtils.getSDKVersionName()}
App version: ${AppUtils.getAppVersionName()} (${AppUtils.getAppVersionCode()})
Stacktrace: 
$this"""
    }
}
