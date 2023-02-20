/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * GamesScreen.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.screen.console_games

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.my.kizzy.R
import com.my.kizzy.utils.Constants
import com.my.kizzy.domain.model.Game
import com.my.kizzy.service.AppDetectionService
import com.my.kizzy.service.CustomRpcService
import com.my.kizzy.service.ExperimentalRpc
import com.my.kizzy.service.MediaRpcService
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.SearchBar
import com.my.kizzy.ui.components.SwitchBar
import com.my.kizzy.ui.components.shimmer.AnimatedShimmer
import com.my.kizzy.ui.components.shimmer.ShimmerGamesScreen
import com.my.kizzy.ui.screen.custom.RpcIntent
import com.my.kizzy.utils.AppUtils
import com.my.kizzy.preference.Prefs
import com.skydoves.landscapist.glide.GlideImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(
    onBackPressed: () -> Unit,
    viewModel: GamesViewModel
) {
    val state = viewModel.state.value

    var selected by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
    var isConsoleRpcRunning by remember {
        mutableStateOf(AppUtils.customRpcRunning())
    }
    val intent = Intent(context, CustomRpcService::class.java)
    Scaffold(Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    if (viewModel.isSearchBarVisible.value) {
                        SearchBar(
                            onTextChanged = {
                                viewModel.onSearch(it)
                            },
                            text = state.searchText,
                            placeholder = "Search",
                            onClose = {
                                viewModel.isSearchBarVisible.value = false
                            }
                        )
                    }
                    else {
                        Text(
                            text = "Console Rpc",
                            style = MaterialTheme.typography.headlineLarge,
                        )
                    }
                },
                actions = {
                        if(!viewModel.isSearchBarVisible.value) {
                            IconButton(onClick = { viewModel.isSearchBarVisible.value = true }) {
                                Icon(Icons.Default.Search, "search")
                            }
                        }
                },
                navigationIcon = { BackButton { onBackPressed() } }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.success) {
                Column(Modifier.fillMaxSize().padding(padding)) {
                    SwitchBar(
                        title = stringResource(id = R.string.enable_console_rpc),
                        isChecked = isConsoleRpcRunning
                    ) {
                        isConsoleRpcRunning = !isConsoleRpcRunning
                        when (isConsoleRpcRunning) {
                            true -> {
                                if (intent.hasExtra("RPC")) {
                                    Prefs[Prefs.LAST_RUN_CONSOLE_RPC] =
                                        intent.getStringExtra("RPC")
                                    context.stopService(
                                        Intent(
                                            context,
                                            AppDetectionService::class.java
                                        )
                                    )
                                    context.stopService(
                                        Intent(
                                            context,
                                            MediaRpcService::class.java
                                        )
                                    )
                                    context.stopService(
                                        Intent(
                                            context,
                                            ExperimentalRpc::class.java
                                        )
                                    )
                                    context.startService(intent)
                                }
                            }
                            false -> context.stopService(
                                Intent(
                                    context,
                                    CustomRpcService::class.java
                                )
                            )
                        }
                    }
                    LazyColumn {
                        items(state.games) { game ->
                            SingleChoiceGameItem(
                                game = game,
                                selected = game.game_title == selected
                            ) { info ->
                                selected = game.game_title
                                val string = Gson().toJson(
                                    RpcIntent(
                                        name = info.platform,
                                        details = info.game_title,
                                        timeatampsStart = System.currentTimeMillis().toString(),
                                        status = "dnd",
                                        largeImg = info.large_image ?: "",
                                        smallImg = info.small_image,
                                        type = "0",
                                    )
                                )
                                intent.apply {
                                    removeExtra("RPC")
                                    putExtra("RPC", string)
                                }
                            }
                        }
                    }
                }
            }
            if (state.error.isNotBlank()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    )
                    Button(onClick = { viewModel.getGames() }) {
                        Text(text = "Try Again")
                    }
                }
            }
            if (state.isLoading) {
                Column(
                    Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    AnimatedShimmer {
                        ShimmerGamesScreen(brush = it)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleChoiceGameItem(
    game: Game,
    selected: Boolean,
    onClick: (game: Game) -> Unit,
) {
    ElevatedCard(
        onClick = { onClick(game) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(25.dp))
    ) {
        Row(modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(15.dp))){
                GlideImage(
                    imageModel = game.large_image,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(15.dp)),
                    previewPlaceholder = R.drawable.error_avatar
                )
                androidx.compose.animation.AnimatedVisibility(
                    visible = selected,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                    exit = shrinkOut(shrinkTowards = Alignment.Center) + fadeOut()
                ) {
                    Icon(
                        Icons.Outlined.Check,
                        null,
                        modifier = Modifier
                            .size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(9f)
                    .padding(5.dp)
            ) {
                Text(
                    text = game.game_title,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis
                )
            }
            GlideImage(
                imageModel = game.small_image,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                previewPlaceholder = R.drawable.error_avatar
            )
        }
    }
}

@Preview
@Composable
fun ConsoleTest() {

    var test by remember {
        mutableStateOf(false)
    }
    LazyColumn(Modifier.fillMaxSize()) {

        items(10) {
            SingleChoiceGameItem(
                game = Game(
                    platform = Constants.NINTENDO,
                    small_image = "",
                    large_image = "",
                    game_title = "Assassin's creed:"
                ),
                selected = true
            ) {
                test = true
            }
        }
    }
}