/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Timestamps.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.rpc.model


import com.google.gson.annotations.SerializedName

data class Timestamps(
    @SerializedName("end")
    val end: Long? = null,
    @SerializedName("start")
    val start: Long? = null
)