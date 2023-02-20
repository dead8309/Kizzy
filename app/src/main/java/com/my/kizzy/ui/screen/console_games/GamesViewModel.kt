/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * GamesViewModel.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.screen.console_games

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.kizzy.domain.model.Resource
import com.my.kizzy.domain.model.Game
import com.my.kizzy.domain.use_case.get_games.GetGamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamesViewModel @Inject constructor(
    private val getGamesUseCase: GetGamesUseCase
): ViewModel() {

   private val _state = mutableStateOf(GamesState())
    val state: State<GamesState> = _state
    private val games = mutableListOf<Game>()
    val isSearchBarVisible = mutableStateOf(false)

    private var searchJob: Job? = null

    init {
        getGames()
    }

    fun getGames(){
        getGamesUseCase().onEach { result ->
            when(result){
                is Resource.Success -> {
                    _state.value = GamesState(games = result.data?: emptyList(), success = true)
                    games.addAll(result.data?: emptyList())
                }
                is Resource.Error -> {
                    _state.value = GamesState(
                        error = result.message?: "An unexpected error occurred"
                    )
                }
                is Resource.Loading -> {
                    _state.value = GamesState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
    fun onSearch(query: String){
                _state.value = _state.value.copy(searchText = query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500)
                    searchForGame(query)
                }
    }

   private fun searchForGame(query: String) = if (query == "")
       _state.value = _state.value.copy(games = games)
   else {
       val newList = games.filter {
           it.game_title.contains(query, ignoreCase = true)
       }
       _state.value =_state.value.copy(games = newList)
   }
}








