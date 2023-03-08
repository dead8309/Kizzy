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

package com.my.kizzy.ui.screen.settings.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.kizzy.data.remote.Contributor
import com.my.kizzy.domain.model.Resource
import com.my.kizzy.domain.use_case.get_contributors.GetContributorsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CreditsScreenViewModel @Inject constructor(
    private val getContributorsUseCase: GetContributorsUseCase
): ViewModel() {
    val contributors: MutableStateFlow<Resource<List<Contributor>>> = MutableStateFlow(Resource.Loading())

    init {
        getContributors()
    }
    private fun getContributors() {
        getContributorsUseCase().onEach { result ->
            when(result){
                is Resource.Success -> {
                    contributors.value = Resource.Success(result.data?: emptyList())
                }
                is Resource.Error -> {
                    contributors.value = Resource.Error(result.message?: "An unexpected error occurred")
                }
                is Resource.Loading -> {
                    contributors.value = Resource.Loading()
                }
            }
        }.launchIn(viewModelScope)
    }
}