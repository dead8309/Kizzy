/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Home.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_home

import android.content.ComponentName
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.my.kizzy.domain.model.toVersion
import com.my.kizzy.domain.model.user.User
import com.my.kizzy.feature_home.feature.Features
import com.my.kizzy.feature_home.feature.HomeFeature
import com.my.kizzy.feature_home.feature.ToolTipContent
import com.my.kizzy.feature_rpc_base.services.KizzyTileService
import com.my.kizzy.feature_settings.SettingsDrawer
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.ChipSection
import com.my.kizzy.ui.components.UpdateDialog
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    state: HomeScreenState,
    checkForUpdates: () -> Unit,
    showBadge: Boolean,
    features: List<HomeFeature>,
    user: User?,
    componentName: ComponentName? = null,
    navigateToProfile: () -> Unit,
    navigateToStyleAndAppearance: () -> Unit,
    navigateToLanguages: () -> Unit,
    navigateToAbout: () -> Unit,
    navigateToRpcSettings: () -> Unit,
    navigateToLogsScreen: () -> Unit
) {
    val ctx = LocalContext.current
    var homeItems by remember {
        mutableStateOf(features)
    }
    var showUpdateDialog by remember {
        mutableStateOf(false)
    }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState(),
            canScroll = { true })
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                if (componentName != null) {
                    SettingsDrawer(
                        user = user,
                        showKizzyQuickieRequestItem = !KizzyTileService.tileAdded.value,
                        componentName = componentName,
                        navigateToProfile = navigateToProfile,
                        navigateToStyleAndAppearance = navigateToStyleAndAppearance,
                        navigateToLanguages = navigateToLanguages,
                        navigateToAbout = navigateToAbout,
                        navigateToRpcSettings = navigateToRpcSettings,
                        navigateToLogsScreen = navigateToLogsScreen
                    )
                }
            }
        }) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.welcome) + ", ${user?.username ?: ""}",
                            style = MaterialTheme.typography.headlineLarge,
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                        ) {
                            Icon(
                                Icons.Outlined.Menu, Icons.Outlined.Menu.name,
                            )
                        }
                    },
                    actions = {
                        if (showBadge) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape),
                                        containerColor = MaterialTheme.colorScheme.error,
                                        contentColor = MaterialTheme.colorScheme.onError,
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Update,
                                    contentDescription = "Update",
                                    modifier = Modifier.clickable {
                                        Toast.makeText(
                                            ctx,
                                            "Checking for updates...",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        checkForUpdates()
                                        showUpdateDialog = true
                                    }
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Update,
                                contentDescription = "Update",
                                modifier = Modifier.clickable {
                                    Toast.makeText(
                                        ctx,
                                        "Checking for updates...",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    checkForUpdates()
                                    showUpdateDialog = true
                                }
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { navigateToProfile() }) {
                            if (user != null) {
                                GlideImage(
                                    imageModel = user.getAvatarImage(),
                                    modifier = Modifier
                                        .size(52.dp)
                                        .border(
                                            2.dp,
                                            MaterialTheme.colorScheme.secondaryContainer,
                                            CircleShape,
                                        )
                                        .clip(CircleShape),
                                    previewPlaceholder = R.drawable.error_avatar,
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = Icons.Default.Person.name,
                                )
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ChipSection()
                    Text(
                        text = stringResource(id = R.string.features),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                }
                item {
                    Features(homeItems) {
                        homeItems = homeItems.mapIndexed { j, item ->
                            if (it == j) {
                                item.copy(isChecked = !item.isChecked)
                            } else item
                        }
                    }
                }
            }
            when (state) {
                is HomeScreenState.LoadingCompleted -> {
                    if (state.release.toVersion().whetherNeedUpdate(BuildConfig.VERSION_NAME.toVersion()) && showUpdateDialog) {
                        with(state.release) {
                            UpdateDialog(
                                newVersionPublishDate = publishedAt ?: "",
                                newVersionSize = assets?.getOrNull(0)?.size ?: 0,
                                newVersionLog = body ?: "",
                                onDismissRequest = {
                                    showUpdateDialog = false
                                },
                            )
                        }
                    } else {
                        Toast.makeText(ctx, "No updates available", Toast.LENGTH_SHORT).show()
                    }
                }
                else -> {}
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    Home(
        state = HomeScreenState.Loading,
        checkForUpdates = {},
        showBadge = true,
        features = fakeFeatures,
        user = fakeUser,
        navigateToProfile = {  },
        navigateToStyleAndAppearance = {  },
        navigateToLanguages = {  },
        navigateToAbout = {  },
        navigateToRpcSettings = {  }) {

    }
}
val fakeFeatures = listOf(
    HomeFeature(
        title = "App Detection",
        icon = R.drawable.ic_apps,
        shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
        tooltipText = ToolTipContent.APP_DETECTION_DOCS
    ), HomeFeature(
        title = "Media Rpc",
        icon = R.drawable.ic_media_rpc,
        shape = RoundedCornerShape(44.dp, 20.dp, 44.dp, 20.dp),
        tooltipText = ToolTipContent.MEDIA_RPC_DOCS
    ), HomeFeature(
        title = "Custom Rpc",
        icon = R.drawable.ic_rpc_placeholder,
        shape = RoundedCornerShape(44.dp, 20.dp, 44.dp, 20.dp),
        tooltipText = ToolTipContent.CUSTOM_RPC_DOCS
    ), HomeFeature(
        title = "Console Rpc",
        icon = R.drawable.ic_console_games,
        shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
        tooltipText = ToolTipContent.CONSOLE_RPC_DOCS
    ),
    HomeFeature(
        title = "Experimental Rpc",
        icon = R.drawable.ic_dev_rpc,
        shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
        tooltipText = ToolTipContent.EXPERIMENTAL_RPC_DOCS
    ),
    HomeFeature(
        title = "Samsung Rpc",
        icon = R.drawable.ic_samsung_logo,
        shape = RoundedCornerShape(44.dp, 20.dp, 44.dp, 20.dp)
    ),
    HomeFeature(
        title = "Coming Soon",
        icon = R.drawable.ic_info,
        shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
        showSwitch = false
    )
)

val fakeUser = User(
    accentColor = null,
    avatar = null,
    avatarDecoration = null,
    badges = null,
    banner = null,
    bannerColor = null,
    discriminator = "3050",
    id = null,
    publicFlags = null,
    username = "yzziK",
    special = null,
    verified = false,
    nitro = true,
    bio = "Hello 👋"
)