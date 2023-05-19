/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * RpcButtons.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.domain.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

@JsonAdapter(RpcButtonsDeserializer::class)
data class RpcButtons(
    @SerializedName("button1")
    val button1: String = "",
    @SerializedName("button2")
    val button2: String = "",
    @SerializedName("button1Url")
    val button1Url: String = "",
    @SerializedName("button2Url")
    val button2Url: String = ""
)

class RpcButtonsDeserializer: JsonDeserializer<RpcButtons> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): RpcButtons {
        val jsonObject = json?.asJsonObject
        val button1 = jsonObject?.get("button1")?.asString ?: ""
        val button1Url = jsonObject?.get("button1Url")?.asString ?: ""
        val button2 = jsonObject?.get("button2")?.asString ?: ""
        val button2Url = jsonObject?.get("button2Url")?.asString ?: ""
        return RpcButtons(
            button1 = button1,
            button2 = button2,
            button1Url = button1Url,
            button2Url = button2Url
        )
    }
}