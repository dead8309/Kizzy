/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * GamesState.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.screen.home.console_games

import com.my.kizzy.domain.model.Game

data class GamesState(
    val isLoading: Boolean = false,
    val games: List<Game> = emptyList(),
    val error: String = "",
    val searchText: String = "",
    val success: Boolean = false
)
