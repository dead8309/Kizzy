package com.my.kizzy.ui.screen.home

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.my.kizzy.R
import com.my.kizzy.ui.common.Routes
import com.my.kizzy.ui.theme.*

data class HomeItem(
    val title: String,
    @DrawableRes val icon: Int,
    val bgColor: Color,
    val iconColor: Color,
    val route: String,
    val lightColor: Color,
    val mediumColor: Color
)

fun getHomeitems() = listOf(
    HomeItem(
        title = "App Detection",
        icon = R.drawable.ic_apps,
        bgColor = LightBlueBg,
        iconColor =
        LightBlueIcon,
        route = Routes.APPS_DETECTION,
        Blue1,
        Blue2,
    ),
    HomeItem(
        title = "Media Rpc",
        icon = R.drawable.ic_media_rpc,
        bgColor = LightGreenBg,
        iconColor = LightGreenIcon,
        route = Routes.MEDIA_RPC,
        Green1,
        Green2,
    ),
    HomeItem(
        title = "Custom Rpc",
        icon = R.drawable.ic_rpc_placeholder,
        bgColor = LightRedBg,
        iconColor = LightRedIcon,
        route = Routes.CUSTOM_RPC,
        Red1,
        Red2,
    ),
    HomeItem(
        title = "Rpc Settings",
        icon = R.drawable.ic_custom_rpc,
        bgColor = LightPurpleBg,
        iconColor = LightPurpleIcon,
        route = Routes.RPC_SETTINGS,
        Purple1,
        Purple2
    )
)
