package com.my.kizzy.ui.screen.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.my.kizzy.R
import com.my.kizzy.data.remote.User
import com.my.kizzy.service.AppDetectionService
import com.my.kizzy.service.CustomRpcService
import com.my.kizzy.service.MediaRpcService
import com.my.kizzy.ui.common.Routes
import com.my.kizzy.ui.screen.profile.user.Base
import com.my.kizzy.utils.AppUtils
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.USER_DATA
import com.my.kizzy.utils.fromJson
import com.skydoves.landscapist.glide.GlideImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navigateTo: (String) -> Unit
) {
    val user: User? = Gson().fromJson(Prefs[USER_DATA, ""])
    val avatar = user?.let { _user ->
        _user.avatar?.let {
            if (it.startsWith("a_")) "$Base/avatars/${_user.id}/${it}.gif"
            else "$Base/avatars/${_user.id}/${it}.png"
        }
    }
    Scaffold(topBar = {
        LargeTopAppBar(title = {
            Text(
                text = stringResource(id = R.string.welcome) + ", ${user?.username ?: ""}",
                style = MaterialTheme.typography.headlineLarge,
            )
        }, navigationIcon = {
            IconButton(
                onClick = { navigateTo(Routes.SETTINGS) },
            ) {
                Icon(
                    Icons.Outlined.Settings, Icons.Outlined.Settings.name
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
                                2.dp, MaterialTheme.colorScheme.secondaryContainer, CircleShape
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
    }) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ChipSection()
            Text(
                text = stringResource(id = R.string.features),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(start = 15.dp)
            )
            val ctx = LocalContext.current
            val features = listOf(
                HomeItem(
                    title = "App Detection",
                    icon = R.drawable.ic_apps,
                    route = Routes.APPS_DETECTION,
                    isChecked = AppUtils.appDetectionRunning(),
                    onClick = {
                        navigateTo(it)
                    },
                    onCheckedChange = {
                        if (it) ctx.startService(Intent(ctx, AppDetectionService::class.java))
                        else ctx.stopService(Intent(ctx, AppDetectionService::class.java))
                    },
                    shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp)
                ), HomeItem(
                    title = "Media Rpc",
                    icon = R.drawable.ic_media_rpc,
                    route = Routes.MEDIA_RPC,
                    isChecked = AppUtils.mediaRpcRunning(),
                    onClick = {
                        navigateTo(it)
                    },
                    onCheckedChange = {
                        if (it) ctx.startService(Intent(ctx, MediaRpcService::class.java))
                        else ctx.stopService(Intent(ctx, MediaRpcService::class.java))
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
                            ctx.startService(intent)
                        } else ctx.stopService(Intent(ctx, CustomRpcService::class.java))
                    },
                    shape = RoundedCornerShape(20.dp, 44.dp, 20.dp, 44.dp),
                    showSwitch = Prefs[Prefs.LAST_RUN_CONSOLE_RPC, ""].isNotEmpty()
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
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), modifier = Modifier.fillMaxHeight()
    ) {
        items(homeItems.size) { i ->
            Box(modifier = Modifier
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
                            Switch(checked = homeItems[i].isChecked, onCheckedChange = {
                                homeItems[i].onCheckedChange(it)
                                onValueUpdate(i)
                            },
                            modifier = Modifier.rotate(-90f)
                            )
                        }
                    }
                }
            }
        }
    }
}