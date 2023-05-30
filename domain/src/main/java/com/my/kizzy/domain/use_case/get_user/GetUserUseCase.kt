/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * GetUserUseCase.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.domain.use_case.get_user

import com.my.kizzy.domain.model.Resource
import com.my.kizzy.domain.model.user.User
import com.my.kizzy.domain.repository.KizzyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val kizzyRepository: KizzyRepository
) {
    operator fun invoke(userid: String): Flow<Resource<User>> = flow {
        try {
            emit(Resource.Loading())
            val user = kizzyRepository.getUser(userid)
            emit(Resource.Success(user))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }
}