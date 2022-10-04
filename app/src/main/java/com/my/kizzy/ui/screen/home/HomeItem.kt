package com.my.kizzy.ui.screen.home

import androidx.annotation.DrawableRes
import com.my.kizzy.R
import com.my.kizzy.ui.common.Routes

data class HomeItem(
    val title: String,
    @DrawableRes val icon: Int,
    val route: String?,
)

fun getHomeitems() = listOf(
    HomeItem(
        title = "App Detection",
        icon = R.drawable.ic_apps,
        route = Routes.APPS_DETECTION,
    ),
    HomeItem(
        title = "Media Rpc",
        icon = R.drawable.ic_media_rpc,
        route = Routes.MEDIA_RPC,
    ),
    HomeItem(
        title = "Custom Rpc",
        icon = R.drawable.ic_rpc_placeholder,
        route = Routes.CUSTOM_RPC,
        ),
    HomeItem(
        title = "Nintendo Rpc",
        icon = R.drawable.ic_nintendo_switch,
        route = Routes.NINTENDO_RPC,
    ),
    HomeItem(
        title = "Soon",
        icon = R.drawable.ic_info,
        route = null,
    )
)