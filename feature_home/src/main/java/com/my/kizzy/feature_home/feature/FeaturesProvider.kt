/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * FeaturesProvider.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_home.feature

import android.content.Intent
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.my.kizzy.feature_rpc_base.AppUtils
import com.my.kizzy.feature_rpc_base.services.AppDetectionService
import com.my.kizzy.feature_rpc_base.services.CustomRpcService
import com.my.kizzy.feature_rpc_base.services.ExperimentalRpc
import com.my.kizzy.feature_rpc_base.services.MediaRpcService
import com.my.kizzy.navigation.Routes
import com.my.kizzy.preference.Prefs
import com.my.kizzy.resources.R

@Composable
fun homeFeaturesProvider(
    navigateTo: (String) -> Unit,
    hasUsageAccess: MutableState<Boolean>,
    hasNotificationAccess: MutableState<Boolean>,
    userVerified: Boolean,
): List<HomeFeature> {
    val ctx = LocalContext.current
    return listOf(
        HomeFeature(
            title = stringResource(id = R.string.main_appDetection),
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
                } else
                    ctx.stopService(Intent(ctx, AppDetectionService::class.java))
            },
            shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
            tooltipText = stringResource(id = R.string.main_appDetection_details),
            featureDocsLink = ToolTipContent.APP_DETECTION_DOCS_LINK
        ), HomeFeature(
            title = stringResource(id = R.string.main_mediaRpc),
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
                } else
                    ctx.stopService(Intent(ctx, MediaRpcService::class.java))
            },
            shape = RoundedCornerShape(44.dp, 20.dp, 44.dp, 20.dp),
            tooltipText = stringResource(id = R.string.main_mediaRpc_details),
            featureDocsLink = ToolTipContent.MEDIA_RPC_DOCS_LINK
        ), HomeFeature(
            title = stringResource(id = R.string.main_customRpc),
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
                } else
                    ctx.stopService(Intent(ctx, CustomRpcService::class.java))
            },
            shape = RoundedCornerShape(44.dp, 20.dp, 44.dp, 20.dp),
            showSwitch = Prefs[Prefs.LAST_RUN_CUSTOM_RPC, ""].isNotEmpty(),
            tooltipText = stringResource(id = R.string.main_customRpc_details),
            featureDocsLink = ToolTipContent.CUSTOM_RPC_DOCS_LINK
        ), HomeFeature(
            title = stringResource(id = R.string.main_consoleRpc),
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
                    ctx.stopService(Intent(ctx, ExperimentalRpc::class.java))
                    ctx.stopService(Intent(ctx, AppDetectionService::class.java))
                    ctx.startService(intent)
                } else
                    ctx.stopService(Intent(ctx, CustomRpcService::class.java))
            },
            shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
            showSwitch = Prefs[Prefs.LAST_RUN_CONSOLE_RPC, ""].isNotEmpty(),
            tooltipText = stringResource(id = R.string.main_consoleRpc_details),
            featureDocsLink = ToolTipContent.CONSOLE_RPC_DOCS_LINK
        ),
        HomeFeature(
            title = stringResource(id = R.string.main_experimentalRpc),
            icon = R.drawable.ic_dev_rpc,
            route = Routes.EXPERIMENTAL_RPC,
            onClick = { navigateTo(it) },
            isChecked = AppUtils.experimentalRpcRunning(),
            onCheckedChange = {
                if (it) {
                    ctx.stopService(Intent(ctx, MediaRpcService::class.java))
                    ctx.stopService(Intent(ctx, CustomRpcService::class.java))
                    ctx.stopService(Intent(ctx, AppDetectionService::class.java))
                    ctx.startService(Intent(ctx, ExperimentalRpc::class.java))
                } else
                    ctx.stopService(Intent(ctx, ExperimentalRpc::class.java))
            },
            shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
            showSwitch = hasUsageAccess.value && hasNotificationAccess.value && userVerified,
            tooltipText = stringResource(id = R.string.main_experimentalRpc_details),
            featureDocsLink = ToolTipContent.EXPERIMENTAL_RPC_DOCS_LINK
        ),
        HomeFeature(
            title = stringResource(id = R.string.main_comingSoon),
            icon = R.drawable.ic_info,
            shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
            showSwitch = false
        )
    )
}
