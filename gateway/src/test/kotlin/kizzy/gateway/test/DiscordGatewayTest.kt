/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * DiscordGatewayTest.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package kizzy.gateway.test

import com.my.kizzy.domain.interfaces.Logger
import kizzy.gateway.DiscordWebSocketImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

class DiscordGatewayTest {
    @Test
    fun `Check Gateway Connection`() = runBlocking {
        val logger = object: Logger  {
            override fun clear() {

            }
            override fun i(tag: String, event: String) {
                println("[$tag]\t\t\t: $event")
            }
            override fun e(tag: String, event: String) {
                println("[$tag]\t\t\t: $event")
            }
            override fun d(tag: String, event: String) {
                println("[$tag]\t\t\t: $event")
            }
            override fun w(tag: String, event: String) {
                println("[$tag]\t\t\t: $event")
            }

        }
        val gateway = DiscordWebSocketImpl(System.getenv("DISCORD_TOKEN"),logger)
        gateway.connect()
        delay(2.seconds)
        assert(gateway.isWebSocketConnected())
    }
}