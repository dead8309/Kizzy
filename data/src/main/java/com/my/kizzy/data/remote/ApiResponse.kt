package com.my.kizzy.data.remote
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    @SerialName("url")
    val url: String
)

@Serializable
data class ImgurResponse(
    @SerialName("data")
    val data: Data
)

@Serializable
data class Data(
    @SerialName("link")
    val link: String
)