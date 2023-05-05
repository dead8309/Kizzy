package com.my.kizzy

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.my.kizzy.preference.Prefs
import com.my.kizzy.preference.getLanguageConfig
import com.my.kizzy.ui.Routes
import com.my.kizzy.ui.animatedComposable
import com.my.kizzy.ui.screen.home.Home
import com.my.kizzy.ui.screen.home.apps.AppsRPC
import com.my.kizzy.ui.screen.home.console_games.GamesScreen
import com.my.kizzy.ui.screen.home.console_games.GamesViewModel
import com.my.kizzy.ui.screen.home.custom.CustomRPC
import com.my.kizzy.ui.screen.home.custom.CustomScreenViewModel
import com.my.kizzy.ui.screen.home.media.MediaRPC
import com.my.kizzy.ui.screen.home.media.hasNotificationAccess
import com.my.kizzy.ui.screen.profile.login.LoginScreen
import com.my.kizzy.ui.screen.profile.user.UserScreen
import com.my.kizzy.ui.screen.profile.user.UserViewModel
import com.my.kizzy.ui.screen.settings.about.About
import com.my.kizzy.ui.screen.settings.about.Credits
import com.my.kizzy.ui.screen.settings.about.CreditsScreenViewModel
import com.my.kizzy.ui.screen.settings.language.Language
import com.my.kizzy.ui.screen.settings.logs.LogScreen
import com.my.kizzy.ui.screen.settings.logs.LogsViewModel
import com.my.kizzy.ui.screen.settings.rpc_settings.RpcSettings
import com.my.kizzy.ui.screen.settings.style.Appearance
import com.my.kizzy.ui.screen.settings.style.DarkThemePreferences
import com.my.kizzy.ui.screen.startup.StartUp
import com.my.kizzy.ui.theme.KizzyTheme
import com.my.kizzy.utils.LocalDarkTheme
import com.my.kizzy.utils.LocalDynamicColorSwitch
import com.my.kizzy.utils.SettingsProvider
import com.my.kizzy.utils.hasUsageAccess
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        usageAccessStatus = mutableStateOf(this.hasUsageAccess())
        mediaControlStatus = mutableStateOf(this.hasNotificationAccess())
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }
        runBlocking {
            if (Build.VERSION.SDK_INT < 33)
                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(getLanguageConfig())
                )
        }
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            SettingsProvider(windowSizeClass.widthSizeClass) {
                KizzyTheme(
                    darkTheme = LocalDarkTheme.current.isDarkTheme(),
                    isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                    isDynamicColorEnabled = LocalDynamicColorSwitch.current,
                ) {
                    Kizzy()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mediaControlStatus.value = hasNotificationAccess()
        usageAccessStatus.value = hasUsageAccess()
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun Kizzy() {
        Scaffold()
        {
            val navController = rememberAnimatedNavController()
            AnimatedNavHost(
                navController = navController,
                startDestination = if (Prefs[Prefs.IS_FIRST_LAUNCHED,true]) Routes.SETUP else Routes.HOME
            ) {
                animatedComposable(Routes.SETUP) {
                   StartUp(
                       usageAccessStatus = usageAccessStatus,
                       mediaControlStatus = mediaControlStatus, navigateToLanguages = {
                       navController.navigate(Routes.LANGUAGES){
                           launchSingleTop = true
                       }
                   }, navigateToHome = {
                       Prefs[Prefs.IS_FIRST_LAUNCHED] = false
                       navController.navigate(Routes.HOME) {
                           popUpTo(Routes.SETUP) { inclusive = true }
                       }
                   }, navigateToLogin = {
                       navController.navigate(Routes.PROFILE)
                   })
                }
                animatedComposable(Routes.HOME) {
                    Home(
                        hasUsageAccess = usageAccessStatus,
                        hasNotificationAccess = mediaControlStatus
                    ){
                        navController.navigate(it)
                    }
                }
                animatedComposable(Routes.APPS_DETECTION) { AppsRPC(onBackPressed = { navController.popBackStack() }) }
                animatedComposable(Routes.CUSTOM_RPC) {
                    val viewModel: CustomScreenViewModel by viewModels()
                    CustomRPC(onBackPressed = { navController.popBackStack() },viewModel)
                }
                animatedComposable(Routes.MEDIA_RPC) { MediaRPC(onBackPressed = { navController.popBackStack() }) }
                animatedComposable(Routes.PROFILE) {
                    var loggedIn by remember {
                        mutableStateOf(Prefs[Prefs.TOKEN, ""].isNotEmpty())
                    }
                    if (loggedIn) {
                        val viewModel: UserViewModel by viewModels()
                        UserScreen(viewModel = viewModel) {
                            navController.popBackStack()
                        }
                    } else {
                        LoginScreen(onBackPressed = {
                            navController.popBackStack()
                        }, onCompleted = {
                            loggedIn = true
                        })
                    }
                }
                animatedComposable(Routes.CONSOLE_RPC) {
                    val viewModel: GamesViewModel by viewModels()
                    GamesScreen(onBackPressed = {
                        navController.popBackStack()
                    }, viewModel)
                }
                animatedComposable(Routes.LANGUAGES) {
                    Language(onBackPressed = {
                        navController.popBackStack()
                    })
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
                animatedComposable(Routes.LOGS_SCREEN){
                    val viewModel: LogsViewModel by viewModels()
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
                    val viewModel: CreditsScreenViewModel by viewModels()
                    Credits(viewModel) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
    companion object {
        lateinit var usageAccessStatus: MutableState<Boolean>
        lateinit var mediaControlStatus: MutableState<Boolean>
        fun setLanguage(locale: String) {
            val localeListCompat =
                if (locale.isEmpty()) LocaleListCompat.getEmptyLocaleList()
                else LocaleListCompat.forLanguageTags(locale)
            AppCompatDelegate.setApplicationLocales(localeListCompat)
        }
    }
}