/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * About.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_about.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.preference.PreferencesHint
import com.my.kizzy.ui.components.SettingItem
import com.my.kizzy.feature_about.BuildConfig



const val github_Repository = "https://github.com/dead8309/Kizzy"
const val github_Release = "$github_Repository/releases"
const val github_Issues = "$github_Repository/issues/new"
const val github_privacy_policy = "$github_Repository/blob//master/TERMS_OF_SERVICE.md"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun About(
    onBackPressed: ()-> Unit,
    navigateToCredits: () -> Unit
){
    val uriHandler = LocalUriHandler.current
    fun openUrl(url: String){
        uriHandler.openUri(url)
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.about),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = { BackButton{ onBackPressed() } }
            )
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)){

            item {
                PreferencesHint(
                    title = stringResource(id = R.string.privacy_policy),
                    description = stringResource(id = R.string.privacy_policy_desc),
                    icon = Icons.Outlined.PrivacyTip
                ) {
                    uriHandler.openUri(github_privacy_policy)
                }
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.github_readme),
                    description = stringResource(id = R.string.github_readme_desc),
                    icon = Icons.Outlined.Description
                ) {
                    openUrl(github_Repository)
                }
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.github_latest_release),
                    description = stringResource(id = R.string.github_latest_release_desc),
                    icon = Icons.Outlined.NewReleases
                ) {
                    openUrl(github_Release)
                }
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.github_issue),
                    description = stringResource(id = R.string.github_issue_desc),
                    icon = Icons.Outlined.ContactSupport
                ) {
                    openUrl(github_Issues)
                }
            }
            item {
                SettingItem(
                    title = stringResource(id = R.string.credits),
                    description = stringResource(id = R.string.credits_desc),
                    icon = Icons.Outlined.AutoAwesome
                ) {
                    navigateToCredits()
                }
            }
            item { 
                SettingItem(
                    title = "Version",
                    description = BuildConfig.VERSION_NAME,
                    icon = Icons.Outlined.Info
                ) {
                }
            }
        }
    }
}

@Preview
@Composable
fun AboutScreen() {
    About(onBackPressed = {}) {

    }
}