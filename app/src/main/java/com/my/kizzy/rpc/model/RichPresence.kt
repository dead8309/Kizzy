/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * RichPresence.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.rpc.model


import com.google.gson.annotations.SerializedName

data class RichPresence(
    @SerializedName("d")
    val d: RichPresenceData?,
    @SerializedName("op")
    val op: Int?
)