package kizzy.gateway.entities

import com.google.gson.annotations.SerializedName


data class Identify(
    @SerializedName("capabilities")
    val capabilities: Int,
    @SerializedName("compress")
    val compress: Boolean,
    @SerializedName("largeThreshold")
    val largeThreshold: Int,
    @SerializedName("properties")
    val properties: Properties,
    @SerializedName("token")
    val token: String
){
    companion object {
        fun String.toIdentifyPayload() = Identify(
            capabilities = 65,
            compress = false,
            largeThreshold = 100,
            properties = Properties(
                browser = "Discord Client",
                device = "ktor",
                os = "Windows"
            ),
            token = this
        )
    }
}
data class Properties(
    @SerializedName("browser")
    val browser: String,
    @SerializedName("device")
    val device: String,
    @SerializedName("os")
    val os: String
)
