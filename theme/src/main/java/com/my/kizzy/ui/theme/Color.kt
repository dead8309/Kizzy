/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Color.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.theme

import androidx.compose.ui.graphics.Color
import com.my.kizzy.domain.model.logs.LogLevel

//Dark Theme
val DarkBlueBg = Color(0xFF3C45A5)

val DISCORD_LIGHT_DARK = Color(0xFF282b30)
val DISCORD_GREY = Color(0xFF36393e)

object LogColors{
    private val error= android.graphics.Color.parseColor("#F44336")
    private val info= android.graphics.Color.parseColor("#4CAF50")
    private val warn= android.graphics.Color.parseColor("#FFC107")
    private val debug= android.graphics.Color.parseColor("#2196F3")
    fun LogLevel.color() = Color(when(this){
        LogLevel.INFO -> info
        LogLevel.DEBUG -> debug
        LogLevel.WARN -> warn
        LogLevel.ERROR -> error
    })
}

