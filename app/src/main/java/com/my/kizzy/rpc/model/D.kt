/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * D.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.rpc.model


import com.google.gson.annotations.SerializedName

data class RichPresenceData(
    @SerializedName("activities")
    val activities: List<Activity?>?,
    @SerializedName("afk")
    val afk: Boolean?,
    @SerializedName("since")
    val since: Long?,
    @SerializedName("status")
    val status: String?
)