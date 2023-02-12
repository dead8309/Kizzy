/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * BubbleDataHelper.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.kizzy.bubble_logger

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal object BubbleDataHelper {

    data class Log(
        val id: Long,
        val title: CharSequence?,
        val message: CharSequence,
        val type: LogType,
        val timestamp: Long
    )

    val logs = MutableLiveData<List<Log>?>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun log(type: LogType, title: CharSequence?, message: CharSequence) {
        coroutineScope.launch {
            val log = Log(
                type = type,
                id = System.nanoTime(),
                title = title,
                timestamp = System.currentTimeMillis(),
                message = if (message.length > 500)
                    message.substring(0, 499) + "..."
                else message
            )
            logs.apply { value = (value?.let { it + log } ?: listOf(log)) }
        }
    }

    fun clearLogs() {
        logs.value = null
    }
}