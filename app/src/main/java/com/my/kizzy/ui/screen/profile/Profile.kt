package com.my.kizzy.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.my.kizzy.BuildConfig
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.theme.DISCORD_DARK
import com.my.kizzy.ui.theme.DISCORD_LIGHT_DARK
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.TOKEN
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile(
    onBackPressed: () -> Unit,
) {
    var switchScreen by remember {
        mutableStateOf(false)
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            contentAlignment = Alignment.Center) {
            if (Prefs[TOKEN, ""].isEmpty()) {
                Login(onCompleted = {
                    switchScreen = true
                })
            } else {
                User()
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
                                    runtime.exec("pm clear ${BuildConfig.APPLICATION_ID}")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                SnackbarResult.Dismissed -> Unit
                            }
                        }

                    }

                }
            }
            if (switchScreen) {
                User()
            }
        }
    }
}

@Composable
fun Logout(
    modifier: Modifier = Modifier,
    shape: Shape,
    onClicked: () -> Unit,
) {
    Button(
        modifier = modifier,
        shape = shape,
        onClick = { onClicked() },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Text(text = "Logout")
    }
}