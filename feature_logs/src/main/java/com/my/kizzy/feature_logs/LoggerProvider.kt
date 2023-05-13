/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * LogProvider.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_logs

object LoggerProvider {
    var logger = KLogger.getInstance()!!
    fun init() {
        KLogger.init()
    }
}