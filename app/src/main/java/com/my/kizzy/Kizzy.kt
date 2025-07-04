/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Kizzy.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy

import android.annotation.SuppressLint
import android.content.ComponentName
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.my.kizzy.domain.model.toVersion
import com.my.kizzy.feature_about.about.About
import com.my.kizzy.feature_about.about.Credits
import com.my.kizzy.feature_about.about.CreditsScreenViewModel
import com.my.kizzy.feature_apps_rpc.AppsRPC
import com.my.kizzy.feature_apps_rpc.AppsScreenViewModel
import com.my.kizzy.feature_console_rpc.GamesScreen
import com.my.kizzy.feature_console_rpc.GamesViewModel
import com.my.kizzy.feature_custom_rpc.CustomRPC
import com.my.kizzy.feature_custom_rpc.CustomScreenViewModel
import com.my.kizzy.feature_home.Home
import com.my.kizzy.feature_home.HomeScreenViewModel
import com.my.kizzy.feature_home.feature.homeFeaturesProvider
import com.my.kizzy.feature_logs.LogScreen
import com.my.kizzy.feature_logs.LogsViewModel
import com.my.kizzy.feature_media_rpc.MediaRPC
import com.my.kizzy.feature_media_rpc.MediaScreenViewModel
import com.my.kizzy.feature_profile.ui.login.LoginScreen
import com.my.kizzy.feature_profile.ui.user.UserScreen
import com.my.kizzy.feature_profile.ui.user.UserViewModel
import com.my.kizzy.feature_rpc_base.AppUtils
import com.my.kizzy.feature_rpc_base.services.KizzyTileService
import com.my.kizzy.feature_settings.language.Language
import com.my.kizzy.feature_settings.rpc_settings.RpcSettings
import com.my.kizzy.feature_settings.style.Appearance
import com.my.kizzy.feature_settings.style.DarkThemePreferences
import com.my.kizzy.feature_startup.StartUp
import com.my.kizzy.navigation.Routes
import com.my.kizzy.navigation.animatedComposable
import com.my.kizzy.preference.Prefs
import xyz.dead8309.feature_experimental_rpc.ExperimentalRpcScreen
import xyz.dead8309.feature_experimental_rpc.ExperimentalRpcViewmodel
import xyz.dead8309.feature_experimental_rpc.apps.ExperimentalRpcAppsScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun ComponentActivity.Kizzy(
    usageAccessStatus: MutableState<Boolean>,
    notificationListenerAccess: MutableState<Boolean>,
) {
    Scaffold()
    {
        val navController = rememberAnimatedNavController()
        AnimatedNavHost(
            navController = navController,
            startDestination = if (Prefs[Prefs.IS_FIRST_LAUNCHED, true]) Routes.SETUP else Routes.HOME
        ) {
            animatedComposable(Routes.SETUP) {
                StartUp(
                    usageAccessStatus = usageAccessStatus,
                    mediaControlStatus = notificationListenerAccess,
                    navigateToLanguages = {
                        navController.navigate(Routes.LANGUAGES) {
                            launchSingleTop = true
                        }
                    },
                    navigateToHome = {
                        Prefs[Prefs.IS_FIRST_LAUNCHED] = false
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.SETUP) { inclusive = true }
                        }
                    },
                    navigateToLogin = {
                        navController.navigate(Routes.PROFILE)
                    })
            }
            animatedComposable(Routes.HOME) {
                val release = Prefs.getSavedLatestRelease()
                val user = Prefs.getUser()
                val ctx = LocalContext.current
                val viewModel by viewModels<HomeScreenViewModel>()
                val state = viewModel.aboutScreenState.collectAsState().value
                val showBadge = release
                    ?.toVersion()
                    ?.whetherNeedUpdate(BuildConfig.VERSION_NAME.toVersion())
                    ?: false
                Home(
                    state = state,
                    checkForUpdates = {
                        if (release != null && release.toVersion() > BuildConfig.VERSION_NAME.toVersion()) {
                            viewModel.setReleaseFromPrefs(release)
                        } else {
                            viewModel.getLatestUpdate()
                        }
                    },
                    showBadge = showBadge,
                    features = homeFeaturesProvider(
                        navigateTo = { navController.navigate(it) },
                        hasUsageAccess = usageAccessStatus,
                        hasNotificationAccess = notificationListenerAccess,
                        userVerified = user?.verified == true
                    ),
                    user = user,
                    componentName = ComponentName(ctx, KizzyTileService::class.java),
                    navigateToProfile = {
                        navController.navigate(Routes.PROFILE)
                    },
                    navigateToStyleAndAppearance = {
                        navController.navigate(Routes.STYLE_AND_APPEARANCE)
                    },
                    navigateToLanguages = {
                        navController.navigate(Routes.LANGUAGES)
                    },
                    navigateToAbout = {
                        navController.navigate(Routes.ABOUT)
                    },
                    navigateToRpcSettings = {
                        navController.navigate(Routes.RPC_SETTINGS)
                    },
                    navigateToLogsScreen = {
                        navController.navigate(Routes.LOGS_SCREEN)
                    }
                )
            }
            animatedComposable(Routes.APPS_DETECTION) {
                val viewModel by viewModels<AppsScreenViewModel>()
                AppsRPC(
                    onBackPressed = { navController.popBackStack() },
                    hasUsageAccess = usageAccessStatus.value,
                    state = viewModel.state.collectAsState().value,
                    updateAppEnabled = viewModel::updateAppEnabled,
                )
            }
            animatedComposable(Routes.CUSTOM_RPC) {
                val viewModel by viewModels<CustomScreenViewModel>()
                CustomRPC(
                    onBackPressed = { navController.popBackStack() },
                    state = viewModel.uiState.collectAsState().value,
                    onEvent = viewModel::onEvent
                )
            }
            animatedComposable(Routes.MEDIA_RPC) {
                val viewModel by viewModels<MediaScreenViewModel>()
                MediaRPC(
                    onBackPressed = { navController.popBackStack() },
                    state = viewModel.state.collectAsState().value,
                    hasNotificationAccess = notificationListenerAccess.value,
                    updateMediaAppEnabled = viewModel::updateMediaAppEnabled
                )
            }
            animatedComposable(Routes.PROFILE) {
                var loggedIn by remember {
                    mutableStateOf(Prefs[Prefs.TOKEN, ""].isNotEmpty())
                }
                if (loggedIn) {
                    val viewModel by viewModels<UserViewModel>()
                    UserScreen(
                        state = viewModel.state.value,
                        onBackPressed = navController::popBackStack
                    )
                } else {
                    LoginScreen(
                        onBackPressed = navController::popBackStack,
                        onCompleted = {
                            loggedIn = true
                        },
                    )
                }
            }
            animatedComposable(Routes.CONSOLE_RPC) {
                val viewModel by viewModels<GamesViewModel>()
                GamesScreen(
                    onBackPressed = { navController.popBackStack() },
                    onEvent = { viewModel.onUiEvent(it) },
                    isSearchBarVisible = viewModel.isSearchBarVisible.value,
                    state = viewModel.state.value,
                    serviceEnabled = AppUtils.customRpcRunning()
                )
            }
            animatedComposable(Routes.LANGUAGES) {
                Language(
                    onBackPressed = { navController.popBackStack() },
                    updateLocaleLanguage = MainActivity::setLanguage
                )
            }
            animatedComposable(Routes.STYLE_AND_APPEARANCE) {
                Appearance(onBackPressed = {
                    navController.popBackStack()
                }) {
                    navController.navigate(Routes.DARK_THEME)
                }
            }
            animatedComposable(Routes.DARK_THEME) {
                DarkThemePreferences {
                    navController.popBackStack()
                }
            }
            animatedComposable(Routes.RPC_SETTINGS) {
                RpcSettings { navController.popBackStack() }
            }
            animatedComposable(Routes.LOGS_SCREEN) {
                val viewModel by viewModels<LogsViewModel>()
                LogScreen(viewModel)
            }
            animatedComposable(Routes.ABOUT) {
                About(
                    onBackPressed = {
                        navController.popBackStack()
                    },
                    navigateToCredits = {
                        navController.navigate(Routes.CREDITS)
                    }
                )
            }
            animatedComposable(Routes.CREDITS) {
                val viewModel by viewModels<CreditsScreenViewModel>()
                Credits(
                    state = viewModel.creditScreenState.collectAsState().value,
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }

            val experimentalRpcViewModel by lazy {
                viewModels<ExperimentalRpcViewmodel>()
            }

            animatedComposable(Routes.EXPERIMENTAL_RPC) {
                ExperimentalRpcScreen(
                    state = experimentalRpcViewModel.value.uiState.collectAsState().value,
                    onEvent = experimentalRpcViewModel.value::onEvent,
                    onBackPressed = { navController.popBackStack() },
                    hasUsageAccess = usageAccessStatus.value,
                    hasNotificationAccess = notificationListenerAccess.value,
                    navigateToAppSelection = {
                        navController.navigate(Routes.EXPERIMENTAL_RPC_APPS)
                    },
                )
            }

            animatedComposable(Routes.EXPERIMENTAL_RPC_APPS) {
                ExperimentalRpcAppsScreen(
                    onBackPressed = { navController.popBackStack() },
                    viewModel = experimentalRpcViewModel.value
                )
            }
        }
    }
}