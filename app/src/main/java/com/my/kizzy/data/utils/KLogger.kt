/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * KLogger.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.utils

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.my.kizzy.ui.theme.LogColors.color
import kizzy.gateway.entities.LogLevel

data class LogEvent(
    var level: LogLevel,
    var tag: String,
    var text: String,
    val createdAt: Long
){
    val annotated: AnnotatedString
        @SuppressLint("SimpleDateFormat") @Composable get() = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    background = level.color().copy(0.4f)
                )
            ) {
                append(" ${level.name[0]} ")
            }
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.ExtraBold,
                    background = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                )
            ) {
                append(SimpleDateFormat("h:mm:ssa").format(createdAt))
            }
            append(" $tag: $text")
        }
}

class KLogger private constructor() {
    var isEnabled = false
    private val logs = mutableStateListOf<LogEvent>()


    fun getLogs(): SnapshotStateList<LogEvent> {
        return logs
    }

    fun clear() {
        synchronized(logs) { logs.clear() }
    }

    fun i(tag: String, event: String) {
        addToLog(LogLevel.INFO, tag, event)
    }

    fun e(tag: String, event: String) {
        addToLog(LogLevel.ERROR, tag, event)
    }

    fun d(tag: String, event: String) {
        addToLog(LogLevel.DEBUG, tag, event)
    }

    fun w(tag: String, event: String) {
        addToLog(LogLevel.WARN, tag, event)
    }

    private fun addToLog(level: LogLevel, tag: String, event: String) {
        if (!isEnabled) return
        synchronized(logs) {
            if (logs.size > 1000) {
                logs.removeFirst()
            }
            val log = LogEvent(level, tag, event, System.currentTimeMillis())
            logs.add(log)
        }
    }

    companion object {
        private var instance: KLogger? = null
        @JvmStatic fun getInstance(): KLogger? {
            var localInstance = instance
            if (localInstance == null) {
                synchronized(KLogger::class.java) {
                    localInstance = instance
                    if (localInstance == null) {
                        localInstance = KLogger()
                        instance = localInstance
                    }
                }
            }
            return localInstance
        }

        @JvmStatic fun init() {
            if (instance == null) {
                instance = KLogger()
            }
        }
    }
}