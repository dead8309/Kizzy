package com.my.kizzy.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.theme.DISCORD_DARK
import com.my.kizzy.ui.theme.DISCORD_LIGHT_DARK
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.TOKEN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    onBackPressed: () -> Unit
) {
    var switchScreen by remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            SmallTopAppBar(
                title = { },
                navigationIcon = { BackButton { onBackPressed() } },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = DISCORD_DARK,
                    scrolledContainerColor = DISCORD_DARK,
                    navigationIconContentColor = Color.White,
                ))
        }
    ) {

        Box(modifier = Modifier
            .padding(it)
            .fillMaxSize()
            .background(DISCORD_LIGHT_DARK),
            contentAlignment = Alignment.Center){
            if (Prefs[TOKEN, ""].isEmpty()) {
                Login(onCompleted = {
                    switchScreen = true
                })
            } else {
                User()
            }
            if (switchScreen) User()
        }
    }
}