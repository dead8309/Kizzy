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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.my.kizzy.domain.model.user.User
import com.my.kizzy.feature_home.feature.Features
import com.my.kizzy.feature_home.feature.HomeFeature
import com.my.kizzy.feature_home.feature.ToolTipContent
import com.my.kizzy.feature_rpc_base.services.KizzyTileService
import com.my.kizzy.feature_settings.SettingsDrawer
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.ChipSection
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
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
    var homeItems by remember {
        mutableStateOf(features)
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
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    Home(
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