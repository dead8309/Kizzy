package com.my.kizzy

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.my.kizzy.ui.common.Routes
import com.my.kizzy.ui.common.animatedComposable
import com.my.kizzy.ui.screen.home.Home
import com.my.kizzy.ui.screen.profile.Profile
import com.my.kizzy.ui.screen.rpc.apps.AppsRPC
import com.my.kizzy.ui.screen.rpc.custom.CustomRPC
import com.my.kizzy.ui.screen.rpc.media.MediaRPC
import com.my.kizzy.ui.screen.rpc.settings.RpcSettings
import com.my.kizzy.ui.screen.settings.Settings
import com.my.kizzy.ui.screen.settings.about.About
import com.my.kizzy.ui.screen.settings.language.Language
import com.my.kizzy.ui.screen.settings.style.Appearance
import com.my.kizzy.ui.theme.AppTypography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import me.rerere.md3compat.Md3CompatTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Md3CompatTheme(typography = AppTypography) {
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
                animatedComposable(Routes.SETTINGS) { Settings(navcontroller) }
                animatedComposable(Routes.APPS_DETECTION) { AppsRPC(navcontroller) }
                animatedComposable(Routes.CUSTOM_RPC) { CustomRPC(navcontroller) }
                animatedComposable(Routes.MEDIA_RPC) { MediaRPC(navcontroller) }
                animatedComposable(Routes.PROFILE) { Profile(navcontroller) }
                animatedComposable(Routes.RPC_SETTINGS) { RpcSettings(navcontroller) }
                animatedComposable(Routes.LANGUAGES) { Language(navcontroller) }
                animatedComposable(Routes.STYLE_AND_APPEAREANCE) { Appearance(navcontroller) }
                animatedComposable(Routes.ABOUT) { About() }
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MaterialTheme {
        }
    }
}