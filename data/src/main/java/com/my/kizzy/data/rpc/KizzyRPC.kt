package com.my.kizzy.data.rpc

import com.my.kizzy.domain.interfaces.Logger
import com.my.kizzy.domain.repository.KizzyRepository
import com.my.kizzy.preference.Prefs
import com.my.kizzy.preference.Prefs.CUSTOM_ACTIVITY_TYPE
import kizzy.gateway.DiscordWebSocket
import kizzy.gateway.entities.presence.Activity
import kizzy.gateway.entities.presence.Assets
import kizzy.gateway.entities.presence.Metadata
import kizzy.gateway.entities.presence.Presence
import kizzy.gateway.entities.presence.Timestamps
import kotlinx.coroutines.isActive
import com.my.kizzy.preference.Prefs.MEDIA_RPC_INVERT_NAME_DETAILS

class KizzyRPC(
    private val token: String,
    private val kizzyRepository: KizzyRepository,
    private val discordWebSocket: DiscordWebSocket,
    private val logger: Logger
) {
    private lateinit var presence: Presence
    private var activityName: String? = null
    private var details: String? = null
    private var state: String? = null
    private var largeImage: RpcImage? = null
    private var smallImage: RpcImage? = null
    private var largeText: String? = null
    private var smallText: String? = null
    private var status: String? = null
    private var startTimestamps: Long? = null
    private var stopTimestamps: Long? = null
    private var type: Int = 0
    private var buttons = ArrayList<String>()
    private var buttonUrl = ArrayList<String>()
    private var url: String? = null
    private var isToggleEnabled: Boolean = Prefs[MEDIA_RPC_INVERT_NAME_DETAILS, false] // Use the preference value

    fun closeRPC() {
        discordWebSocket.close()
    }

    fun isRpcRunning(): Boolean {
        return discordWebSocket.isWebSocketConnected()
    }

    /**
     * Use Regex to check if token is valid
     * @return true if token is valid else false
     *
     * source: [#token-structure](https://gist.github.com/aydynx/5d29e903417354fd25641b98efc9d437#token-structure)
     */
    private fun isUserTokenValid(): Boolean {
        /*val regex = Regex(
            "[a-z\\d]{24}\\.[a-z\\d]{6}\\.([\\w-]{107}|[\\w-]{38}|[\\w-]{27})|mfa\\.[\\w-]{84}",
            RegexOption.IGNORE_CASE
        )
        return regex.matches(token)*/
        return token.isNotBlank()
    }

    /**
     * Activity Name of Rpc
     *
     * @param activity_name
     * @return
     */
    fun setName(activity_name: String?): KizzyRPC {
        this.activityName = activity_name
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
    fun setLargeImage(large_image: RpcImage?, large_text: String? = null): KizzyRPC {
        this.largeText = large_text.takeIf { !it.isNullOrEmpty() }
        this.largeImage = large_image
        return this
    }

    /**
     * Small image on Rpc
     *
     * @param small_image
     * @return
     */
    fun setSmallImage(small_image: RpcImage?, small_text: String? = null): KizzyRPC {
        this.smallText = small_text.takeIf { !it.isNullOrEmpty() }
        this.smallImage = small_image
        return this
    }

    /**
     * start timestamps
     *
     * @param start_timestamps
     * @return
     */
    fun setStartTimestamps(start_timestamps: Long?): KizzyRPC {
        this.startTimestamps = start_timestamps
        return this
    }

    /**
     * stop timestamps
     *
     * @param stop_timestamps
     * @return
     */
    fun setStopTimestamps(stop_timestamps: Long?): KizzyRPC {
        this.stopTimestamps = stop_timestamps
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

    /** Status type for profile online,idle,dnd
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
        url?.let { buttonUrl.add(it) }
        return this
    }

    /**
     * Button2 url
     * @param url
     * @return
     */
    fun setButton2URL(url: String?): KizzyRPC {
        url?.let { buttonUrl.add(it) }
        return this
    }
    /**
     * Streaming Url
     * @param url The streaming type currently only supports Twitch and YouTube.
     * Only https://twitch.tv/ and https://youtube.com/ urls will work
     */
    fun setStreamUrl(url: String?): KizzyRPC {
        this.url = url
        return this
    }

    suspend fun build() {
        presence = Presence(
            activities = listOf(
                Activity(
                    name = if (isToggleEnabled) details else activityName,
                    state = state,
                    details = if (isToggleEnabled) activityName else details,
                    type = type,
                    timestamps = Timestamps(
                        start = startTimestamps,
                        end = stopTimestamps
                    ).takeIf { startTimestamps != null || stopTimestamps != null },
                    assets = Assets(
                        largeImage = largeImage?.resolveImage(kizzyRepository),
                        smallImage = smallImage?.resolveImage(kizzyRepository),
                        largeText = largeText,
                        smallText = smallText
                    ).takeIf { largeImage != null || smallImage != null },
                    buttons = buttons.takeIf { buttons.isNotEmpty() },
                    metadata = Metadata(buttonUrls = buttonUrl).takeIf { buttonUrl.isNotEmpty() },
                    applicationId = Constants.APPLICATION_ID.takeIf { buttons.isNotEmpty() },
                    url = url
                )
            ),
            afk = true,
            since = startTimestamps ?: System.currentTimeMillis(),
            status = status
        )

        connectToWebSocket()
    }

    private suspend fun connectToWebSocket() {
        if (!isUserTokenValid()) {
            logger.e(
                tag = "KizzyRPC",
                event = "Token Seems to be invalid, Please Login if you haven't"
            )
        } else {
            discordWebSocket.connect()
            sendActivity(presence)
        }
    }

    private suspend fun sendActivity(presence: Presence) {
        if (discordWebSocket.isActive) {
            discordWebSocket.sendActivity(presence)
        }
    }

    suspend fun updateRPC(
        name: String,
        details: String?,
        state: String?,
        large_image: RpcImage?,
        small_image: RpcImage?,
        enableTimestamps: Boolean,
        time: Long
    ) {
        val updatedPresence = Presence(
            activities = listOf(
                Activity(
                    name = if (isToggleEnabled) details else name,
                    details = if (isToggleEnabled) name else details,
                    state = state,
                    type = Prefs[CUSTOM_ACTIVITY_TYPE, 0],
                    timestamps = Timestamps(start = time).takeIf { enableTimestamps },
                    assets = Assets(
                        largeImage = large_image?.resolveImage(kizzyRepository),
                        smallImage = small_image?.resolveImage(kizzyRepository)
                    ).takeIf { large_image != null || small_image != null },
                    buttons = buttons.takeIf { buttons.isNotEmpty() },
                    metadata = Metadata(buttonUrls = buttonUrl).takeIf { buttonUrl.isNotEmpty() },
                    applicationId = Constants.APPLICATION_ID.takeIf { buttons.isNotEmpty() }
                )
            ),
            afk = true,
            since = time,
            status = Constants.DND
        )
        sendActivity(updatedPresence)
    }

    suspend fun updateRPC(commonRpc: CommonRpc) {
        if (!discordWebSocket.isActive) return
        var time = Timestamps(start = startTimestamps)
        if (commonRpc.time != null) {
            Timestamps(end = commonRpc.time.end, start = commonRpc.time.start).also { time = it }
        }
        val updatedPresence = Presence(
            activities = listOf(
                Activity(
                    name = if (isToggleEnabled) commonRpc.details else commonRpc.name?.takeIf { it.isNotEmpty() },
                    details = if (isToggleEnabled) commonRpc.name?.takeIf { it.isNotEmpty() } else commonRpc.details,
                    state = commonRpc.state?.takeIf { it.isNotEmpty() },
                    type = Prefs[CUSTOM_ACTIVITY_TYPE, 0],
                    timestamps = time,
                    assets = Assets(
                        largeImage = commonRpc.largeImage?.resolveImage(kizzyRepository),
                        smallImage = commonRpc.smallImage?.resolveImage(kizzyRepository)
                    ).takeIf { commonRpc.largeImage != null || commonRpc.smallImage != null },
                    buttons = buttons.takeIf { buttons.isNotEmpty() },
                    metadata = Metadata(buttonUrls = buttonUrl).takeIf { buttonUrl.isNotEmpty() },
                    applicationId = Constants.APPLICATION_ID.takeIf { buttons.isNotEmpty() }
                )
            ),
            afk = true,
            since = startTimestamps ?: 0L,
            status = Constants.DND
        )
        sendActivity(updatedPresence)
    }

}
