/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * User.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.remote

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("accent_color")
    val accentColor: Int?,
    @SerializedName("avatar")
    val avatar: String?,
    @SerializedName("avatar_decoration")
    val avatarDecoration: Any?,
    @SerializedName("badges")
    val badges: List<Badge>?,
    @SerializedName("banner")
    val banner: String?,
    @SerializedName("banner_color")
    val bannerColor: String?,
    @SerializedName("discriminator")
    val discriminator: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("public_flags")
    val publicFlags: Int?,
    @SerializedName("username")
    val username: String?,
    @SerializedName("special")
    val special: String?,
    @SerializedName("verified")
    val verified: Boolean
)

data class Badge(
    @SerializedName("icon")
    val icon: String,
    @SerializedName("name")
    val name: String
)