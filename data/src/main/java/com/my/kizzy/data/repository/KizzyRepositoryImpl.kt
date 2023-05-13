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
import com.my.kizzy.data.remote.toGame
import com.my.kizzy.data.utils.toImageAsset
import com.my.kizzy.domain.model.Contributor
import com.my.kizzy.domain.model.Game
import com.my.kizzy.domain.model.User
import com.my.kizzy.domain.repository.KizzyRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class KizzyRepositoryImpl @Inject constructor(
    private val api: ApiService,
): KizzyRepository {

    override suspend fun getImage(url: String): String? {
        return api.getImage(url).toImageAsset()
    }

    override suspend fun uploadImage(file: File): String? {
        val reqBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData(
            "temp",
            file.name, reqBody
        )
        return api.uploadImage(part).toImageAsset()
    }

    override suspend fun getGames(): List<Game> {
        return api.getGames().map { it.toGame() }
    }

    override suspend fun getUser(userid: String): User {
        return api.getUser(userid)
    }
    override suspend fun getContributors(): List<Contributor> {
        return api.getContributors()
    }
}