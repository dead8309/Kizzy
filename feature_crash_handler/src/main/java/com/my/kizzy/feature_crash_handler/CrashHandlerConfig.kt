/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Config.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_crash_handler

import com.developer.crashx.config.CrashConfig

object CrashHandlerConfig {
    fun apply() {
        CrashConfig.Builder.create()
            .errorActivity(CrashHandler::class.java)
            .apply()
    }
}