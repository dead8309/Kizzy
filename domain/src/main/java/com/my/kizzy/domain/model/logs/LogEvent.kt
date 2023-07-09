/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * LogEvent.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.domain.model.logs

data class LogEvent(
    var level: LogLevel,
    var tag: String,
    var text: String,
    val createdAt: Long
)