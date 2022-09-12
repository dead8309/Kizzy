package com.my.kizzy.rpc

import android.content.Context
import android.util.ArrayMap
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.my.kizzy.rpc.Constants.APPLICATION
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.net.URISyntaxException
import javax.net.ssl.SSLParameters

class KizzyRPC(
    var token: String,
) {
    private var activity_name: String? = null
    private var details: String? = null
    private var state: String? = null
    private var large_image: String? = null
    private var small_image: String? = null
    private var status:String? = null
    private var start_timestamps: Long? = null
    private var stop_timestamps: Long? = null
    private var type:Int = 0
    var rpc = ArrayMap<String, Any>()
    var webSocketClient: WebSocketClient? = null
    var gson: Gson = GsonBuilder().setPrettyPrinting().serializeNulls().create()
    var heartbeatRunnable: Runnable
    var heartbeatThr: Thread? = null
    var heartbeat_interval = 0
    var seq = 0
    private var session_id: String? = null
    private var reconnect_session = false
    private var buttons = ArrayList<String>()
    private var button_url = ArrayList<String>()


    fun closeRPC() {
        if (heartbeatThr != null) {
            if (!heartbeatThr!!.isInterrupted) heartbeatThr!!.interrupt()
        }
        if (webSocketClient != null) webSocketClient!!.close(1000)
    }

     fun isRpcRunning(): Boolean{
        return webSocketClient?.isOpen == true
    }

    /**
     * Activity Name of Rpc
     *
     * @param activity_name
     * @return
     */
    fun setName(activity_name: String?): KizzyRPC {
        this.activity_name = activity_name
        return this
    }

    /**
     * Details of Rpc
     *
     * @param details
     * @return
     */
    fun setDetails(details: String?): KizzyRPC {
        this.details = details
        return this
    }

    /**
     * Rpc State
     *
     * @param state
     * @return
     */
    fun setState(state: String?): KizzyRPC {
        this.state = state
        return this
    }

    /**
     * Large image on rpc
     * How to get Image ?
     * Upload image to any discord chat and copy its media link it should look like "https://media.discordapp.net/attachments/90202992002/xyz.png" now just use the image link from attachments part
     * so it would look like: .setLargeImage("attachments/90202992002/xyz.png")
     * @param large_image
     * @return
     */
    fun setLargeImage(large_image: String?): KizzyRPC {
        if (large_image != null) {
            if (large_image.startsWith("attachments"))
                this.large_image = "mp:$large_image"
            else
                this.large_image = ImageResolver().resolveImageFromUrl(large_image)
        }
        return this
    }

    fun setLargeImage(large_image: String,context: Context): KizzyRPC {
        this.large_image = ImageResolver().resolveImageOfAppIcon(large_image,context)
        return this
    }


    /**
     * Small image on Rpc
     *
     * @param small_image
     * @return
     */
    fun setSmallImage(small_image: String?): KizzyRPC {
        if (small_image != null) {
            if (small_image.startsWith("attachments"))
                this.small_image = "mp:$small_image"
            else
                this.small_image = ImageResolver().resolveImageFromUrl(small_image)
        }
        return this
    }

    /**
     * start timestamps
     *
     * @param start_timestamps
     * @return
     */
    fun setStartTimestamps(start_timestamps: Long?): KizzyRPC {
        this.start_timestamps = start_timestamps
        return this
    }

    /**
     * stop timestamps
     *
     * @param stop_timestamps
     * @return
     */
    fun setStopTimestamps(stop_timestamps: Long?): KizzyRPC {
        this.stop_timestamps = stop_timestamps
        return this
    }

    /**
     * Activity Types
     * 0: Playing
     * 1: Streaming
     * 2: Listening
     * 3: Watching
     * 5: Competing
     *
     * @param type
     * @return
     */

    fun setType(type: Int): KizzyRPC {
        if (type in 0..5)
            this.type = type
        else this.type = 0
        return this
    }

    /* Status type for profile online,idle,dnd
    *
    * @param status
    * @return
    */
    fun setStatus(status: String?): KizzyRPC {
        this.status = status
        return this
    }

    /**
     * Button1 text
     * @param button1_Text
     * @return
     */
    fun setButton1(button1_Text: String?): KizzyRPC {
        button1_Text?.let { buttons.add(it) }
        return this
    }

    /**
     * Button2 text
     * @param button2_text
     * @return
     */
    fun setButton2(button2_text: String?): KizzyRPC {
        button2_text?.let { buttons.add(it) }
        return this
    }

    /**
     * Button1 url
     * @param url
     * @return
     */
    fun setButton1URL(url: String?): KizzyRPC {
        url?.let { button_url.add(it) }
        return this
    }

    /**
     * Button2 url
     * @param url
     * @return
     */
    fun setButton2URL(url: String?): KizzyRPC {
        url?.let { button_url.add(it) }
        return this
    }

    fun build() {
        val presence = ArrayMap<String, Any?>()
        val activity = ArrayMap<String, Any?>()
        activity[Constants.NAME] = activity_name
        activity[Constants.DETAILS] = details
        activity[Constants.STATE] = state
        activity[Constants.TYPE] = type
        val timestamps = ArrayMap<String, Any?>()
        timestamps[Constants.START_TIMESTAMPS] = start_timestamps
        timestamps[Constants.STOP_TIMESTAMPS] = stop_timestamps
        activity[Constants.TIMESTAMPS] = timestamps
        val assets = ArrayMap<String, Any?>()
        assets[Constants.LARGE_IMAGE] = large_image
        assets[Constants.SMALL_IMAGE] = small_image
        activity[Constants.ASSETS] = assets
        if (buttons.size > 0) {
            activity[APPLICATION] = Constants.APPLICATION_ID
            activity[Constants.BUTTONS] = buttons
            val metadata = ArrayMap<String, Any>()
            metadata[Constants.BUTTON_LINK] = button_url
            activity[Constants.METADATA] = metadata
        }
        presence[Constants.ACTIVITIES] = arrayOf<Any>(activity)
        presence[Constants.AFK] = true
        presence[Constants.SINCE] = start_timestamps
        presence[Constants.STATUS] = status
        rpc["op"] = 3
        rpc["d"] = presence
        createWebsocketClient()
    }

    fun sendIdentify() {
        val prop = ArrayMap<String, Any>()
        prop["\$os"] = "windows"
        prop["\$browser"] = "Chrome"
        prop["\$device"] = "disco"
        val data = ArrayMap<String, Any>()
        data["token"] = token
        data["properties"] = prop
        data["compress"] = false
        data["intents"] = 0
        val identify = ArrayMap<String, Any>()
        identify["op"] = 2
        identify["d"] = data
        webSocketClient!!.send(gson.toJson(identify))
    }

    private fun createWebsocketClient() {
        Log.i("Connecting", "")
        val uri: URI = try {
            URI("wss://gateway.discord.gg/?encoding=json&v=10")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            return
        }
        val headerMap = ArrayMap<String, String>()
        webSocketClient = object : WebSocketClient(uri, headerMap) {
            override fun onOpen(s: ServerHandshake) {
                Log.e("Connection opened", "")
            }

            override fun onMessage(message: String) {
                val map = gson.fromJson<ArrayMap<String, Any>>(
                    message, object : TypeToken<ArrayMap<String?, Any?>?>() {}.type
                )
                val o = map["s"]
                if (o != null) {
                    seq = (o as Double).toInt()
                }
                when ((map["op"] as Double?)!!.toInt()) {
                    0 -> if (map["t"] as String? == "READY") {
                        session_id = (map["d"] as Map<*, *>?)!!["session_id"].toString()
                        Log.e("Connected", "")
                        webSocketClient!!.send(gson.toJson(rpc))
                        return
                    }
                    10 -> if (!reconnect_session) {
                        val data = map["d"] as Map<*, *>?
                        heartbeat_interval = (data!!["heartbeat_interval"] as Double?)!!.toInt()
                        heartbeatThr = Thread(heartbeatRunnable)
                        heartbeatThr!!.start()
                        sendIdentify()
                    } else {
                        Log.e("Sending Reconnect", "")
                        val data = map["d"] as Map<*, *>?
                        heartbeat_interval = (data!!["heartbeat_interval"] as Double?)!!.toInt()
                        heartbeatThr = Thread(heartbeatRunnable)
                        heartbeatThr!!.start()
                        reconnect_session = false
                        webSocketClient!!.send("{\"op\": 6,\"d\":{\"token\":\"$token\",\"session_id\":\"$session_id\",\"seq\":$seq}}")
                    }
                    1 -> {
                        if (!Thread.interrupted()) {
                            heartbeatThr!!.interrupt()
                        }
                        webSocketClient!!.send(
                            "{\"op\":1, \"d\":" + (if (seq == 0) "null" else seq.toString()) + "}"
                        )
                    }
                    11 -> {
                        if (!Thread.interrupted()) {
                            heartbeatThr!!.interrupt()
                        }
                        heartbeatThr = Thread(heartbeatRunnable)
                        heartbeatThr!!.start()
                    }
                    7 -> {
                        reconnect_session = true
                        webSocketClient!!.close(4000)
                    }
                    9 -> if (!heartbeatThr!!.isInterrupted) {
                        heartbeatThr!!.interrupt()
                        heartbeatThr = Thread(heartbeatRunnable)
                        heartbeatThr!!.start()
                        sendIdentify()
                    }
                }
            }

            override fun onClose(code: Int, reason: String, remote: Boolean) {
                if (code == 4000) {
                    reconnect_session = true
                    heartbeatThr!!.interrupt()
                    Log.e("", "Closed Socket")
                    val newTh = Thread {
                        try {
                            Thread.sleep(200)
                            reconnect()
                        } catch (e: InterruptedException) {
                        }
                    }
                    newTh.start()
                } else throw RuntimeException("Invalid")
            }

            override fun onError(e: Exception) {
                if (e.message != "Interrupt") {
                    closeRPC()
                }
            }

            override fun onSetSSLParameters(p: SSLParameters) {
                try {
                    super.onSetSSLParameters(p)
                } catch (th: Throwable) {
                    th.printStackTrace()
                }
            }
        }
        (webSocketClient as WebSocketClient).connect()
    }

    fun updateRPC(
        name: String,
        details: String?,
        state: String?,
        large_image: String?,
        enableTimestamps: Boolean,
        time: Long
    ) {
        if (!isRpcRunning()) return
        val presence = ArrayMap<String, Any>()
        val activity = ArrayMap<String, Any>()
        activity["name"] = name
        activity["details"] = details
        activity["state"] = state
        activity["type"] = 0
        if (enableTimestamps) {
            val timestamps = ArrayMap<String, Any>()
            timestamps["start"] = time
            activity["timestamps"] = timestamps
        }
        large_image?.let {
            val assets = ArrayMap<String, String>()
            assets[Constants.LARGE_IMAGE] = large_image
            activity[Constants.ASSETS] = assets
        }

        presence["activities"] = arrayOf<Any>(activity)
        presence["afk"] = true
        presence["since"] = time
        presence["status"] = "dnd"
        val arr = ArrayMap<String, Any>()
        arr["op"] = 3
        arr["d"] = presence
        webSocketClient?.send(gson.toJson(arr))
    }

    init {
        heartbeatRunnable = Runnable {
            try {
                if (heartbeat_interval < 10000) throw RuntimeException("invalid")
                Thread.sleep(heartbeat_interval.toLong())
                webSocketClient!!.send(
                    "{\"op\":1, \"d\":" + (if (seq == 0) "null" else seq.toString()) + "}"
                )
            } catch (e: InterruptedException) {
            }
        }
    }
}