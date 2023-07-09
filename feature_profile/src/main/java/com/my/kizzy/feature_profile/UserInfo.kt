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

import com.my.kizzy.domain.model.user.User
import com.my.kizzy.preference.Prefs
import com.my.kizzy.preference.Prefs.USER_BIO
import com.my.kizzy.preference.Prefs.USER_ID
import com.my.kizzy.preference.Prefs.USER_NITRO
import kizzy.gateway.DiscordWebSocket
import kizzy.gateway.DiscordWebSocketImpl
import kizzy.gateway.entities.Payload
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private val json = Json { ignoreUnknownKeys = true }
suspend fun getUserInfo(token: String, onInfoSaved: () -> Unit) {
    val discordWebSocket: DiscordWebSocket = object: DiscordWebSocketImpl(token){
        override fun Payload.handleDispatch() {
            if (this.t.toString() == "READY"){
                val data = this.d!!.jsonObject["user"] as JsonObject?
                val user = json.decodeFromString<User>(data.toString())
                Prefs[USER_ID] = user.id
                Prefs[USER_BIO] = user.bio
                Prefs[USER_NITRO] = data!!["premium_type"]!!.jsonPrimitive.intOrNull in 1..3
                close()
                onInfoSaved()
            }
        }
    }
    discordWebSocket.connect()
}

