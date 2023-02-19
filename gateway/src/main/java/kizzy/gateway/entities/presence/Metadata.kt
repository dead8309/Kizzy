package kizzy.gateway.entities.presence

import com.google.gson.annotations.SerializedName

data class Metadata(
    @SerializedName("button_urls")
    val buttonUrls: List<String?>?
)