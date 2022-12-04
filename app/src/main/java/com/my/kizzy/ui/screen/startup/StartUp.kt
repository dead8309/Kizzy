/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * StartUp.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.screen.startup

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.gson.Gson
import com.my.kizzy.R
import com.my.kizzy.data.remote.User
import com.my.kizzy.ui.common.PreferenceSubtitle
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.fromJson

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun StartUp(
    usageAccessStatus: MutableState<Boolean>,
    mediaControlStatus: MutableState<Boolean>,
    navigateToLanguages: () -> Unit = {},
    navigateToLogin: () -> Unit = {},
    navigateToHome: () -> Unit = {}
) {
    val user: User? = Gson().fromJson(Prefs[Prefs.USER_DATA,""])
    val context = LocalContext.current
    val storagePermissionState = rememberPermissionState(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    var notificationPostingPerm by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context, POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                notificationPostingPerm = isGranted
            })
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.W600),
                color = MaterialTheme.colorScheme.primary
            )
        })
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                Modifier.fillMaxSize(), contentPadding = PaddingValues(
                    start = 15.dp, top = 0.dp, end = 15.dp, bottom = 70.dp
                ), verticalArrangement = Arrangement.spacedBy(17.dp)
            ) {
                item {
                    Text(
                        text = stringResource(id = R.string.setup),
                        style = MaterialTheme.typography.displayMedium
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f))
                    )
                }
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(id = R.string.user_data_policy),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                item {
                    Text(
                        text = stringResource(id = R.string.setup_desc),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                item {
                    PreferenceSubtitle(
                        text = "Mandatory",
                        modifier = Modifier,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                item {
                    SetupCard(
                        title = "Usage Access Permission",
                        description = stringResource(id = R.string.usage_access_desc),
                        status = usageAccessStatus.value
                    ) {
                        context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                    }
                }
                item {
                    SetupCard(
                        title = "Notification Access Permission",
                        description = "Notification Access is needed for app to extract media information",
                        status = mediaControlStatus.value
                    ) {
                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    item {
                        SetupCard(
                            title = "Grant Permission to Show Notification",
                            description = stringResource(id = R.string.request_for_permission),
                            status = notificationPostingPerm
                        ) {
                            launcher.launch(POST_NOTIFICATIONS)
                        }
                    }
                } else {
                    item {
                        SetupCard(
                            title = "Grant Storage Access Permission",
                            description = stringResource(id = R.string.request_for_permission),
                            status = storagePermissionState.status.isGranted
                        ) {
                            when (storagePermissionState.status) {
                                PermissionStatus.Granted -> {}
                                is PermissionStatus.Denied -> {
                                    storagePermissionState.launchPermissionRequest()
                                }
                            }
                        }
                    }
                }

                item {
                    PreferenceSubtitle(
                        text = "Optional",
                        modifier = Modifier,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                item {
                    SetupCard(title = stringResource(id = R.string.account),
                        description = user?.username ?: stringResource(id = R.string.login_with_discord)
                        ) {
                        navigateToLogin()
                    }
                }
                item {
                    SetupCard(title = stringResource(id = R.string.language),
                        description = buildString {
                            Prefs.languages.keys.forEach { key ->
                                this.append(Prefs.getLanguageDesc(key) + ", ")
                            }
                        }) {
                        navigateToLanguages()
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(end = 15.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { navigateToHome() }, enabled = notificationPostingPerm && usageAccessStatus.value || mediaControlStatus.value) {
                    val text =
                        if (usageAccessStatus.value && mediaControlStatus.value) "Start App Now" else "Skip"
                    val style =
                        if (usageAccessStatus.value && mediaControlStatus.value) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp
                        )
                    val color =
                        if (usageAccessStatus.value && mediaControlStatus.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondaryContainer
                    Text(
                        text = text,
                        maxLines = 1,
                        style = style.copy(fontWeight = FontWeight.SemiBold),
                        color = color
                    )
                }
            }
        }
    }
}