/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ImgurApiService.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.remote

import com.my.kizzy.data.rpc.Constants.APPLICATION_ID
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import java.io.File
import javax.inject.Inject

class ImgurApiService @Inject constructor(
    private val client: HttpClient,
    @Discord private val discordBaseUrl: String,
    @Imgur private val imgurBaseUrl: String,
) {
    suspend fun getImage(url: String, token: String) = runCatching {
        client.post {
            url("$discordBaseUrl/applications/$APPLICATION_ID/external-assets")
            headers {
                append(HttpHeaders.Authorization, token)
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(mapOf("urls" to arrayOf(url)))
        }
    }

    suspend fun uploadImage(file: File, clientId: String) = runCatching {
        client.post {
            url("$imgurBaseUrl/image")
            headers {
                append(HttpHeaders.Authorization, "Client-ID $clientId")
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
    }
}