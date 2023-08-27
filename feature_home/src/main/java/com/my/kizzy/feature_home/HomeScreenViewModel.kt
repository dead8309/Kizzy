/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * AboutScreenViewModel.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.kizzy.domain.model.Resource
import com.my.kizzy.domain.model.release.Release
import com.my.kizzy.domain.use_case.check_for_update.CheckForUpdateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val checkForUpdateUseCase: CheckForUpdateUseCase
): ViewModel() {
    private val _aboutScreenState: MutableStateFlow<HomeScreenState> = MutableStateFlow(
        HomeScreenState.Loading
    )
    val aboutScreenState: StateFlow<HomeScreenState> = _aboutScreenState

    fun getLatestUpdate() {
        checkForUpdateUseCase().onEach { result ->
            when(result){
                is Resource.Success -> {
                    _aboutScreenState.value =
                        HomeScreenState.LoadingCompleted(result.data ?: Release())
                }
                is Resource.Error -> {
                    _aboutScreenState.value =
                        HomeScreenState.Error(result.message ?: "An unexpected error occurred")
                }
                is Resource.Loading -> {
                    _aboutScreenState.value = HomeScreenState.Loading
                }
            }
        }.launchIn(viewModelScope)
    }
    fun setReleaseFromPrefs(release: Release){
        _aboutScreenState.value = HomeScreenState.LoadingCompleted(release)
    }
}

sealed interface HomeScreenState {
    object Loading: HomeScreenState
    class Error(val error: String?): HomeScreenState
    class LoadingCompleted(val release: Release): HomeScreenState
}