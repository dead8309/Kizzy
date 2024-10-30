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

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    checkConnection: () -> Unit,
    showBadge: Boolean,
    features: List<HomeFeature>,
    user: User?,
    componentName: ComponentName? = null,
    navigateToProfile: () -> Unit,
    navigateToStyleAndAppearance: () -> Unit,
    navigateToLanguages: () -> Unit,
    navigateToAbout: () -> Unit,
    navigateToRpcSettings: () -> Unit,
    navigateToLogsScreen: () -> Unit,
) {
    val ctx = LocalContext.current
    var timestamp by remember { mutableStateOf(System.currentTimeMillis()) }
    var homeItems by remember(timestamp) { mutableStateOf(features) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    var isConnected by remember { mutableStateOf(true) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState(),
            canScroll = { true })
    val isCollapsed = scrollBehavior.state.collapsedFraction > 0.55f

    // Refresh home screen in case user turns off service from notification/Quickie
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                timestamp = System.currentTimeMillis()
                checkConnection()
            }
            else -> {}
        }
    }

    // Check for internet connectivity
    InternetConnectivityChanges(
        onAvailable = {
            checkConnection()
        },
        onLost = {
            isConnected = false
        }
    )

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
                            text = stringResource(id = R.string.welcome) + ", ${user?.globalName ?: user?.username ?: ""}",
                            style = if (isCollapsed) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.headlineLarge,
                            maxLines = if (isCollapsed) 1 else Int.MAX_VALUE,
                            overflow = if (isCollapsed) androidx.compose.ui.text.style.TextOverflow.Ellipsis else androidx.compose.ui.text.style.TextOverflow.Clip,
                            modifier = Modifier.padding(end = if (isCollapsed) 0.dp else 12.dp)
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
                                            .offset(8.dp, -14.dp)
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
                                            ctx.getString(R.string.update_check_for_update),
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
                                        ctx.getString(R.string.update_check_for_update),
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
                AnimatedVisibility(
                    visible = !isConnected,
                    enter = fadeIn(),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = TopAppBarDefaults.LargeAppBarExpandedHeight - TopAppBarDefaults.LargeAppBarCollapsedHeight)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_internet_connection),
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }
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
                            } else {
                                if (item.isChecked) {
                                    item.copy(isChecked = false)
                                } else {
                                    item
                                }
                            }
                        }
                    }
                }
            }
            when (state) {
                is HomeScreenState.LoadingCompleted -> {
                    if (showUpdateDialog) {
                        if (state.release.toVersion()
                                .whetherNeedUpdate(BuildConfig.VERSION_NAME.toVersion())
                        ) {
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
                            Toast.makeText(
                                ctx,
                                ctx.getString(R.string.update_no_updates_available),
                                Toast.LENGTH_SHORT
                            ).show()
                            showUpdateDialog = false
                        }
                    }
                }
                is HomeScreenState.ConnectionStatus -> {
                    isConnected = state.isConnected
                }
                else -> {}
            }
        }
    }
}

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer = LifecycleEventObserver { owner, event ->
            eventHandler.value(owner, event)
        }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun InternetConnectivityChanges(
    onAvailable: (network: Network) -> Unit, onLost: (network: Network) -> Unit,
) {
    val context = LocalContext.current
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCallback = remember {
        object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                onAvailable(network)
            }

            override fun onLost(network: Network) {
                onLost(network)
            }
        }
    }

    LaunchedEffect(key1 = context) {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    Home(
        state = HomeScreenState.Loading,
        checkForUpdates = {},
        checkConnection = {},
        showBadge = true,
        features = fakeFeatures,
        user = fakeUser,
        navigateToProfile = { },
        navigateToStyleAndAppearance = { },
        navigateToLanguages = { },
        navigateToAbout = { },
        navigateToRpcSettings = { }) {

    }
}

val fakeFeatures = listOf(
    HomeFeature(
        title = "App Detection",
        icon = R.drawable.ic_apps,
        shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
        tooltipText = ToolTipContent.APP_DETECTION_DOCS
    ), HomeFeature(
        title = "Media RPC",
        icon = R.drawable.ic_media_rpc,
        shape = RoundedCornerShape(44.dp, 20.dp, 44.dp, 20.dp),
        tooltipText = ToolTipContent.MEDIA_RPC_DOCS
    ), HomeFeature(
        title = "Custom RPC",
        icon = R.drawable.ic_rpc_placeholder,
        shape = RoundedCornerShape(44.dp, 20.dp, 44.dp, 20.dp),
        tooltipText = ToolTipContent.CUSTOM_RPC_DOCS
    ), HomeFeature(
        title = "Console RPC",
        icon = R.drawable.ic_console_games,
        shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
        tooltipText = ToolTipContent.CONSOLE_RPC_DOCS
    ),
    HomeFeature(
        title = "Experimental RPC",
        icon = R.drawable.ic_dev_rpc,
        shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
        tooltipText = ToolTipContent.EXPERIMENTAL_RPC_DOCS
    ),
    HomeFeature(
        title = "Samsung RPC",
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