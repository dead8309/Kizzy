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

package com.my.kizzy.domain.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("bio")
    val bio: String? = "",
    @SerialName("nitro")
    val nitro: Boolean? = false,
    @SerialName("accent_color")
    val accentColor: Int? = null,
    @SerialName("avatar")
    val avatar: String? = null,
    @SerialName("avatar_decoration")
    val avatarDecoration: String? = null,
    @SerialName("badges")
    val badges: List<Badge>? = null,
    @SerialName("banner")
    val banner: String? = null,
    @SerialName("banner_color")
    val bannerColor: String? = null,
    @SerialName("discriminator")
    val discriminator: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("public_flags")
    val publicFlags: Int? = null,
    @SerialName("username")
    val username: String? = null,
    @SerialName("special")
    val special: String? = null,
    @SerialName("verified")
    val verified: Boolean = false
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

private const val DISCORD_CDN = "https://cdn.discordapp.com"