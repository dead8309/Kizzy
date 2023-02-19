package kizzy.gateway

import kizzy.gateway.entities.LogLevel
import kizzy.gateway.entities.presence.Presence
import kotlinx.coroutines.CoroutineScope


sealed interface DiscordWebSocket: CoroutineScope {
    suspend fun connect()
    suspend fun sendActivity(presence: Presence)
    fun isWebSocketConnected(): Boolean
    fun log(message: Any?,logLevel: LogLevel = LogLevel.INFO)
    fun close()
}