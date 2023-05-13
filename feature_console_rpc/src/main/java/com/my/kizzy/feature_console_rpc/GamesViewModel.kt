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

package com.my.kizzy.feature_console_rpc

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.kizzy.domain.model.Game
import com.my.kizzy.domain.model.Resource
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
) : ViewModel() {

    private val _state: MutableState<GamesState> = mutableStateOf(GamesState.Loading)
    val state: State<GamesState> = _state
    private val games = mutableListOf<Game>()
    val isSearchBarVisible = mutableStateOf(false)

    private var searchJob: Job? = null

    init {
        getGames()
    }

    private fun getGames() {
        getGamesUseCase().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _state.value = GamesState.Success(games = result.data ?: emptyList())
                    games.addAll(result.data ?: emptyList())
                }

                is Resource.Error -> {
                    _state.value = GamesState.Error(
                        error = result.message ?: "An unexpected error occurred"
                    )
                }

                is Resource.Loading -> {
                    _state.value = GamesState.Loading
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onUiEvent(uiEvent: UiEvent) {
        when (uiEvent) {
            UiEvent.CloseSearchBar -> isSearchBarVisible.value = false
            UiEvent.OpenSearchBar -> isSearchBarVisible.value = true
            is UiEvent.Search -> onSearch(uiEvent.query)
            UiEvent.TryAgain -> getGames()
        }
    }

    private fun onSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            searchForGame(query)
        }
    }

    private fun searchForGame(query: String) = if (query == "")
        _state.value = GamesState.Success(games = games)
    else {
        val newList = games.filter {
            it.game_title.contains(query, ignoreCase = true)
        }
        _state.value = GamesState.Success(games = newList)
    }
}








