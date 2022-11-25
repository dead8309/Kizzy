package com.my.kizzy

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.my.kizzy.ui.common.Routes
import com.my.kizzy.ui.common.animatedComposable
import com.my.kizzy.ui.screen.apps.AppsRPC
import com.my.kizzy.ui.screen.console_games.GamesScreen
import com.my.kizzy.ui.screen.console_games.GamesViewModel
import com.my.kizzy.ui.screen.custom.CustomRPC
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
import com.my.kizzy.ui.theme.AppTypography
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.THEME
import dagger.hilt.android.AndroidEntryPoint
import me.rerere.md3compat.Md3CompatTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Md3CompatTheme(
                typography = AppTypography,
                darkTheme = Prefs[THEME, isSystemInDarkTheme()]
            ) {
                Kizzy()
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
                animatedComposable(Routes.CUSTOM_RPC) { CustomRPC(onBackPressed = { navcontroller.popBackStack() }) }
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
                    }, onThemeModeChanged = {
                        Prefs[THEME] = it
                        recreate()
                    })
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

