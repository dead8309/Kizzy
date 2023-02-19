package kizzy.gateway.entities.presence

import com.google.gson.annotations.SerializedName

data class Presence(
    @SerializedName("activities")
    val activities: List<Activity?>?,
    @SerializedName("afk")
    val afk: Boolean? = true,
    @SerializedName("since")
    val since: Long? = System.currentTimeMillis(),
    @SerializedName("status")
    val status: String? = "online"
)