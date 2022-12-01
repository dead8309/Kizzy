/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Reconnect.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.rpc.model


import com.google.gson.annotations.SerializedName

data class Resume(
    @SerializedName("d")
    val d: D,
    @SerializedName("op")
    val op: Int
)

data class D(
    @SerializedName("seq")
    val seq: Int,
    @SerializedName("session_id")
    val sessionId: String?,
    @SerializedName("token")
    val token: String
)