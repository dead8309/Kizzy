package kizzy.gateway.entities

import com.google.gson.annotations.SerializedName

data class Resume(
    @SerializedName("seq")
    val seq: Int,
    @SerializedName("session_id")
    val sessionId: String?,
    @SerializedName("token")
    val token: String
)
