package com.my.kizzy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.my.kizzy.ui.common.Routes
import com.my.kizzy.ui.screen.home.Home
import com.my.kizzy.ui.screen.profile.Profile
import com.my.kizzy.ui.screen.rpc.apps.AppsRPC
import com.my.kizzy.ui.screen.rpc.custom.CustomRPC
import com.my.kizzy.ui.screen.rpc.media.MediaRPC
import com.my.kizzy.ui.screen.rpc.settings.RpcSettings
import com.my.kizzy.ui.screen.settings.Settings
import com.my.kizzy.ui.screen.settings.language.Languages
import com.my.kizzy.ui.screen.settings.style.Appearance
import me.rerere.md3compat.Md3CompatTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Md3CompatTheme {
                Kizzy()
            }
        }
    }

    @Composable
    fun Kizzy() {
        val navcontroller = rememberNavController()
        NavHost(navController = navcontroller, startDestination = Routes.HOME){
            composable(Routes.HOME){
                Home(navcontroller)
            }
            composable(Routes.SETTINGS){ Settings(navcontroller)}
            composable(Routes.APPS_DETECTION) { AppsRPC()}
            composable(Routes.CUSTOM_RPC) { CustomRPC(navcontroller)}
            composable(Routes.MEDIA_RPC){ MediaRPC(navcontroller)}
            composable(Routes.PROFILE) { Profile(navcontroller) }
            composable(Routes.RPC_SETTINGS){RpcSettings(navcontroller)}
            //Todo Setup settings navGraph
            composable(Routes.LANGUAGES){ Languages()}
            composable(Routes.STYLE_AND_APPEAREANCE){ Appearance(navcontroller)}
        }
    }



    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MaterialTheme {
            Settings(navController = rememberNavController())
        }
    }
}