/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Assets.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.rpc.model


import com.google.gson.annotations.SerializedName

data class Assets(
    @SerializedName("large_image")
    val largeImage: String?,
    @SerializedName("small_image")
    val smallImage: String?,
    @SerializedName("large_text")
    val largeText: String? = null,
    @SerializedName("small_text")
    val smallText: String? = null,

)