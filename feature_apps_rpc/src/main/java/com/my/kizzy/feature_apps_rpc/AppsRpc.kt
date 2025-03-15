/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * AppsRpc.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

@file:Suppress("DEPRECATION")

package com.my.kizzy.feature_apps_rpc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppsOutage
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.my.kizzy.feature_rpc_base.AppUtils
import com.my.kizzy.feature_rpc_base.services.AppDetectionService
import com.my.kizzy.feature_rpc_base.services.CustomRpcService
import com.my.kizzy.feature_rpc_base.services.ExperimentalRpc
import com.my.kizzy.feature_rpc_base.services.MediaRpcService
import com.my.kizzy.preference.Prefs
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.AppsItem
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.SearchBar
import com.my.kizzy.ui.components.SwitchBar
import com.my.kizzy.ui.components.preference.PreferencesHint

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsRPC(
    onBackPressed: () -> Unit,
    hasUsageAccess: Boolean,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })
    val ctx = LocalContext.current
    var serviceEnabled by remember { mutableStateOf(AppUtils.appDetectionRunning()) }
    var apps by remember { mutableStateOf(getInstalledApps(ctx)) }
    var searchText by remember { mutableStateOf("") }
    var isSearchBarVisible by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(id = R.string.main_appDetection),
                            style = MaterialTheme.typography.headlineLarge,
                        )
                    }
                },
                navigationIcon = { BackButton { onBackPressed() } },
                actions = {
                    if (isSearchBarVisible) {
                        Column(
                            modifier = Modifier.padding(10.dp)
                        ) {
                            SearchBar(
                                text = searchText,
                                onTextChanged = { searchText = it },
                                onClose = { isSearchBarVisible = false },
                                placeholder = stringResource(id = R.string.search_placeholder)
                            )
                        }
                    } else {
                        IconButton(onClick = { isSearchBarVisible = !isSearchBarVisible }) {
                            Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {

            LazyColumn {
                item {
                    AnimatedVisibility(
                        visible = !hasUsageAccess
                    ) {
                        PreferencesHint(
                            title = stringResource(id = R.string.usage_access),
                            description = stringResource(id = R.string.usage_access_desc),
                            icon = Icons.Default.AppsOutage,
                        ) {
                            when (hasUsageAccess) {
                                false -> ctx.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                                else -> Unit
                            }
                        }
                    }
                }
                item {
                    SwitchBar(
                        title = stringResource(id = R.string.enable_appsRpc),
                        isChecked = serviceEnabled,
                        enabled = hasUsageAccess
                    ) {
                        serviceEnabled = !serviceEnabled
                        when (serviceEnabled) {
                            true -> {
                                ctx.stopService(Intent(ctx, MediaRpcService::class.java))
                                ctx.stopService(Intent(ctx, CustomRpcService::class.java))
                                ctx.stopService(Intent(ctx, ExperimentalRpc::class.java))
                                ctx.startService(Intent(ctx, AppDetectionService::class.java))
                            }

                            false -> ctx.stopService(Intent(ctx, AppDetectionService::class.java))
                        }
                    }
                }
                items(apps.size) { i ->
                    if (searchText.isEmpty() || apps[i].name.contains(
                            searchText,
                            ignoreCase = true
                        ) || apps[i].pkg.contains(searchText, ignoreCase = true)
                    ) {
                        AppsItem(
                            name = apps[i].name,
                            pkg = apps[i].pkg,
                            isChecked = apps[i].isChecked
                        ) {
                            apps = apps.mapIndexed { j, app ->
                                if (i == j) {
                                    Prefs.saveToPrefs(app.pkg)
                                    app.copy(isChecked = !app.isChecked)
                                } else
                                    app
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(0.dp))
                    }
                }
            }
        }
    }
}

/*
  TODO: Move this and MediaRpc's getInstalledApps function in a common place
 */
fun getInstalledApps(context1: Context): List<AppsInfo> {
    val appList: ArrayList<AppsInfo> = ArrayList()
    val pm = context1.packageManager
    val resolvedAppsInfo = pm.getInstalledApplications(PackageManager.GET_GIDS)

    for (appInfo in resolvedAppsInfo) {
        if (pm.getLaunchIntentForPackage(appInfo.packageName) != null) {
            appList.add(
                AppsInfo(
                    name = appInfo.loadLabel(pm).toString(),
                    pkg = appInfo.packageName,
                    isChecked = Prefs.isAppEnabled(appInfo.packageName),
                )
            )
        }
    }
    return appList.sortedBy { it.name }.sortedBy { !it.isChecked }
}