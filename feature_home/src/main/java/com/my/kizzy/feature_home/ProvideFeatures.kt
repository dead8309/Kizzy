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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.my.kizzy.feature_rpc_base.AppUtils
import com.my.kizzy.feature_rpc_base.services.AppDetectionService
import com.my.kizzy.feature_rpc_base.services.CustomRpcService
import com.my.kizzy.feature_rpc_base.services.ExperimentalRpc
import com.my.kizzy.feature_rpc_base.services.MediaRpcService
import com.my.kizzy.navigation.Routes
import com.my.kizzy.preference.Prefs
import com.my.kizzy.resources.R

private const val MEDIA_RPC_INVERT_NAME_DETAILS = "media_rpc_invert_name_details"

@Composable
fun provideFeatures(
    navigateTo: (String) -> Unit,
    hasUsageAccess: MutableState<Boolean>,
    hasNotificationAccess: MutableState<Boolean>,
    userVerified: Boolean
): List<HomeFeatures> {
    val ctx = LocalContext.current
    var isInvertNameDetailsEnabled by remember { mutableStateOf(Prefs[MEDIA_RPC_INVERT_NAME_DETAILS, false]) }

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
                    ctx.stopService(Intent(ctx, CustomRpcService::class.java))
                    ctx.stopService(Intent(ctx, ExperimentalRpc::class.java))
                    ctx.stopService(Intent(ctx, MediaRpcService::class.java))
                    ctx.startService(Intent(ctx, AppDetectionService::class.java))
                } else {
                    ctx.stopService(Intent(ctx, AppDetectionService::class.java))
                }
            },
            shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp)
        ),
        HomeFeatures(
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
                    ctx.stopService(Intent(ctx, CustomRpcService::class.java))
                    ctx.stopService(Intent(ctx, ExperimentalRpc::class.java))
                    ctx.stopService(Intent(ctx, AppDetectionService::class.java))
                    ctx.startService(Intent(ctx, MediaRpcService::class.java))
                } else {
                    ctx.stopService(Intent(ctx, MediaRpcService::class.java))
                }
            },
            shape = RoundedCornerShape(44.dp, 20.dp, 44.dp, 20.dp)
        ),
        HomeFeatures(
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
                    ctx.stopService(Intent(ctx, MediaRpcService::class.java))
                    ctx.stopService(Intent(ctx, ExperimentalRpc::class.java))
                    ctx.stopService(Intent(ctx, AppDetectionService::class.java))
                    ctx.startService(intent)
                } else {
                    ctx.stopService(Intent(ctx, CustomRpcService::class.java))
                }
            },
            shape = RoundedCornerShape(44.dp, 20.dp, 44.dp, 20.dp),
            showSwitch = Prefs[Prefs.LAST_RUN_CUSTOM_RPC, ""].isNotEmpty()
        ),
        HomeFeatures(
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
                    ctx.stopService(Intent(ctx, MediaRpcService::class.java))
                    ctx.stopService(Intent(ctx, CustomRpcService::class.java))
                    ctx.stopService(Intent(ctx, AppDetectionService::class.java))
                    ctx.startService(intent)
                } else {
                    ctx.stopService(Intent(ctx, CustomRpcService::class.java))
                }
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
                    ctx.stopService(Intent(ctx, MediaRpcService::class.java))
                    ctx.stopService(Intent(ctx, CustomRpcService::class.java))
                    ctx.stopService(Intent(ctx, AppDetectionService::class.java))
                    ctx.startService(Intent(ctx, ExperimentalRpc::class.java))
                } else {
                    ctx.stopService(Intent(ctx, ExperimentalRpc::class.java))
                }
            },
            shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
            showSwitch = hasUsageAccess.value && hasNotificationAccess.value && userVerified
        ),
        HomeFeatures(
            title = "Invert Name Details",
            icon = R.drawable.ic_invert_name_details,
            isChecked = isInvertNameDetailsEnabled,
            onCheckedChange = {
                isInvertNameDetailsEnabled = !isInvertNameDetailsEnabled
                Prefs[MEDIA_RPC_INVERT_NAME_DETAILS] = isInvertNameDetailsEnabled
            },
            shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp),
            showSwitch = true
        ),
        HomeFeatures(
            title = "Coming Soon",
            icon = R.drawable.ic_info,
            shape = RoundedCornerShape(44.dp, 20.dp, 44.dp, 20.dp),
            showSwitch = false
        )
    )
}