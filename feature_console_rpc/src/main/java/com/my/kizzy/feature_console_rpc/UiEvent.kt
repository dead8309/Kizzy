/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * UiEvent.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_console_rpc

sealed interface UiEvent {
    object TryAgain: UiEvent
    object CloseSearchBar: UiEvent
    object OpenSearchBar: UiEvent
    class Search(val query: String): UiEvent
}