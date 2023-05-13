/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * LoginScreen.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_profile.ui.login

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.my.kizzy.feature_profile.getUserInfo
import com.my.kizzy.preference.Prefs
import com.my.kizzy.preference.Prefs.TOKEN
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.theme.DISCORD_GREY
import kotlinx.coroutines.launch

const val JS_SNIPPET =
    "javascript:(function()%7Bvar%20i%3Ddocument.createElement('iframe')%3Bdocument.body.appendChild(i)%3Balert(i.contentWindow.localStorage.token.slice(1,-1))%7D)()"

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginScreen(
    onCompleted: () -> Unit,
    onBackPressed: () -> Unit
) {
    var showWebView by remember {
        mutableStateOf(false)
    }
    var showProgress by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val url = "https://discord.com/login"
    Scaffold(
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
            if (showProgress) {
                CircularProgressIndicator()
            }
            ElevatedButton(
                onClick = { showWebView = true },
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = DISCORD_GREY,
                    contentColor = Color.White.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !showProgress
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_discord),
                    tint = Color.Unspecified,
                    contentDescription = "discord_login",
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(text = stringResource(id = R.string.login_with_discord))
            }
            if (showWebView) {

                AndroidView(factory = {
                    WebView(it).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        webViewClient = object : WebViewClient() {

                            @Deprecated("Deprecated in Java")
                            override fun shouldOverrideUrlLoading(
                                webView: WebView,
                                url: String,
                            ): Boolean {
                                stopLoading()
                                if (url.endsWith("/app")) {
                                    loadUrl(JS_SNIPPET)
                                    visibility = View.GONE
                                }
                                return false
                            }
                        }
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        webChromeClient = object : WebChromeClient() {
                            override fun onJsAlert(
                                view: WebView,
                                url: String,
                                message: String,
                                result: JsResult,
                            ): Boolean {
                                Prefs[TOKEN] = message
                                showProgress = true
                                scope.launch {
                                    getUserInfo(message, onInfoSaved = {
                                        onCompleted()
                                    })
                                }
                                visibility = View.GONE
                                return true
                            }
                        }
                        loadUrl(url)
                    }
                })
            }
        }
    }
}
@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onCompleted = {},
        onBackPressed = {}
    )
}