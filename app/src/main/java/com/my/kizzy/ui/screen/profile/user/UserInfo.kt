package com.my.kizzy.ui.screen.profile.user

import android.util.ArrayMap
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.my.kizzy.utils.Prefs
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.net.URISyntaxException
import javax.net.ssl.SSLParameters

data class UserData(
    val name: String,
    val username: String,
    val avatar: String,
    val about: String,
)
private lateinit var webSocketClient: WebSocketClient

fun getUserInfo(token: String,onInfoSaved: () -> Unit){
    val uri: URI = try {
        URI("wss://gateway.discord.gg/?encoding=json&v=10")
    } catch (e: URISyntaxException) {
        e.printStackTrace()
        return
    }
    val headerMap = ArrayMap<String, String>()

    webSocketClient = object : WebSocketClient(uri, headerMap) {
        override fun onOpen(s: ServerHandshake) {
        }

        override fun onMessage(message: String) {
            val map = Gson().fromJson<ArrayMap<String, Any>>(
                message, object : TypeToken<ArrayMap<String?, Any?>?>() {}.type
            )
            when ((map["op"] as Double?)!!.toInt()) {
                0 -> if (map["t"] as String? == "READY") {
                    val data = (map["d"] as Map<*, *>?)!!["user"] as Map<*, *>?
                    val userData = UserData(
                        name = data!!["username"].toString(),
                        username = data["username"].toString() + "#" + data["discriminator"],
                        avatar = "https://cdn.discordapp.com/avatars/${data["id"]}/${data["avatar"]}.png",
                        about = data["bio"].toString() + ""
                    )

                    Prefs[Prefs.USER_DATA] = Gson().toJson(userData)
                    onInfoSaved()
                    return
                }
                10 -> {
                        val prop = ArrayMap<String, Any>()
                        prop["\$os"] = "linux"
                        prop["\$browser"] = "Discord Android"
                        prop["\$device"] = "unknown"
                        val data = ArrayMap<String, Any?>()
                        data["token"] = token
                        data["properties"] = prop
                        data["compress"] = false
                        data["intents"] = 0
                        val arr = ArrayMap<String, Any>()
                        arr["op"] = 2
                        arr["d"] = data
                        webSocketClient.send(Gson().toJson(arr))
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