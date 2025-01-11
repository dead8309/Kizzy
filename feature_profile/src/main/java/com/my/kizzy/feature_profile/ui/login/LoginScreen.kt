/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * LoginScreen.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_profile.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.my.kizzy.feature_profile.getUserInfo
import com.my.kizzy.feature_profile.ui.component.DiscordLoginButton
import com.my.kizzy.feature_profile.ui.component.DiscordLoginWebView
import com.my.kizzy.preference.Prefs
import com.my.kizzy.preference.Prefs.TOKEN
import com.my.kizzy.ui.components.BackButton
import kotlinx.coroutines.launch

const val JS_SNIPPET =
    "javascript:(function()%7Bvar%20i%3Ddocument.createElement('iframe')%3Bdocument.body.appendChild(i)%3Balert(i.contentWindow.localStorage.token.slice(1,-1))%7D)()"

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginScreen(
    onCompleted: () -> Unit,
    onBackPressed: () -> Unit
) {
    var buttonEnabledState by remember { mutableStateOf(true) }
    var uiState: LoginUiState by remember { mutableStateOf(LoginUiState.InitialState) }
    val scope = rememberCoroutineScope()
    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(title = { },
                navigationIcon = { BackButton { onBackPressed() } })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                LoginUiState.InitialState -> {}
                LoginUiState.OnLoginClicked -> {
                    ModalBottomSheet(
                        onDismissRequest = {
                            uiState = LoginUiState.InitialState
                        },
                        sheetState = modalBottomSheetState,
                        dragHandle = {
                            BottomSheetDefaults.DragHandle()
                        },
                    ) {
                        DiscordLoginWebView {
                            Prefs[TOKEN] = it
                            scope.launch {
                                modalBottomSheetState.hide()
                                uiState = LoginUiState.OnLoginCompleted
                                getUserInfo(it, onInfoSaved = {
                                    onCompleted()
                                })
                            }
                        }
                    }
                }
                LoginUiState.OnLoginCompleted -> {
                    buttonEnabledState = false
                    CircularProgressIndicator()
                }
            }
            DiscordLoginButton(
                onClick = { uiState = LoginUiState.OnLoginClicked },
                enabled = buttonEnabledState
            )
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onCompleted = {},
        onBackPressed = {}
    )
}