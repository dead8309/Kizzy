@file:Suppress("DEPRECATION")

package com.my.kizzy.ui.screen.apps


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppsOutage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.my.kizzy.R
import com.my.kizzy.service.AppDetectionService
import com.my.kizzy.service.CustomRpcService
import com.my.kizzy.service.ExperimentalRpc
import com.my.kizzy.service.MediaRpcService
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.PreferencesHint
import com.my.kizzy.ui.common.SwitchBar
import com.my.kizzy.utils.AppUtils
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.hasUsageAccess

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsRPC(onBackPressed: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })
    val ctx = LocalContext.current
    var hasUsageAccess by remember { mutableStateOf(ctx.hasUsageAccess()) }
    var serviceEnabled by remember { mutableStateOf(AppUtils.appDetectionRunning()) }
    var apps by remember { mutableStateOf(getInstalledApps(ctx)) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Apps",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = { BackButton { onBackPressed() } },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {

            LazyColumn {
                item {
                    AnimatedVisibility(visible = !hasUsageAccess
                    ) {
                        PreferencesHint(
                            title = stringResource(id = R.string.usage_access),
                            description = stringResource(id = R.string.usage_access_desc),
                            icon = Icons.Default.AppsOutage,
                        ) {
                            when (ctx.hasUsageAccess()) {
                                true -> hasUsageAccess = !hasUsageAccess
                                false -> ctx.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
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
                                ctx.stopService(Intent(ctx,ExperimentalRpc::class.java))
                                ctx.startService(Intent(ctx, AppDetectionService::class.java))
                            }
                            false -> ctx.stopService(Intent(ctx, AppDetectionService::class.java))
                        }

                    }
                }
                items(apps.size) { i ->
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
                }
            }
        }
    }
}
@Suppress("DEPRECATION")
fun getInstalledApps(context1: Context): List<AppsInfo> {
    val appList: ArrayList<AppsInfo> = ArrayList()
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
    val resolveInfoList: List<ResolveInfo> = context1.packageManager.queryIntentActivities(intent, 0)

    for (resolveInfo in resolveInfoList) {
        val activityInfo = resolveInfo.activityInfo
        if (!resolveInfo.isSystemPackage()) {
            appList.add(AppsInfo(
                name = context1.packageManager.getApplicationLabel(activityInfo.applicationInfo).toString(),
                pkg = activityInfo.applicationInfo.packageName.toString(),
                isChecked = Prefs.isAppEnabled(activityInfo.packageName),
            ))
        }
    }
    return appList
}

private fun ResolveInfo.isSystemPackage(): Boolean {
    return this.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
}

















