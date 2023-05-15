/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * DiscordLoginWebView.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_profile.ui.component

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.my.kizzy.feature_profile.ui.login.JS_SNIPPET

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun DiscordLoginWebView(
    onLoginCompleted: (String) -> Unit
) {
    val url = "https://discord.com/login"
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
                    onLoginCompleted(message)
                    visibility = View.GONE
                    return true
                }
            }
            loadUrl(url)
        }
    })
}