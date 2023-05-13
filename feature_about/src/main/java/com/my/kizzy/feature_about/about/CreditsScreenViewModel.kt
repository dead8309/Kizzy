/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * CreditsScreenViewModel.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_about.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.kizzy.domain.model.Resource
import com.my.kizzy.domain.use_case.get_contributors.GetContributorsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CreditsScreenViewModel @Inject constructor(
    private val getContributorsUseCase: GetContributorsUseCase
): ViewModel() {
    private val _creditScreenState: MutableStateFlow<CreditScreenState> = MutableStateFlow(CreditScreenState.Loading)
    val creditScreenState: StateFlow<CreditScreenState> = _creditScreenState

    init {
        getContributors()
    }
    private fun getContributors() {
        getContributorsUseCase().onEach { result ->
            when(result){
                is Resource.Success -> {
                    _creditScreenState.value = CreditScreenState.LoadingCompleted(result.data?: emptyList())
                }
                is Resource.Error -> {
                    _creditScreenState.value = CreditScreenState.Error(result.message?: "An unexpected error occurred")
                }
                is Resource.Loading -> {
                    _creditScreenState.value = CreditScreenState.Loading
                }
            }
        }.launchIn(viewModelScope)
    }
}