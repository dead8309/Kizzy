package com.my.kizzy.ui.screen.home.custom
import com.google.gson.annotations.SerializedName

data class RpcIntent(
    @SerializedName("button1")
    val button1: String = "",
    @SerializedName("button1link")
    val button1link: String = "",
    @SerializedName("button2")
    val button2: String = "",
    @SerializedName("button2link")
    val button2link: String = "",
    @SerializedName("details")
    val details: String = "",
    @SerializedName("largeImg")
    val largeImg: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("smallImg")
    val smallImg: String = "",
    @SerializedName("state")
    val state: String = "",
    @SerializedName("status")
    val status: String = "",
    @SerializedName("timeatampsStart")
    val timeatampsStart: String = "",
    @SerializedName("timeatampsStop")
    val timeatampsStop: String = "",
    @SerializedName("type")
    val type: String = "",
    @SerializedName("large_text")
    val largeText: String = "",
    @SerializedName("small_text")
    val smallText: String = ""
)