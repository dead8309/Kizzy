package com.my.kizzy.ui.screen.xbox

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.kizzy.utils.Games.getXboxData
import com.my.kizzy.utils.Xbox
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class XboxViewModel : ViewModel() {
    var state by mutableStateOf(XboxScreenState())
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
        val newList = getXboxData().filter {
            it.titlename.contains(query, ignoreCase = true)
        }
        state = state.copy(games = newList)
    }
}

sealed class Actions {
    data class TextFieldInput(val text: String) : Actions()
}

data class XboxScreenState(
    val searchText: String = "",
    val games: List<Xbox> = getXboxData()
)