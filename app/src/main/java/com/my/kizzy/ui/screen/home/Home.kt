package com.my.kizzy.ui.screen.home

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import com.google.gson.Gson
import com.my.kizzy.BuildConfig
import com.my.kizzy.R
import com.my.kizzy.data.remote.User
import com.my.kizzy.service.AppDetectionService
import com.my.kizzy.service.CustomRpcService
import com.my.kizzy.service.ExperimentalRpc
import com.my.kizzy.service.MediaRpcService
import com.my.kizzy.ui.components.KSwitch
import com.my.kizzy.ui.Routes
import com.my.kizzy.ui.screen.profile.user.Base
import com.my.kizzy.ui.screen.settings.SettingsDrawer
import com.my.kizzy.utils.AppUtils
import com.my.kizzy.preference.Prefs
import com.my.kizzy.preference.Prefs.USER_DATA
import com.my.kizzy.utils.fromJson
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    hasUsageAccess: MutableState<Boolean>,
    hasNotificationAccess: MutableState<Boolean>,
    navigateTo: (String) -> Unit
) {
    val user: User? = Gson().fromJson(Prefs[USER_DATA, ""])
    val avatar = user?.let { _user ->
        _user.avatar?.let {
            if (it.startsWith("a_")) "$Base/avatars/${_user.id}/${it}.gif"
            else "$Base/avatars/${_user.id}/${it}.png"
        }
    }
    val ctx = LocalContext.current
    val features = listOf(
        HomeItem(
            title = "App Detection",
            icon = R.drawable.ic_apps,
            route = Routes.APPS_DETECTION,
            isChecked = AppUtils.appDetectionRunning(),
            showSwitch = hasUsageAccess.value,
            onClick = {
                navigateTo(it)
            },
            onCheckedChange = {
                if (it) {
                    ctx.stopService(Intent(ctx, CustomRpcService::class.java))
                    ctx.stopService(Intent(ctx, ExperimentalRpc::class.java))
                    ctx.stopService(Intent(ctx, MediaRpcService::class.java))
                    ctx.startService(Intent(ctx, AppDetectionService::class.java))
                } else ctx.stopService(Intent(ctx, AppDetectionService::class.java))
            },
            shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp)
        ), HomeItem(
            title = "Media Rpc",
            icon = R.drawable.ic_media_rpc,
            route = Routes.MEDIA_RPC,
            isChecked = AppUtils.mediaRpcRunning(),
            showSwitch = hasNotificationAccess.value,
            onClick = {
                navigateTo(it)
            },
            onCheckedChange = {
                if (it) {
                    ctx.stopService(Intent(ctx, CustomRpcService::class.java))
                    ctx.stopService(Intent(ctx, ExperimentalRpc::class.java))
                    ctx.stopService(Intent(ctx, AppDetectionService::class.java))
                    ctx.startService(Intent(ctx, MediaRpcService::class.java))
                } else ctx.stopService(Intent(ctx, MediaRpcService::class.java))
            },
            shape = RoundedCornerShape(44.dp, 20.dp, 44.dp, 20.dp)
        ), HomeItem(
            title = "Custom Rpc",
            icon = R.drawable.ic_rpc_placeholder,
            route = Routes.CUSTOM_RPC,
            isChecked = AppUtils.customRpcRunning(),
            onClick = {
                navigateTo(it)
            },
            onCheckedChange = {
                if (it) {
                    val lastRpc = Prefs[Prefs.LAST_RUN_CUSTOM_RPC, ""]
                    val intent = Intent(ctx, CustomRpcService::class.java)
                    intent.apply {
                        putExtra("RPC", lastRpc)
                    }
                    ctx.stopService(Intent(ctx, MediaRpcService::class.java))
                    ctx.stopService(Intent(ctx, ExperimentalRpc::class.java))
                    ctx.stopService(Intent(ctx, AppDetectionService::class.java))
                    ctx.startService(intent)
                } else ctx.stopService(Intent(ctx, CustomRpcService::class.java))
            },
            shape = RoundedCornerShape(44.dp, 20.dp, 44.dp, 20.dp),
            showSwitch = Prefs[Prefs.LAST_RUN_CUSTOM_RPC, ""].isNotEmpty()
        ), HomeItem(
            title = "Console Rpc",
            icon = R.drawable.ic_console_games,
            route = Routes.CONSOLE_RPC,
            isChecked = AppUtils.customRpcRunning(),
            onClick = {
                navigateTo(it)
            },
            onCheckedChange = {
                if (it) {
                    val lastRpc = Prefs[Prefs.LAST_RUN_CONSOLE_RPC, ""]
                    val intent = Intent(ctx, CustomRpcService::class.java)
                    intent.apply {
                        putExtra("RPC", lastRpc)
                    }
                    ctx.stopService(Intent(ctx, MediaRpcService::class.java))
                    ctx.stopService(Intent(ctx, ExperimentalRpc::class.java))
                    ctx.stopService(Intent(ctx, AppDetectionService::class.java))
                    ctx.startService(intent)
                } else ctx.stopService(Intent(ctx, CustomRpcService::class.java))
            },
            shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
            showSwitch = Prefs[Prefs.LAST_RUN_CONSOLE_RPC, ""].isNotEmpty()
        ),
        HomeItem(
            title = "Experimental Rpc",
            icon = R.drawable.ic_dev_rpc,
            isChecked = AppUtils.sharedRpcRunning(),
            onCheckedChange = {
                if (it) {
                    ctx.stopService(Intent(ctx, MediaRpcService::class.java))
                    ctx.stopService(Intent(ctx, CustomRpcService::class.java))
                    ctx.stopService(Intent(ctx, AppDetectionService::class.java))
                    ctx.startService(Intent(ctx, ExperimentalRpc::class.java))
                } else ctx.stopService(Intent(ctx, ExperimentalRpc::class.java))
            },
            shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
            showSwitch = hasUsageAccess.value && hasNotificationAccess.value,
            isVisible = BuildConfig.DEBUG || user?.verified == true
        ),
        HomeItem(
            title = "Coming Soon",
            icon = R.drawable.ic_info,
            shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
            showSwitch = false
        )
    )
    var homeItems by remember {
        mutableStateOf(features)
    }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
                SettingsDrawer(
                    user = user,
                    navigateToProfile = {
                        navigateTo(Routes.PROFILE)
                    },
                    navigateToStyleAndAppeareance = {
                        navigateTo(Routes.STYLE_AND_APPEAREANCE)
                    },
                    navigateToAbout = {
                        navigateTo(Routes.ABOUT)
                    },
                    navigateToRpcSettings = {
                        navigateTo(Routes.RPC_SETTINGS)
                    },
                    navigateToLogsScreen = {
                        navigateTo(Routes.LOGS_SCREEN)
                    })
            }
        }) {
        Scaffold(
            topBar = {
                LargeTopAppBar(title = {
                    Text(
                        text = stringResource(id = R.string.welcome) + ", ${user?.username ?: ""}",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }, navigationIcon = {
                    IconButton(
                        onClick = { scope.launch { drawerState.open() } }//navigateTo(Routes.SETTINGS) },
                    ) {
                        Icon(
                            Icons.Outlined.Menu, Icons.Outlined.Menu.name
                        )
                    }
                }, actions = {
                    IconButton(onClick = { navigateTo(Routes.PROFILE) }) {
                        if (user != null) {
                            GlideImage(
                                imageModel = avatar,
                                modifier = Modifier
                                    .size(52.dp)
                                    .border(
                                        2.dp,
                                        MaterialTheme.colorScheme.secondaryContainer,
                                        CircleShape
                                    )
                                    .clip(CircleShape),
                                previewPlaceholder = R.drawable.error_avatar
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = Icons.Default.Person.name
                            )
                        }
                    }
                })
            },
        ){ paddingValues ->
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


@Composable
fun Features(
    homeItems: List<HomeItem> = emptyList(), onValueUpdate: (Int) -> Unit
) {
    val brush = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.secondaryContainer.copy(0.8f),
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
        )
    )
    val featureSize = (LocalConfiguration.current.screenWidthDp.dp / 2)
    FlowRow(
        mainAxisSize = SizeMode.Expand,
        mainAxisAlignment = FlowMainAxisAlignment.SpaceBetween
    ) {
        for (i in homeItems.indices) {
            if (homeItems[i].isVisible) {
                Box(modifier = Modifier
                    .size(featureSize)
                    .padding(9.dp)
                    .aspectRatio(1f)
                    .clip(homeItems[i].shape)
                    .background(
                        brush = if (homeItems[i].isChecked) {
                            Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.primaryContainer.copy(0.8f),
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                )
                            )
                        } else {
                            brush
                        }
                    )
                    .clickable { homeItems[i].route?.let { homeItems[i].onClick(it) } }) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(15.dp, 15.dp, 2.dp, 15.dp)
                    ) {
                        Icon(
                            tint = if (homeItems[i].isChecked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondary,
                            painter = painterResource(id = homeItems[i].icon),
                            contentDescription = "",
                            modifier = Modifier
                                .weight(1f)
                                .padding(5.dp, 0.dp)
                        )
                        Text(
                            text = homeItems[i].title,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.W500),
                            color = if (homeItems[i].isChecked) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.weight(2f)
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            if (homeItems[i].showSwitch) {
                                Text(
                                    text = if (homeItems[i].isChecked) stringResource(id = R.string.android_on)
                                    else stringResource(id = R.string.android_off),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.W600),
                                    color = if (homeItems[i].isChecked) MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                KSwitch(
                                    checked = homeItems[i].isChecked,
                                    modifier = Modifier.rotate(-90f),
                                    onClick = {
                                        homeItems[i].onCheckedChange(!homeItems[i].isChecked)
                                        onValueUpdate(i)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview
@Composable
fun HomeScreenPreview() {
    Home(
        hasUsageAccess = mutableStateOf(false),
        hasNotificationAccess = mutableStateOf(
            false
        ),
        navigateTo = {}
    )
}