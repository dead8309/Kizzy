/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * KizzyRepository.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */
package com.my.kizzy.domain.repository

import com.my.kizzy.domain.model.Contributor
import com.my.kizzy.domain.model.Game
import com.my.kizzy.domain.model.samsung_rpc.GalaxyPresence
import com.my.kizzy.domain.model.user.User
import java.io.File

interface KizzyRepository {
    suspend fun getImage(url: String): String?
    suspend fun uploadImage(file: File): String?
    suspend fun getGames(): List<Game>
    suspend fun getUser(userid: String): User
    suspend fun getContributors(): List<Contributor>
    suspend fun setSamsungGalaxyPresence(galaxyPresence: GalaxyPresence, token: String)
}
