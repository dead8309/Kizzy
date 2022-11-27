package com.my.kizzy

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.my.kizzy.common.LocalDarkTheme
import com.my.kizzy.common.LocalDynamicColorSwitch
import com.my.kizzy.common.LocalSeedColor
import com.my.kizzy.common.SettingsProvider
import com.my.kizzy.ui.common.Routes
import com.my.kizzy.ui.common.animatedComposable
import com.my.kizzy.ui.screen.apps.AppsRPC
import com.my.kizzy.ui.screen.console_games.GamesScreen
import com.my.kizzy.ui.screen.console_games.GamesViewModel
import com.my.kizzy.ui.screen.custom.CustomRPC
import com.my.kizzy.ui.screen.custom.CustomScreenViewModel
import com.my.kizzy.ui.screen.home.Home
import com.my.kizzy.ui.screen.media.MediaRPC
import com.my.kizzy.ui.screen.profile.login.LoginScreen
import com.my.kizzy.ui.screen.profile.user.UserScreen
import com.my.kizzy.ui.screen.profile.user.UserViewModel
import com.my.kizzy.ui.screen.settings.Settings
import com.my.kizzy.ui.screen.settings.about.About
import com.my.kizzy.ui.screen.settings.about.Credits
import com.my.kizzy.ui.screen.settings.language.Language
import com.my.kizzy.ui.screen.settings.rpc_settings.RpcSettings
import com.my.kizzy.ui.screen.settings.style.Appearance
import com.my.kizzy.ui.screen.settings.style.DarkThemePreferences
import com.my.kizzy.ui.theme.KizzyTheme
import com.my.kizzy.utils.Prefs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            SettingsProvider(windowSizeClass.widthSizeClass) {
                KizzyTheme(
                    darkTheme = LocalDarkTheme.current.isDarkTheme(),
                    isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                    seedColor = LocalSeedColor.current,
                    isDynamicColorEnabled = LocalDynamicColorSwitch.current,
                ) {
                    Kizzy()
                }
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun Kizzy() {
        Scaffold()
        {
            val navcontroller = rememberAnimatedNavController()
            AnimatedNavHost(
                navController = navcontroller,
                startDestination = Routes.HOME
            ) {
                animatedComposable(Routes.HOME) {
                    Home(navController = navcontroller)
                }
                animatedComposable(Routes.SETTINGS) {
                    Settings(
                        onBackPressed = {
                            navcontroller.popBackStack()
                        },
                        navigateToAbout = {
                            navcontroller.navigate(Routes.ABOUT) {
                                launchSingleTop = true
                            }
                        },
                        navigateToProfile = {
                            navcontroller.navigate(Routes.PROFILE) {
                                launchSingleTop = true
                            }
                        },
                        navigateToStyleAndAppeareance = {
                            navcontroller.navigate(Routes.STYLE_AND_APPEAREANCE) {
                                launchSingleTop = true
                            }
                        },
                        navigateToRpcSettings = {
                            navcontroller.navigate(Routes.RPC_SETTINGS) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                animatedComposable(Routes.APPS_DETECTION) { AppsRPC(onBackPressed = { navcontroller.popBackStack() }) }
                animatedComposable(Routes.CUSTOM_RPC) {
                    val viewModel: CustomScreenViewModel by viewModels()
                    CustomRPC(onBackPressed = { navcontroller.popBackStack() },viewModel)
                }
                animatedComposable(Routes.MEDIA_RPC) { MediaRPC(onBackPressed = { navcontroller.popBackStack() }) }
                animatedComposable(Routes.PROFILE) {
                    var loggedIn by remember {
                        mutableStateOf(Prefs[Prefs.TOKEN, ""].isNotEmpty())
                    }
                    if (loggedIn) {
                        val viewModel: UserViewModel by viewModels()
                        UserScreen(viewModel = viewModel) {
                            navcontroller.popBackStack()
                        }
                    } else {
                        LoginScreen(onBackPressed = {
                            navcontroller.popBackStack()
                        }, onCompleted = {
                            loggedIn = true
                        })
                    }
                }
                animatedComposable(Routes.CONSOLE_RPC) {
                    val viewModel: GamesViewModel by viewModels()
                    GamesScreen(onBackPressed = {
                        navcontroller.popBackStack()
                    }, viewModel)
                }
                animatedComposable(Routes.LANGUAGES) {
                    Language(onBackPressed = {
                        navcontroller.popBackStack()
                    })
                }
                animatedComposable(Routes.STYLE_AND_APPEAREANCE) {
                    Appearance(onBackPressed = {
                        navcontroller.popBackStack()
                    }, navigateToLanguages = {
                        navcontroller.navigate(Routes.LANGUAGES)
                    }, navigateToDarkTheme = {
                        navcontroller.navigate(Routes.DARK_THEME)
                    })
                }
                animatedComposable(Routes.DARK_THEME) {
                    DarkThemePreferences {
                        navcontroller.popBackStack()
                    }
                }
                animatedComposable(Routes.RPC_SETTINGS) {
                    RpcSettings {
                        navcontroller.popBackStack()
                    }
                }
                animatedComposable(Routes.ABOUT) {
                    About(
                        onBackPressed = {
                            navcontroller.popBackStack()
                        },
                        navigateToCredits = {
                            navcontroller.navigate(Routes.CREDITS)
                        }
                    )
                }
                animatedComposable(Routes.CREDITS) {
                    Credits {
                        navcontroller.popBackStack()
                    }
                }
            }
        }
    }
}

