/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Identify.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.rpc.model


import com.google.gson.annotations.SerializedName

data class Identify(
    @SerializedName("d")
    val d: Data,
    @SerializedName("op")
    val op: Int
)

data class Data(
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
)
data class Properties(
    @SerializedName("browser")
    val browser: String,
    @SerializedName("device")
    val device: String,
    @SerializedName("os")
    val os: String
)