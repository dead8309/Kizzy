/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * UserScreen.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.screen.profile.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.my.kizzy.BuildConfig
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.shimmer.AnimatedShimmer
import com.my.kizzy.ui.components.shimmer.ShimmerProfileCard
import com.my.kizzy.ui.screen.profile.user.component.Logout
import com.my.kizzy.ui.screen.profile.user.component.ProfileCard
import com.my.kizzy.ui.screen.profile.user.component.ProfileNetworkError
import com.my.kizzy.data.utils.Log.logger
import kotlinx.coroutines.launch

const val Base = "https://cdn.discordapp.com"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    viewModel: UserViewModel,
    onBackPressed: () -> Unit
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            if (state.error.isNotEmpty()) {
                ProfileNetworkError(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .align(Alignment.TopCenter),
                    error = state.error
                )
            }
            if (state.loading) {
                AnimatedShimmer {
                    ShimmerProfileCard(brush = it)
                }
            } else {
                ProfileCard(state.user)
                Logout(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Are you sure ?",
                            actionLabel = "Yes",
                            duration = SnackbarDuration.Short,
                            withDismissAction = true
                        ).run {
                            when (this) {
                                SnackbarResult.ActionPerformed -> try {
                                    val runtime = Runtime.getRuntime()
                                    // running shell command to clear data
                                    runtime.exec("pm clear ${BuildConfig.APPLICATION_ID}")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    logger.e("Error",e.message.toString())
                                }
                                SnackbarResult.Dismissed -> Unit
                            }
                        }

                    }

                }
            }
        }
    }
}