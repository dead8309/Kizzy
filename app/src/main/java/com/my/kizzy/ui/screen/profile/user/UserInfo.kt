package com.my.kizzy.ui.screen.profile.user

import android.util.ArrayMap
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.my.kizzy.rpc.model.Data
import com.my.kizzy.rpc.model.Identify
import com.my.kizzy.rpc.model.Properties
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.USER_BIO
import com.my.kizzy.utils.Prefs.USER_ID
import com.my.kizzy.utils.Prefs.USER_NITRO
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.net.URISyntaxException
import javax.net.ssl.SSLParameters

fun getUserInfo(token: String,onInfoSaved: () -> Unit){
    val gson: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()
    val uri: URI = try {
        URI("wss://gateway.discord.gg/?encoding=json&v=10")
    } catch (e: URISyntaxException) {
        e.printStackTrace()
        return
    }
    val headerMap = ArrayMap<String, String>()

    val webSocketClient = object : WebSocketClient(uri, headerMap) {
        override fun onOpen(s: ServerHandshake) {
        }

        override fun onMessage(message: String) {
            val map = gson.fromJson<ArrayMap<String, Any>>(
                message, object : TypeToken<ArrayMap<String?, Any?>?>() {}.type
            )
            when ((map["op"] as Double?)!!.toInt()) {
                0 -> if (map["t"] as String? == "READY") {
                    val data = (map["d"] as Map<*, *>?)!!["user"] as Map<*, *>?
                    Prefs[USER_ID] = data!!["id"]
                    Prefs[USER_BIO] = data["bio"]
                    Prefs[USER_NITRO] = data["premium_type"] in 1..3
                    onInfoSaved()
                    return
                }
                10 -> {
                    send(gson.toJson(
                        Identify(
                            op = 2,
                            d = Data(
                                capabilities = 65,
                                compress = false,
                                largeThreshold = 100,
                                properties = Properties(
                                    browser = "Discord Client",
                                    device = "disco",
                                    os = "Windows"
                                ),
                                token = token
                            )
                        )
                    )
                    )
                }
            }
        }

        override fun onClose(code: Int, reason: String, remote: Boolean) {
        }

        override fun onError(e: Exception) {
        }

        override fun onSetSSLParameters(p: SSLParameters) {
            try {
                super.onSetSSLParameters(p)
            } catch (th: Throwable) {
                th.printStackTrace()
            }
        }
    }
    webSocketClient.connect()
}