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

import com.google.gson.annotations.SerializedName

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