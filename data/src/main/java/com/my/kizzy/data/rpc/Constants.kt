/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Constants.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.rpc

import com.my.kizzy.resources.R

object Constants {
    const val NINTENDO_LINK =
        "https://media.discordapp.net/attachments/948828217312178227/1018855932496719932/default.png"
    const val WII_U_LINK =
        "https://media.discordapp.net/attachments/948828217312178227/1020010414576255017/default.png"
    const val XBOX_LINK =
        "https://media.discordapp.net/attachments/1009061802593763398/1049675792952598599/1670332610733.png"
    const val N3DS_LINK =
        "https://media.discordapp.net/attachments/948828217312178227/1040885415864967279/3ds.png"
    const val NINTENDO = "Nintendo Switch"
    const val NINTENDO_3DS = "Nintendo-3DS"
    const val WII_U = "Wii-U"
    const val XBOX = "Xbox"
    const val APPLICATION_ID = "962990036020756480"
    const val IMGUR_CLIENT_ID = "d70305e7c3ac5c6"
    const val APP_DIRECTORY = "App Directory"
    const val DOWNLOADS_DIRECTORY = "Downloads Directory"
    const val MAX_ALLOWED_CHARACTER_LENGTH = 32
    /*
    See https://discord.com/developers/docs/reference#snowflakes
    */
    val MAX_APPLICATION_ID_LENGTH_RANGE = 18..19

    val ACTIVITY_TYPE = mapOf(
        "Playing" to 0,
        "Streaming" to 1,
        "Listening" to 2,
        "Watching" to 3,
        "Competing" to 5
    )
    val ACTIVITY_STATUS = mapOf(
        R.string.status_online to "online",
        R.string.status_idle to "idle",
        R.string.status_dnd to "dnd",
        R.string.status_offline to "offline",
        R.string.status_invisible_offline to "invisible"
    )

    val ACTIVITY_PLATFORMS = mapOf(
        "Android" to "android",
        "Desktop" to "desktop",
        "Embedded" to "embedded",
        "IOS" to "ios",
        "PlayStation 4" to "ps4",
        "PlayStation 5" to "ps5",
        "Samsung" to "samsung",
        "Xbox" to "xbox",
    )
}