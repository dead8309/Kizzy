/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ProfileViewModel.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.screen.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.my.kizzy.common.Resource
import com.my.kizzy.data.remote.User
import com.my.kizzy.domain.use_case.get_user.GetUserUseCase
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.USER_DATA
import com.my.kizzy.utils.Prefs.USER_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
): ViewModel() {

    private val _state = mutableStateOf(ProfileState())
    val state: State<ProfileState> = _state

    fun getUser(){
        getUserUseCase(Prefs[USER_ID,""]).onEach { result ->
            when(result){
                is Resource.Success -> {
                    _state.value = ProfileState(user = result.data)
                    Prefs[USER_DATA] = Gson().toJson(result.data)
                }
                is Resource.Error -> {
                    _state.value = ProfileState(
                        error = result.message?: "An unexpected error occurred"
                    )
                }
                is Resource.Loading -> {
                    val user = Gson().fromJson(Prefs[USER_DATA,"{}"], User::class.java)
                    _state.value = ProfileState(user = user)
                }
            }
        }.launchIn(viewModelScope)
    }
}
