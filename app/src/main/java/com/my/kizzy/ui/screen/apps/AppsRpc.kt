@file:Suppress("DEPRECATION")

package com.my.kizzy.ui.screen.apps


import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AppsOutage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.my.kizzy.R
import com.my.kizzy.service.AppDetectionService
import com.my.kizzy.service.CustomRpcService
import com.my.kizzy.service.MediaRpcService
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.PreferencesHint
import com.my.kizzy.ui.common.SwitchBar
import com.my.kizzy.utils.AppUtils
import com.my.kizzy.utils.Prefs
import com.skydoves.landscapist.glide.GlideImage


fun Context.hasUsageAccess(): Boolean {
    return try {
        val packageManager: PackageManager = this.packageManager
        val applicationInfo = packageManager.getApplicationInfo(this.packageName, 0)
        val appOpsManager = this.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            applicationInfo.uid,
            applicationInfo.packageName)
        mode == AppOpsManager.MODE_ALLOWED
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsRPC(onBackPressed: () -> Unit) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })
    val ctx = LocalContext.current
    var hasUsageAccess by remember {
        mutableStateOf(ctx.hasUsageAccess())
    }

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

            var serviceEnabled by remember {
                mutableStateOf(AppUtils.appDetectionRunning())
            }


            var apps by remember {
                mutableStateOf(
                    getInfo(ctx)
                )
            }
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
                        checked = serviceEnabled,
                        enabled = hasUsageAccess
                    ) {
                        serviceEnabled = !serviceEnabled
                        when (serviceEnabled) {
                            true -> {
                                ctx.stopService(Intent(ctx, MediaRpcService::class.java))
                                ctx.stopService(Intent(ctx, CustomRpcService::class.java))
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
                        icon = apps[i].icon,
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

data class AppsInfo(
    val name: String,
    val pkg: String,
    val icon: Drawable?,
    val isChecked: Boolean,
)

@Composable
fun AppsItem(
    name: String,
    pkg: String,
    icon: Drawable?,
    isChecked: Boolean,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = Modifier.clickable {
            onClick()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon?.let {
                GlideImage(
                    imageModel = it,
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .padding(10.dp),
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis
                )
                if (pkg.isNotEmpty())
                    Text(
                        text = pkg,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        style = MaterialTheme.typography.bodyMedium,
                    )
            }
            Switch(
                checked = isChecked,
                onCheckedChange = null,
                modifier = Modifier.padding(start = 20.dp, end = 6.dp),
            )
        }
    }
}

fun getInfo(context1: Context): List<AppsInfo> {
    val appList: ArrayList<AppsInfo> = ArrayList()
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
    @Suppress("DEPRECATION") val resolveInfoList: List<ResolveInfo> =
        context1.packageManager.queryIntentActivities(intent, 0)



    for (resolveInfo in resolveInfoList) {
        val activityInfo = resolveInfo.activityInfo
        if (!isSystemPackage(resolveInfo)) {
            appList.add(AppsInfo(
                name = context1.packageManager.getApplicationLabel(activityInfo.applicationInfo)
                    .toString(),
                pkg = activityInfo.applicationInfo.packageName.toString(),
                icon = activityInfo.loadIcon(context1.packageManager),
                isChecked = Prefs.isAppEnabled(activityInfo.packageName),
            ))
        }

    }
    return appList
}

private fun isSystemPackage(resolveInfo: ResolveInfo): Boolean {
    return resolveInfo.activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
}

















