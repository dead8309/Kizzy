/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * MediaRpc.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_media_rpc

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
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
import com.my.kizzy.preference.Prefs.MEDIA_RPC_ALBUM_NAME
import com.my.kizzy.preference.Prefs.MEDIA_RPC_APP_ICON
import com.my.kizzy.preference.Prefs.MEDIA_RPC_ARTIST_NAME
import com.my.kizzy.preference.Prefs.MEDIA_RPC_ENABLE_TIMESTAMPS
import com.my.kizzy.preference.Prefs.MEDIA_RPC_HIDE_ON_PAUSE
import com.my.kizzy.preference.Prefs.MEDIA_RPC_SHOW_PLAYBACK_STATE
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.AppsItem
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.SearchBar
import com.my.kizzy.ui.components.Subtitle
import com.my.kizzy.ui.components.SwitchBar
import com.my.kizzy.ui.components.preference.PreferenceSwitch
import com.my.kizzy.ui.components.preference.PreferencesHint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaRPC(
    onBackPressed: () -> Unit,
    state: MediaAppsState,
    hasNotificationAccess: Boolean,
    updateMediaAppEnabled: (String) -> Unit,
) {
    val context = LocalContext.current
    var mediaRpcRunning by remember { mutableStateOf(AppUtils.mediaRpcRunning()) }
    var isArtistEnabled by remember { mutableStateOf(Prefs[MEDIA_RPC_ARTIST_NAME, false]) }
    var isAlbumEnabled by remember { mutableStateOf(Prefs[MEDIA_RPC_ALBUM_NAME, false]) }
    var isAppIconEnabled by remember { mutableStateOf(Prefs[MEDIA_RPC_APP_ICON, false]) }
    var isTimestampsEnabled by remember { mutableStateOf(Prefs[MEDIA_RPC_ENABLE_TIMESTAMPS, false]) }
    var hideOnPause by remember { mutableStateOf(Prefs[MEDIA_RPC_HIDE_ON_PAUSE, false]) }
    var isShowPlaybackState by remember { mutableStateOf(Prefs[MEDIA_RPC_SHOW_PLAYBACK_STATE, false]) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })
    var searchText by remember { mutableStateOf("") }
    var isSearchBarVisible by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.main_mediaRpc),
                        style = MaterialTheme.typography.headlineLarge,
                    )
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
        Column(modifier = Modifier.padding(it)) {
            AnimatedVisibility(
                visible = !hasNotificationAccess
            ) {
                PreferencesHint(
                    title = stringResource(id = R.string.permission_required),
                    description = stringResource(id = R.string.request_for_notification_access),
                    icon = Icons.Default.Warning,
                ) {
                    if (!hasNotificationAccess) {
                        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                    }
                }
            }
            SwitchBar(
                title = stringResource(id = R.string.enable_mediaRpc),
                isChecked = mediaRpcRunning,
                enabled = hasNotificationAccess
            ) {
                mediaRpcRunning = !mediaRpcRunning
                when (mediaRpcRunning) {
                    true -> {
                        context.stopService(Intent(context, AppDetectionService::class.java))
                        context.stopService(Intent(context, CustomRpcService::class.java))
                        context.stopService(Intent(context, ExperimentalRpc::class.java))
                        context.startService(Intent(context, MediaRpcService::class.java))
                    }

                    false -> context.stopService(Intent(context, MediaRpcService::class.java))
                }
            }
            LazyColumn {
                item {
                    PreferenceSwitch(
                        title = stringResource(id = R.string.enable_artist_name),
                        icon = Icons.Default.Audiotrack,
                        isChecked = isArtistEnabled,
                    ) {
                        isArtistEnabled = !isArtistEnabled
                        Prefs[MEDIA_RPC_ARTIST_NAME] = isArtistEnabled
                    }
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(id = R.string.enable_album_name),
                        icon = Icons.Default.Album,
                        isChecked = isAlbumEnabled
                    ) {
                        isAlbumEnabled = !isAlbumEnabled
                        Prefs[MEDIA_RPC_ALBUM_NAME] = isAlbumEnabled
                    }
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(id = R.string.show_app_icon),
                        icon = Icons.Default.Apps,
                        isChecked = isAppIconEnabled,
                    ) {
                        isAppIconEnabled = !isAppIconEnabled
                        Prefs[MEDIA_RPC_APP_ICON] = isAppIconEnabled
                        if (isAppIconEnabled) {
                            isShowPlaybackState = false
                            Prefs[MEDIA_RPC_SHOW_PLAYBACK_STATE] = false
                        }
                    }
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(id = R.string.enable_timestamps),
                        icon = Icons.Default.Timer,
                        isChecked = isTimestampsEnabled,
                    ) {
                        isTimestampsEnabled = !isTimestampsEnabled
                        Prefs[MEDIA_RPC_ENABLE_TIMESTAMPS] = isTimestampsEnabled
                    }
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(id = R.string.show_playback_state),
                        icon = Icons.Default.PlayCircle,
                        isChecked = isShowPlaybackState,
                    ) {
                        isShowPlaybackState = !isShowPlaybackState
                        Prefs[MEDIA_RPC_SHOW_PLAYBACK_STATE] = isShowPlaybackState
                        if (isShowPlaybackState) {
                            isAppIconEnabled = false
                            Prefs[MEDIA_RPC_APP_ICON] = false
                        }
                    }
                }
                item {
                    PreferenceSwitch(
                        title = stringResource(id = R.string.hide_on_pause),
                        icon = Icons.Default.PauseCircle,
                        isChecked = hideOnPause,
                    ) {
                        hideOnPause = !hideOnPause
                        Prefs[MEDIA_RPC_HIDE_ON_PAUSE] = hideOnPause
                    }
                }
                item {
                    Subtitle(
                        text = "Apps",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp, 8.dp)
                    )
                }
                if (state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    items(state.apps.size, key = { idx -> state.apps[idx].pkg }) { i ->
                        if (searchText.isEmpty() || state.apps[i].name.contains(
                                searchText,
                                ignoreCase = true
                            ) || state.apps[i].pkg.contains(searchText, ignoreCase = true)
                        ) {
                            AppsItem(
                                name = state.apps[i].name,
                                pkg = state.apps[i].pkg,
                                isChecked = state.enabledApps[state.apps[i].pkg] ?: false,
                                onClick = { updateMediaAppEnabled(state.apps[i].pkg) }
                            )
                        } else {
                            Spacer(modifier = Modifier.height(0.dp))
                        }
                    }
                }
            }
        }
    }
}