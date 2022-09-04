package com.my.kizzy.ui.screen.home

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.my.kizzy.R
import com.my.kizzy.ui.common.Routes
import com.my.kizzy.ui.theme.LightBlueIcon
import com.my.kizzy.ui.theme.LightGreenIcon
import com.my.kizzy.ui.theme.LightPurpleIcon
import com.my.kizzy.ui.theme.LightRedIcon

data class HomeItem(
    val title: String,
    @DrawableRes val icon: Int,
    val iconColor: Color,
    val route: String?,
)

fun getHomeitems() = listOf(
    HomeItem(
        title = "App Detection",
        icon = R.drawable.ic_apps,
        iconColor = LightBlueIcon,
        route = Routes.APPS_DETECTION,
    ),
    HomeItem(
        title = "Media Rpc",
        icon = R.drawable.ic_media_rpc,
        iconColor = LightGreenIcon,
        route = Routes.MEDIA_RPC,
    ),
    HomeItem(
        title = "Custom Rpc",
        icon = R.drawable.ic_rpc_placeholder,
        iconColor = LightRedIcon,
        route = Routes.CUSTOM_RPC,
        ),
    HomeItem(
        title = "Rpc Settings",
        icon = R.drawable.ic_rpc_settings,
        iconColor = LightPurpleIcon,
        route = Routes.RPC_SETTINGS,
    ),
    HomeItem(
        title = "Soon",
        icon = R.drawable.ic_info,
        iconColor = Color.White,
        route = null,
    )
)