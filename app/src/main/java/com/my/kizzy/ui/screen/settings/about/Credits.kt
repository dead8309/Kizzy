package com.my.kizzy.ui.screen.settings.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.my.kizzy.R
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.CreditItem
import com.my.kizzy.ui.common.SettingTitle

data class Credit(val title: String = "", val license: String = "", val url: String = "")

const val GPL_V3 = "GNU General Public License v3.0"
const val APACHE_V2 = "Apache License, Version 2.0"

const val readYou = "https://github.com/Ashinch/ReadYou"
const val seal = "https://github.com/JunkFood02/Seal"
const val materialIcon = "https://fonts.google.com/icons"
const val materialColor = "https://github.com/re-ovo/md3compat"
const val nintendoRepo = "https://github.com/ninstar/Rich-Presence-U"

val creditsList = listOf(
    Credit("Read You", GPL_V3, readYou),
    Credit("Seal", GPL_V3, seal),
    Credit("Material Icons", APACHE_V2, materialIcon),
    Credit("md3compat", "", materialColor),
    Credit("Rich-Presence-U", GPL_V3, nintendoRepo)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Credits(onBackPressed: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })

    val uriHandler = LocalUriHandler.current
    fun openUrl(url: String) {
        uriHandler.openUri(url)
    }
    val languages = stringArrayResource(id = R.array.languages)
    val contributors = stringArrayResource(id = R.array.contributors)
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.credits),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = { BackButton{ onBackPressed() } },
                scrollBehavior = scrollBehavior
            )
        }
    ){
        LazyColumn(modifier = Modifier.padding(it)){
            item {
                SettingTitle(text = stringResource(id = R.string.design_credits))
                }
            items(creditsList){item: Credit ->
                CreditItem(title = item.title,
                description = item.license) {
                    openUrl(item.url)
                }
            }
            item {
                SettingTitle(text = stringResource(id = R.string.translation_credits))
            }
           items(languages.size){ lang ->
               CreditItem(
                   title = languages[lang],
                   description = contributors[lang],
               ) {}
           }
        }
    }
}

@Preview
@Composable
fun CreditScreen() {
    Credits {
    }
}