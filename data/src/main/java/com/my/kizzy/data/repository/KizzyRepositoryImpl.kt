/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * KizzyRepositoryImpl.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.repository

import com.my.kizzy.data.remote.ApiService
import com.my.kizzy.data.remote.GamesResponse
import com.my.kizzy.data.remote.toGame
import com.my.kizzy.data.utils.toImageAsset
import com.my.kizzy.domain.model.Contributor
import com.my.kizzy.domain.model.Game
import com.my.kizzy.domain.model.samsung_rpc.GalaxyPresence
import com.my.kizzy.domain.model.user.User
import com.my.kizzy.domain.repository.KizzyRepository
import io.ktor.client.call.body
import java.io.File
import javax.inject.Inject

class KizzyRepositoryImpl @Inject constructor(
    private val api: ApiService,
): KizzyRepository {

    override suspend fun getImage(url: String): String? {
        return api.getImage(url).toImageAsset()
    }

    override suspend fun uploadImage(file: File): String? {
        return api.uploadImage(file).toImageAsset()
    }

    override suspend fun getGames(): List<Game> {
        return api.getGames().body<List<GamesResponse>>().map { it.toGame() }
    }

    override suspend fun getUser(userid: String): User {
        return api.getUser(userid).body()
    }
    override suspend fun getContributors(): List<Contributor> {
        return api.getContributors().body()
    }
    override suspend fun setSamsungGalaxyPresence(galaxyPresence: GalaxyPresence,token: String) {
        return api.setSamsungGalaxyPresence(galaxyPresence,token)
    }
}