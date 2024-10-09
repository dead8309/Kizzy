/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ApiService.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */
package com.my.kizzy.data.remote

import com.my.kizzy.domain.model.samsung_rpc.GalaxyPresence
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import java.io.File
import javax.inject.Inject

class ApiService @Inject constructor(
    private val client: HttpClient,
    @Base private val baseUrl: String,
    @External private val externalBaseUrl: String,
    @Discord private val discordBaseUrl: String,
    @Github private val githubBaseUrl: String,
) {
    suspend fun getImage(url: String) = client.get {
        url(externalBaseUrl)
        parameter("url", url)
    }

    suspend fun uploadImage(file: File) = client.post {
        url("https://api.imgur.com/3/image")
        headers {
            // Imgur web client API key, unchanged for at least >5 years as of 2024
            append(HttpHeaders.Authorization, "Client-ID 546c25a59c58ad7")
        }
        contentType(ContentType.MultiPart.FormData)
        setBody(
            MultiPartFormDataContent(
                formData {
                    append("image", file.readBytes())
                    append("type", "raw")
                }
            )
        )
    }

    suspend fun getGames() = client.get {
        url("$baseUrl/games")
    }

    suspend fun getUser(userid: String) = client.get {
        url("$baseUrl/user/$userid")
    }

    suspend fun getContributors() = client.get {
        url("$baseUrl/contributors")
    }

    suspend fun setSamsungGalaxyPresence(galaxyPresence: GalaxyPresence,token: String) {
        client.post {
            url("$discordBaseUrl/presences")
            headers {
                append(HttpHeaders.Authorization, token)
                append(HttpHeaders.UserAgent, USER_AGENT)
            }
            contentType(ContentType.Application.Json)
            setBody(galaxyPresence)
        }
    }

    suspend fun checkForUpdate() = client.get {
        url("$githubBaseUrl/repos/dead8309/Kizzy/releases/latest")
    }
}

private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 11) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/100.0.4896.127 Mobile OceanHero/6 Safari/537.36"