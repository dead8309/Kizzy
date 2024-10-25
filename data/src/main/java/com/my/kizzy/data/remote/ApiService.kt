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

import com.my.kizzy.data.rpc.Constants.APPLICATION_ID
import com.my.kizzy.domain.model.samsung_rpc.GalaxyPresence
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import java.io.File
import javax.inject.Inject

open class ApiService @Inject constructor(
    private val client: HttpClient,
    @Base private val baseUrl: String,
    @Discord private val discordBaseUrl: String,
    @Github private val githubBaseUrl: String,
) {
    open suspend fun getImage(url: String, token: String) = runCatching {
        client.get {
            url("$baseUrl/image")
            parameter("url", url)
        }
    }.getOrNull()?.toImageAsset()

    open suspend fun uploadImage(file: File, token: String) = runCatching {
        client.post {
            url("$baseUrl/upload")
            setBody(MultiPartFormDataContent(
                formData {
                    append("\"temp\"", file.readBytes(), Headers.build {
                        append(HttpHeaders.ContentType, "image/*")
                        append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                    })
                }
            ))
        }
    }.getOrNull()?.toImageAsset()

    open suspend fun HttpResponse.toImageAsset(): String? {
        return try {
            if (this.status == HttpStatusCode.OK)
                this.body<ApiResponse>().id
            else
                null
        } catch (e: Exception) {
            null
        }
    }

    class ImgurApiService @Inject constructor(
        private val client: HttpClient,
        @Discord private val discordBaseUrl: String,
        @Imgur private val imgurBaseUrl: String,
    ) : ApiService(client, "", discordBaseUrl, "") {
        override suspend fun getImage(url: String, token: String) = runCatching {
            client.post {
                url("$discordBaseUrl/applications/$APPLICATION_ID/external-assets")
                headers {
                    append(HttpHeaders.Authorization, token)
                    append(HttpHeaders.ContentType, "application/json")
                }
                setBody(mapOf("urls" to arrayOf(url)))
            }
        }.getOrNull()?.toExternalImage()

        override suspend fun uploadImage(file: File, token: String) = runCatching {
            client.post {
                url("$imgurBaseUrl/image")
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
        }.getOrNull()?.toImageAsset()?.let { this.getImage(it, token) }

        override suspend fun HttpResponse.toImageAsset(): String? {
            return try {
                if (this.status == HttpStatusCode.OK)
                    this.body<ImgurResponse>().data.link
                else
                    null
            } catch (e: Exception) {
                null
            }
        }

        suspend fun HttpResponse.toExternalImage(): String? {
            return try {
                if (this.status == HttpStatusCode.OK)
                    "mp:" + this.body<Array<ExternalAsset>>().first().externalAssetPath
                else
                    null
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun getGames() = runCatching {
        client.get {
            url("$baseUrl/games")
        }
    }

    suspend fun getUser(userid: String) = runCatching {
        client.get {
            url("$baseUrl/user/$userid")
        }
    }

    suspend fun getContributors() = runCatching {
        client.get {
            url("$baseUrl/contributors")
        }
    }

    suspend fun setSamsungGalaxyPresence(galaxyPresence: GalaxyPresence, token: String) =
        runCatching {
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

    suspend fun checkForUpdate() = runCatching {
        client.get {
            url("$githubBaseUrl/repos/dead8309/Kizzy/releases/latest")
        }
    }
}

private const val USER_AGENT =
    "Mozilla/5.0 (Linux; Android 11) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/100.0.4896.127 Mobile OceanHero/6 Safari/537.36"