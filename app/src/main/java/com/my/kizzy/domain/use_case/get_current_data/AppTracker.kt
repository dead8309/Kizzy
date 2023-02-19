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

package com.my.kizzy.domain.use_case.get_current_data

import android.content.Context
import com.my.kizzy.domain.use_case.get_current_data.get_apps.getCurrentRunningApp
import com.my.kizzy.domain.use_case.get_current_data.get_media.getCurrentRunningMedia
import com.my.kizzy.rpc.RpcImage
import com.my.kizzy.utils.Log.logger
import dagger.hilt.android.qualifiers.ApplicationContext
import kizzy.gateway.entities.presence.Timestamps
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import javax.inject.Inject

class AppTracker @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getCurrentAppData() = flow {
        var songTitle = "" // Title::packageName
        while (currentCoroutineContext().isActive) {
            val getCurrentMedia = getCurrentRunningMedia(context)
            if (getCurrentMedia.name.isNotEmpty()) {
                if (songTitle != getCurrentMedia.package_name) {
                    songTitle = getCurrentMedia.package_name
                    emit(getCurrentMedia)
                }
            } else {
                val getCurrentApp = getCurrentRunningApp(context)
                if (getCurrentApp.name.isNotEmpty()) {
                    emit(getCurrentApp)
                } else logger.d("AppTracker", "No Apps Running")
            }
            delay(5000)
        }
    }.catch { exception ->
        logger.e("Error", exception.message.toString())
    }
}

data class SharedRpc(
    val name: String = "",
    val details: String? = "",
    val state: String? = "",
    val large_image: RpcImage? = null,
    val small_image: RpcImage? = null,
    val time: Timestamps? = null,
    val package_name: String = ""
)