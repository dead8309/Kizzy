/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * AppTracker.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.get_current_data

import android.util.Log
import com.my.kizzy.data.get_current_data.app.GetCurrentlyRunningApp
import com.my.kizzy.data.get_current_data.media.GetCurrentPlayingMedia
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import javax.inject.Inject

class AppTracker @Inject constructor(
    private val getCurrentlyRunningApp: GetCurrentlyRunningApp,
    private val getCurrentPlayingMedia: GetCurrentPlayingMedia
) {

    fun getCurrentAppData() = flow {
        var songTitle = "" // Title::packageName
        while (currentCoroutineContext().isActive) {
            val getCurrentMedia = getCurrentPlayingMedia()
            if (getCurrentMedia.name.isNotEmpty()) {
                if (songTitle != getCurrentMedia.packageName) {
                    songTitle = getCurrentMedia.packageName
                    emit(getCurrentMedia)
                }
            } else {
                val getCurrentApp = getCurrentlyRunningApp()
                if (getCurrentApp.name.isNotEmpty()) {
                    emit(getCurrentApp)
                }
            }
            delay(5000)
        }
    }.catch { exception ->
        Log.e("Error", exception.message.toString())
    }
}

