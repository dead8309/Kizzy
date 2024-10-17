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

package com.my.kizzy.feature_profile.ui.user

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.my.kizzy.domain.model.rpc.RpcConfig
import com.my.kizzy.domain.model.user.User
import com.my.kizzy.feature_profile.ui.component.Logout
import com.my.kizzy.feature_profile.ui.component.ProfileCard
import com.my.kizzy.feature_profile.ui.component.ProfileNetworkError
import com.my.kizzy.preference.Prefs
import com.my.kizzy.preference.Prefs.CURRENT_PRESENCE
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.shimmer.AnimatedShimmer
import com.my.kizzy.ui.components.shimmer.ShimmerProfileCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    state: UserState,
    onBackPressed: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val presence = remember { mutableStateOf<RpcConfig>(Prefs[CURRENT_PRESENCE]) }
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    LaunchedEffect(presence) {
        while (true) {
            presence.value = Prefs[CURRENT_PRESENCE]
            delay(1000)
        }
    }

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
            when (state) {
                is UserState.Error -> {
                    ProfileNetworkError(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .align(Alignment.TopCenter),
                        error = (state.error)
                    )
                    ProfileCard(state.user)
                }

                UserState.Loading -> {
                    AnimatedShimmer {
                        ShimmerProfileCard(brush = it)
                    }
                }

                is UserState.LoadingCompleted -> {
                    ProfileCard(
                        user = state.user,
                        rpcConfig = presence.value,
                        type = presence.value.type.getType(ctx, presence.value.name) ?: stringResource(id = R.string.user_profile_rpc_name),
                    )
                    Logout(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = ctx.getString(R.string.are_you_sure),
                                actionLabel = ctx.getString(R.string.yes),
                                duration = SnackbarDuration.Short,
                                withDismissAction = true
                            ).run {
                                when (this) {
                                    SnackbarResult.ActionPerformed -> try {
                                        val runtime = Runtime.getRuntime()
                                        // running shell command to clear data
                                        // TODO replace with deleting directories and restarting the app to have multiple user accounts
                                        runtime.exec("pm clear com.my.kizzy")
                                    } catch (e: Exception) {
                                        e.printStackTrace()
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
}

private fun String?.getType(ctx: Context, name: String?): String? {
    val type: Int = try {
        if (!this.isNullOrEmpty()) this.toDouble().toInt()
        else 0
    } catch (ex: NumberFormatException) {
        0
    }
    return when (type) {
        1 -> ctx.getString(R.string.activity_streaming_title, name)
        2 -> ctx.getString(R.string.activity_listening_title, name)
        3 -> ctx.getString(R.string.activity_watching_title, name)
        4 -> ""
        5 -> ctx.getString(R.string.activity_competiting_title, name)
        else -> null
    }
}

val fakeUser = User(
    accentColor = null,
    avatar = null,
    avatarDecoration = null,
    badges = null,
    banner = null,
    bannerColor = null,
    discriminator = "3050",
    id = null,
    publicFlags = null,
    username = "yzziK",
    special = null,
    verified = false,
    nitro = true,
    bio = "Hello 👋"
)
@Preview
@Composable
fun UserScreenPreview() {
    UserScreen(
        state = UserState.Loading,
        onBackPressed = {},
    )
}
@Preview
@Composable
fun UserScreenPreview2() {
    UserScreen(
        state = UserState.Error(
            error = "No Internet Connection",
            user = fakeUser
        ),
        onBackPressed = {}
    )
}
@Preview
@Composable
fun UserScreenPreview3() {
    UserScreen(
        state = UserState.LoadingCompleted(
            user = fakeUser
        ),
        onBackPressed = {}
    )
}