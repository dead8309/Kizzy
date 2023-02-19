package kizzy.gateway.entities.presence

import com.google.gson.annotations.SerializedName

data class Assets(
    @SerializedName("large_image")
    val largeImage: String?,
    @SerializedName("small_image")
    val smallImage: String?,
    @SerializedName("large_text")
    val largeText: String? = null,
    @SerializedName("small_text")
    val smallText: String? = null,
)
