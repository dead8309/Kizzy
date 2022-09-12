package com.my.kizzy.ui.screen.settings.about

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
import com.blankj.utilcode.util.AppUtils
import com.my.kizzy.R
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.SettingItem

const val github_Release = "https://github.com/dead8309/Kizzy-compose/releases"
const val github_Repository = "https://github.com/dead8309/Kizzy-compose"
const val github_Issues = "https://github.com/dead8309/Kizzy-compose/issues/new"

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
                        text = "About",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = { BackButton{ onBackPressed() } }
            )
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)){

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
                    description = AppUtils.getAppVersionName(),
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