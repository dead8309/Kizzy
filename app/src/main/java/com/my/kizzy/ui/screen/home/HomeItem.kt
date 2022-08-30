package com.my.kizzy.ui.screen.home

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.my.kizzy.R
import com.my.kizzy.ui.common.Routes
import com.my.kizzy.ui.theme.*

data class HomeItem(
    val title: String,
    @DrawableRes val icon: Int,
    val bgColor: List<Color>,
    val iconColor: List<Color>,
    val route: String
)

fun getHomeitems() = listOf(
    HomeItem(
        title = "App Detection",
        icon = R.drawable.ic_apps,
        bgColor = listOf(
            DarkBlueBg, LightBlueBg
        ),
        iconColor = listOf(
            DarkBlueIcon,
            LightBlueIcon
        ),
        route = Routes.APPS_DETECTION
    ),
    HomeItem(
        title = "Media Rpc",
        icon = R.drawable.ic_media_rpc,
        bgColor = listOf(
            DarkGreenBg,
            LightGreenBg
        ),
        iconColor = listOf(
            DarkGreenIcon,
            LightGreenIcon
        ),
        route = Routes.MEDIA_RPC
    ),
    HomeItem(
        title = "Custom Rpc",
        icon = R.drawable.ic_custom_rpc,
        bgColor = listOf(
            DarkRedBg,
            LightRedBg
        ),
        iconColor = listOf(
            DarkRedIcon,
            LightRedIcon
        ),
        route = Routes.CUSTOM_RPC
    )

)
