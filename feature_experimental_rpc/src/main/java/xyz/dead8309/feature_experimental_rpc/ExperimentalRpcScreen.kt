/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ExperimentalRpcScreen.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package xyz.dead8309.feature_experimental_rpc

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.AppSettingsAlt
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.my.kizzy.data.rpc.TemplateKeys
import com.my.kizzy.feature_rpc_base.AppUtils
import com.my.kizzy.feature_rpc_base.services.AppDetectionService
import com.my.kizzy.feature_rpc_base.services.CustomRpcService
import com.my.kizzy.feature_rpc_base.services.ExperimentalRpc
import com.my.kizzy.feature_rpc_base.services.MediaRpcService
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.RpcFieldWithCompletions
import com.my.kizzy.ui.components.SettingItem
import com.my.kizzy.ui.components.Subtitle
import com.my.kizzy.ui.components.SwitchBar
import com.my.kizzy.ui.components.preference.PreferenceSwitch

private val completions = listOf(
    TemplateKeys.MEDIA_TITLE to R.string.completion_media_title,
    TemplateKeys.MEDIA_ARTIST to R.string.completion_media_artist,
    TemplateKeys.MEDIA_AUTHOR to R.string.completion_media_author,
    TemplateKeys.APP_NAME to R.string.completion_app_name,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperimentalRpcScreen(
    onBackPressed: () -> Unit,
    state: UiState,
    navigateToAppSelection: () -> Unit,
    onEvent: (UiEvent) -> Unit,
) {
    val context = LocalContext.current
    var experimentalRpcRunning by remember { mutableStateOf(AppUtils.experimentalRpcRunning()) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.main_experimentalRpc),
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = { BackButton { onBackPressed() } },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->

        Column(modifier = Modifier.padding(paddingValues)) {
            SwitchBar(
                title = stringResource(id = R.string.enable_experimental_rpc),
                isChecked = experimentalRpcRunning,
            ) {
                experimentalRpcRunning = !experimentalRpcRunning
                when (experimentalRpcRunning) {
                    true -> {
                        context.stopService(Intent(context, AppDetectionService::class.java))
                        context.stopService(Intent(context, CustomRpcService::class.java))
                        context.stopService(Intent(context, MediaRpcService::class.java))
                        context.startService(Intent(context, ExperimentalRpc::class.java))
                    }

                    false -> context.stopService(Intent(context, ExperimentalRpc::class.java))
                }
            }
            LazyColumn {
                item {
                    Subtitle(text = stringResource(R.string.general_settings))
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(R.string.enable_appsRpc),
                        description = stringResource(R.string.experimental_rpc_detect_apps),
                        isChecked = state.isAppsRpcPartEnabled,
                        onClick = {
                            onEvent(UiEvent.ToggleAppsRpcPart(!state.isAppsRpcPartEnabled))
                        },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_apps),
                                modifier = Modifier
                                    .padding(start = 8.dp, end = 16.dp)
                                    .size(24.dp),
                                contentDescription = null
                            )
                        },
                    )
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(R.string.enable_mediaRpc),
                        description = stringResource(R.string.experimental_rpc_detect_media),
                        isChecked = state.isMediaRpcPartEnabled,
                        onClick = {
                            onEvent(UiEvent.ToggleMediaRpcPart(!state.isMediaRpcPartEnabled))
                        },
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_media_rpc),
                                modifier = Modifier
                                    .padding(start = 8.dp, end = 16.dp)
                                    .size(24.dp),
                                contentDescription = null
                            )
                        },
                    )
                }
                item {
                    SettingItem(
                        title = stringResource(R.string.app_preferences),
                        description = stringResource(R.string.app_preferences_desc),
                        icon = Icons.Outlined.AppSettingsAlt
                    ) {
                        navigateToAppSelection()
                    }
                }
                item {
                    Subtitle(
                        text = stringResource(R.string.main_mediaRpc)
                    )
                }

                // Media Display Options
                item {
                    PreferenceSwitch(
                        title = stringResource(R.string.show_cover_art),
                        description = stringResource(R.string.show_cover_art_desc),
                        isChecked = state.showCoverArt,
                        onClick = {
                            onEvent(UiEvent.ToggleShowCoverArt(!state.showCoverArt))
                        },
                        icon = Icons.Outlined.Image,
                    )
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(R.string.show_app_icon),
                        isChecked = state.showAppIcon,
                        onClick = {
                            onEvent(UiEvent.ToggleShowAppIcon(!state.showAppIcon))
                        },
                        icon = Icons.Outlined.Apps,
                    )
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(R.string.show_playback_state),
                        isChecked = state.showPlaybackState,
                        onClick = {
                            onEvent(UiEvent.ToggleShowPlaybackState(!state.showPlaybackState))
                        },
                        icon = Icons.Default.PlayCircle
                    )
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(R.string.enable_timestamps),
                        isChecked = state.enableTimestamps,
                        onClick = {
                            onEvent(UiEvent.ToggleEnableTimestamps(!state.enableTimestamps))
                        },
                        icon = Icons.Default.Timer
                    )
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(R.string.hide_on_pause),
                        isChecked = state.hideOnPause,
                        onClick = {
                            onEvent(UiEvent.ToggleHideOnPause(!state.hideOnPause))
                        },
                        icon = Icons.Default.PauseCircle
                    )
                }
                item {
                    Subtitle(text = stringResource(R.string.advance_settings))
                }
                item {
                    RpcFieldWithCompletions(
                        value = state.templateName,
                        label = R.string.activity_name,
                        onValueChange = { onEvent(UiEvent.SetTemplateName(it)) },
                        completionList = completions
                    )
                }
                item {
                    RpcFieldWithCompletions(
                        value = state.templateDetails,
                        label = R.string.activity_details,
                        onValueChange = { onEvent(UiEvent.SetTemplateDetails(it)) },
                        completionList = completions
                    )
                }
                item {
                    RpcFieldWithCompletions(
                        value = state.templateState,
                        label = R.string.activity_state,
                        onValueChange = { onEvent(UiEvent.SetTemplateState(it)) },
                        completionList = completions
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                        )
                        Text(
                            text = stringResource(R.string.rpc_templates_note),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Light,
                        )
                    }
                }
            }
        }
    }
}


