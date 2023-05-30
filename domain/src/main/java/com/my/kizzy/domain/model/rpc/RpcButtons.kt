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

package com.my.kizzy.domain.model.rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RpcButtons(
    @SerialName("button1")
    val button1: String = "",
    @SerialName("button2")
    val button2: String = "",
    @SerialName("button1Url")
    val button1Url: String = "",
    @SerialName("button2Url")
    val button2Url: String = ""
)