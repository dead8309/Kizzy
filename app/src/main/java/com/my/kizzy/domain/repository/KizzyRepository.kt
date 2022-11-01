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

import com.my.kizzy.data.remote.GamesResponse
import java.io.File

interface KizzyRepository {

    suspend fun getImage(url: String): String?

    suspend fun uploadImage(file: File): String?

    suspend fun getGames(): List<GamesResponse>
}
