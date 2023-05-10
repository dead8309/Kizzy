/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ProvideFeatures.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_home

import android.content.Intent
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.my.kizzy.feature_rpc_base.AppUtils
import com.my.kizzy.feature_rpc_base.services.AppDetectionService
import com.my.kizzy.feature_rpc_base.services.CustomRpcService
import com.my.kizzy.feature_rpc_base.services.ExperimentalRpc
import com.my.kizzy.feature_rpc_base.services.MediaRpcService
import com.my.kizzy.feature_rpc_base.startServiceAndStopOthers
import com.my.kizzy.feature_rpc_base.stopService
import com.my.kizzy.navigation.Routes
import com.my.kizzy.preference.Prefs

@Composable
fun provideFeatures(
    navigateTo: (String) -> Unit,
    hasUsageAccess: MutableState<Boolean>,
    hasNotificationAccess: MutableState<Boolean>,
    userVerified: Boolean
): List<HomeFeatures> {
    val ctx = LocalContext.current
    return listOf(
        HomeFeatures(
            title = "App Detection",
            icon = R.drawable.ic_apps,
            route = Routes.APPS_DETECTION,
            isChecked = AppUtils.appDetectionRunning(),
            showSwitch = hasUsageAccess.value,
            onClick = {
                navigateTo(it)
            },
            onCheckedChange = {
                if (it) {
                    ctx.startServiceAndStopOthers<AppDetectionService>()
                } else ctx.stopService<AppDetectionService>()
            },
            shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp)
        ), HomeFeatures(
            title = "Media Rpc",
            icon = R.drawable.ic_media_rpc,
            route = Routes.MEDIA_RPC,
            isChecked = AppUtils.mediaRpcRunning(),
            showSwitch = hasNotificationAccess.value,
            onClick = {
                navigateTo(it)
            },
            onCheckedChange = {
                if (it) {
                    ctx.startServiceAndStopOthers<MediaRpcService>()
                } else ctx.stopService<MediaRpcService>()
            },
            shape = RoundedCornerShape(44.dp, 20.dp, 44.dp, 20.dp)
        ), HomeFeatures(
            title = "Custom Rpc",
            icon = R.drawable.ic_rpc_placeholder,
            route = Routes.CUSTOM_RPC,
            isChecked = AppUtils.customRpcRunning(),
            onClick = {
                navigateTo(it)
            },
            onCheckedChange = {
                if (it) {
                    val lastRpc = Prefs[Prefs.LAST_RUN_CUSTOM_RPC, ""]
                    val intent = Intent(ctx, CustomRpcService::class.java)
                    intent.apply {
                        putExtra("RPC", lastRpc)
                    }
                    ctx.stopService<MediaRpcService>()
                    ctx.stopService<ExperimentalRpc>()
                    ctx.stopService<AppDetectionService>()
                    ctx.startService(intent)
                } else ctx.stopService<CustomRpcService>()
            },
            shape = RoundedCornerShape(44.dp, 20.dp, 44.dp, 20.dp),
            showSwitch = Prefs[Prefs.LAST_RUN_CUSTOM_RPC, ""].isNotEmpty()
        ), HomeFeatures(
            title = "Console Rpc",
            icon = R.drawable.ic_console_games,
            route = Routes.CONSOLE_RPC,
            isChecked = AppUtils.customRpcRunning(),
            onClick = {
                navigateTo(it)
            },
            onCheckedChange = {
                if (it) {
                    val lastRpc = Prefs[Prefs.LAST_RUN_CONSOLE_RPC, ""]
                    val intent = Intent(ctx, CustomRpcService::class.java)
                    intent.apply {
                        putExtra("RPC", lastRpc)
                    }
                    ctx.stopService<MediaRpcService>()
                    ctx.stopService<ExperimentalRpc>()
                    ctx.stopService<AppDetectionService>()
                    ctx.startService(intent)
                } else ctx.stopService<CustomRpcService>()
            },
            shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
            showSwitch = Prefs[Prefs.LAST_RUN_CONSOLE_RPC, ""].isNotEmpty()
        ),
        HomeFeatures(
            title = "Experimental Rpc",
            icon = R.drawable.ic_dev_rpc,
            isChecked = AppUtils.experimentalRpcRunning(),
            onCheckedChange = {
                if (it) {
                    ctx.startServiceAndStopOthers<ExperimentalRpc>()
                } else ctx.stopService<ExperimentalRpc>()
            },
            shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
            showSwitch = hasUsageAccess.value && hasNotificationAccess.value && userVerified
        ),
        HomeFeatures(
            title = "Coming Soon",
            icon = R.drawable.ic_info,
            shape = RoundedCornerShape(44.dp, 20.dp, 44.dp, 20.dp),
            showSwitch = false
        )
    )
}