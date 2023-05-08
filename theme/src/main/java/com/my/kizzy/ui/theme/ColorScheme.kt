/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ColorScheme.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

/**
 * source: https://github.com/JunkFood02/Seal
 */
package com.my.kizzy.ui.theme

import androidx.compose.runtime.Composable

@Composable
fun Number.autoDark(isDarkTheme: Boolean = LocalDarkTheme.current.isDarkTheme()): Double =
    if (!isDarkTheme) this.toDouble()
    else when (this.toDouble()) {
        10.0 -> 99.0
        20.0 -> 95.0
        25.0 -> 90.0
        30.0 -> 90.0
        40.0 -> 80.0
        50.0 -> 60.0
        60.0 -> 50.0
        70.0 -> 40.0
        80.0 -> 40.0
        90.0 -> 30.0
        95.0 -> 20.0
        98.0 -> 10.0
        99.0 -> 10.0
        100.0 -> 20.0
        else -> this.toDouble()
    }

