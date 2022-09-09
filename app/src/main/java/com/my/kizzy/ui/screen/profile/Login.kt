package com.my.kizzy.ui.screen.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.my.kizzy.ui.common.BackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(onBackPressed: () -> Unit) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                },
                navigationIcon = { BackButton { onBackPressed() } }
            )
        }
    ){
        Box(Modifier
            .fillMaxSize()
            .padding(it)) {

        }
    }
}

@Preview
@Composable
fun LoginScreen() {
    Login {

    }
}