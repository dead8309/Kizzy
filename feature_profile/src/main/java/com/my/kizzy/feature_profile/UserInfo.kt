/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * UserInfo.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_profile

import com.my.kizzy.preference.Prefs
import com.my.kizzy.preference.Prefs.USER_BIO
import com.my.kizzy.preference.Prefs.USER_ID
import com.my.kizzy.preference.Prefs.USER_NITRO
import kizzy.gateway.DiscordWebSocket
import kizzy.gateway.DiscordWebSocketImpl
import kizzy.gateway.entities.Payload

suspend fun getUserInfo(token: String, onInfoSaved: () -> Unit) {
    val discordWebSocket: DiscordWebSocket = object: DiscordWebSocketImpl(token){
        override fun Payload.handleDispatch() {
            if (this.t.toString() == "READY"){
                val data = (this.d as Map<*, *>?)!!["user"] as Map<*, *>?
                Prefs[USER_ID] = data!!["id"]
                Prefs[USER_BIO] = data["bio"]
                Prefs[USER_NITRO] = data["premium_type"] in 1..3
                close()
                onInfoSaved()
            }
        }
    }
    discordWebSocket.connect()
}

