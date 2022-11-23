/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Activity.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.rpc.model


import com.google.gson.annotations.SerializedName

data class Activity(
    @SerializedName("name")
    val name: String?,
    @SerializedName("state")
    val state: String? = null,
    @SerializedName("details")
    val details: String? = null,
    @SerializedName("type")
    val type: Int? = 0,
    @SerializedName("timestamps")
    val timestamps: Timestamps? = null,
    @SerializedName("assets")
    val assets: Assets? = null,
    @SerializedName("buttons")
    val buttons: List<String?>? = null,
    @SerializedName("metadata")
    val metadata: Metadata? = null,
    @SerializedName("application_id")
    val applicationId: String? = null
)