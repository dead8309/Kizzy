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

package com.my.kizzy.domain.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("bio")
    val bio: String? = "",
    @SerializedName("nitro")
    val nitro: Boolean? = false,
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
) {
    fun getAvatarImage(): String {
        return if (avatar?.startsWith("a_") == true)
            "${DISCORD_CDN}/avatars/${id}/${avatar}.gif"
        else
            "${DISCORD_CDN}/avatars/${id}/${avatar}.png"
    }
    fun getBannerImage(): String? {
        if (banner.isNullOrEmpty()) return null
        return if (banner.startsWith("a_"))
            "$DISCORD_CDN/banners/${id}/${banner}.gif"
        else
            "$DISCORD_CDN/banners/${id}/${banner}.png"
    }
}

data class Badge(
    @SerializedName("icon")
    val icon: String,
    @SerializedName("name")
    val name: String
)
const val DISCORD_CDN = "https://cdn.discordapp.com"