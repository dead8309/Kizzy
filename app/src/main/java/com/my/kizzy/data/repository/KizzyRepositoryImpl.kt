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
import com.my.kizzy.domain.repository.KizzyRepository
import com.my.kizzy.utils.toImageAsset
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

    override suspend fun getGames(): List<GamesResponse> {
        return api.getGames()
    }
}