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
import com.my.kizzy.preference.Prefs
import com.my.kizzy.preference.Prefs.USER_BIO

data class User(
    @SerializedName("accent_color")
    val accentColor: Int? = null,
    @SerializedName("avatar")
    val avatar: String? = null,
    @SerializedName("avatar_decoration")
    val avatarDecoration: Any? = null,
    @SerializedName("badges")
    val badges: List<Badge?>? = null,
    @SerializedName("banner")
    val banner: String? = null,
    @SerializedName("banner_color")
    val bannerColor: String? = "#000000",
    @SerializedName("bio")
    val bio: String = Prefs[USER_BIO,""],
    @SerializedName("cached")
    val cached: Boolean? = null,
    @SerializedName("discriminator")
    val discriminator: String? = null,
    @SerializedName("display_name")
    val displayName: Any? = null,
    @SerializedName("flags")
    val flags: Int? = null,
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("public_flags")
    val publicFlags: Int? = null,
    @SerializedName("special")
    val special: String? = null,
    @SerializedName("theme_colors")
    val themeColors: List<String> = listOf("#FFA3A1ED", "#FFA77798"),
    @SerializedName("username")
    val username: String? = null,
    @SerializedName("verified")
    val verified: Boolean? = null
)

data class Badge(
    @SerializedName("icon")
    val icon: String,
    @SerializedName("name")
    val name: String
)