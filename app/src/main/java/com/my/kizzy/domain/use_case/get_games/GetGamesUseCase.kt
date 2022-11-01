/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * GetGamesUseCase.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.domain.use_case.get_games

import com.my.kizzy.common.Resource
import com.my.kizzy.data.remote.toGame
import com.my.kizzy.domain.model.Game
import com.my.kizzy.domain.repository.KizzyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetGamesUseCase @Inject constructor(
    private val kizzyRepository: KizzyRepository
) {
    operator fun invoke(): Flow<Resource<List<Game>>> = flow {
        try {
            emit(Resource.Loading())
            val games = kizzyRepository.getGames().map { it.toGame() }
            emit(Resource.Success(games))
        } catch (e: HttpException){
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException){
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        } catch (e: Exception){
            emit(Resource.Error("An unexpected error occurred"))
        }
    }
}