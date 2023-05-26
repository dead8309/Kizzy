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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("bio")
    val bio: String? = "",
    @SerialName("nitro")
    val nitro: Boolean? = false,
    @SerialName("accent_color")
    val accentColor: Int?,
    @SerialName("avatar")
    val avatar: String?,
    @SerialName("avatar_decoration")
    val avatarDecoration: String?,
    @SerialName("badges")
    val badges: List<Badge>?,
    @SerialName("banner")
    val banner: String?,
    @SerialName("banner_color")
    val bannerColor: String?,
    @SerialName("discriminator")
    val discriminator: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("public_flags")
    val publicFlags: Int?,
    @SerialName("username")
    val username: String?,
    @SerialName("special")
    val special: String?,
    @SerialName("verified")
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

@Serializable
data class Badge(
    @SerialName("icon")
    val icon: String,
    @SerialName("name")
    val name: String
)
const val DISCORD_CDN = "https://cdn.discordapp.com"