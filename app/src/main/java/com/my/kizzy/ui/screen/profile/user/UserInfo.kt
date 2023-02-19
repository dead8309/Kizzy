package com.my.kizzy.ui.screen.profile.user

import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.USER_BIO
import com.my.kizzy.utils.Prefs.USER_ID
import com.my.kizzy.utils.Prefs.USER_NITRO
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

