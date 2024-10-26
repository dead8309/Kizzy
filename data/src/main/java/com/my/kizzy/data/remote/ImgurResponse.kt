/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ImgurResponse.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImgurResponse(
    @SerialName("data")
    val data: Data
)

@Serializable
data class Data(
    @SerialName("link")
    val link: String
)