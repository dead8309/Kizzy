/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * CheckConnectionUseCase.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.domain.use_case.check_connection

import com.my.kizzy.domain.model.Resource
import com.my.kizzy.domain.repository.KizzyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CheckConnectionUseCase @Inject constructor(
    private val repository: KizzyRepository
) {
    operator fun invoke(): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading())
            val isConnected = repository.checkConnection()
            emit(Resource.Success(isConnected))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}