package com.my.kizzy.rpc

import com.google.gson.annotations.SerializedName

data class WebhookModel(
    @SerializedName("attachments")
    val attachments: List<Attachment>,
    @SerializedName("author")
    val author: Author,
    @SerializedName("channel_id")
    val channelId: String,
    @SerializedName("components")
    val components: List<Any>,
    @SerializedName("content")
    val content: String,
    @SerializedName("edited_timestamp")
    val editedTimestamp: Any,
    @SerializedName("embeds")
    val embeds: List<Any>,
    @SerializedName("flags")
    val flags: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("mention_everyone")
    val mentionEveryone: Boolean,
    @SerializedName("mention_roles")
    val mentionRoles: List<Any>,
    @SerializedName("mentions")
    val mentions: List<Any>,
    @SerializedName("pinned")
    val pinned: Boolean,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("tts")
    val tts: Boolean,
    @SerializedName("type")
    val type: Int,
    @SerializedName("webhook_id")
    val webhookId: String
)

data class Attachment(
    @SerializedName("content_type")
    val contentType: String,
    @SerializedName("filename")
    val filename: String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("proxy_url")
    val proxyUrl: String,
    @SerializedName("size")
    val size: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("width")
    val width: Int
)

data class Author(
    @SerializedName("avatar")
    val avatar: Any,
    @SerializedName("bot")
    val bot: Boolean,
    @SerializedName("discriminator")
    val discriminator: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("username")
    val username: String
)


