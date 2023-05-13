/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Contributor.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.domain.model

import com.google.gson.annotations.SerializedName

data class Contributor(
    @SerializedName("avatar")
    val avatar: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("url")
    val url: String = ""
)