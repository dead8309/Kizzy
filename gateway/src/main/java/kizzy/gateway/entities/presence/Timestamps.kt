package kizzy.gateway.entities.presence


import com.google.gson.annotations.SerializedName

data class Timestamps(
    @SerializedName("end")
    val end: Long? = null,
    @SerializedName("start")
    val start: Long? = null
)