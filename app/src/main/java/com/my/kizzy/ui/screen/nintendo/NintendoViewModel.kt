package com.my.kizzy.ui.screen.nintendo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.kizzy.ui.screen.nintendo.Games.getGamesData
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NintendoViewModel : ViewModel() {
    var state by mutableStateOf(NintendoScreenState())
    private var searchJob: Job? = null

    fun onAction(actions: Actions){
        when(actions){
            is Actions.TextFieldInput -> {
                state = state.copy(searchText = actions.text)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500)
                    searchForGame(actions.text)
                }
            }
        }
    }

   private fun searchForGame(query: String) {
        val newList = getGamesData().filter {
            it.title.contains(query, ignoreCase = true)
        }
        state = state.copy(games = newList)
    }
}

sealed class Actions {
    data class TextFieldInput(val text: String) : Actions()
}

data class NintendoScreenState(
    val searchText: String = "",
   val games: List<GameItem> = getGamesData()
)