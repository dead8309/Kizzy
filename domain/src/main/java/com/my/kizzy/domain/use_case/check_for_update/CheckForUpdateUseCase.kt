/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * CheckForUpdateUseCase.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.domain.use_case.check_for_update

import com.my.kizzy.domain.model.Resource
import com.my.kizzy.domain.model.release.Release
import com.my.kizzy.domain.repository.KizzyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CheckForUpdateUseCase @Inject constructor(
    private val repository: KizzyRepository
) {
    operator fun invoke(): Flow<Resource<Release>> = flow {
        try {
            emit(Resource.Loading())
            val release = repository.checkForUpdate()
            emit(Resource.Success(release))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}