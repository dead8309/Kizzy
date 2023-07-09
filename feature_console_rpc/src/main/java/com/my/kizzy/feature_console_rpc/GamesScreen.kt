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

package com.my.kizzy.feature_console_rpc

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
import com.my.kizzy.domain.model.Game
import com.my.kizzy.domain.model.rpc.RpcConfig
import com.my.kizzy.feature_rpc_base.services.AppDetectionService
import com.my.kizzy.feature_rpc_base.services.CustomRpcService
import com.my.kizzy.feature_rpc_base.services.ExperimentalRpc
import com.my.kizzy.feature_rpc_base.services.MediaRpcService
import com.my.kizzy.preference.Prefs
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.SearchBar
import com.my.kizzy.ui.components.SwitchBar
import com.my.kizzy.ui.components.shimmer.AnimatedShimmer
import com.my.kizzy.ui.components.shimmer.ShimmerGamesScreen
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamesScreen(
    onBackPressed: () -> Unit,
    onEvent: (UiEvent) -> Unit,
    state: GamesState,
    serviceEnabled: Boolean,
    isSearchBarVisible: Boolean
) {
    var selected by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
    var isConsoleRpcRunning by remember {
        mutableStateOf(serviceEnabled)
    }
    var searchText by remember { mutableStateOf("") }

    val intent = Intent(context, CustomRpcService::class.java)
    Scaffold(Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchBarVisible) {
                        SearchBar(
                            onTextChanged = {
                                searchText  = it
                                onEvent(UiEvent.Search(it))
                            },
                            text = searchText,
                            placeholder = "Search",
                            onClose = {
                                onEvent(UiEvent.CloseSearchBar)
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
                        if(!isSearchBarVisible) {
                            IconButton(onClick = { onEvent(UiEvent.OpenSearchBar) }) {
                                Icon(Icons.Default.Search, "search")
                            }
                        }
                },
                navigationIcon = { BackButton { onBackPressed() } }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (state) {
                is GamesState.Error -> {
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
                        Button(onClick = { onEvent(UiEvent.TryAgain) }) {
                            Text(text = "Try Again")
                        }
                    }
                }
                GamesState.Loading -> {
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

                is GamesState.Success -> {
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
                                    val string = Json.encodeToString(
                                        RpcConfig(
                                            name = info.platform,
                                            details = info.game_title,
                                            timestampsStart = System.currentTimeMillis().toString(),
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
                    previewPlaceholder = R.drawable.ic_console_games
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
                previewPlaceholder = R.drawable.ic_console_games
            )
        }
    }
}

// <-------------- Previews --------------->
private val fakeGames = buildList {
    repeat(10) {
        this.add(
            Game(
                platform = "nintendo",
                small_image = "",
                large_image = "",
                game_title = "Assassin's creed:"
            )
        )
    }
}

@Preview
@Composable
fun GamesScreenPreview() {
    GamesScreen(
        onEvent = {},
        onBackPressed = {},
        state = GamesState.Loading,
        isSearchBarVisible = false,
        serviceEnabled = false
    )
}
@Preview
@Composable
fun GamesScreenPreview2() {
    GamesScreen(
        onEvent = {},
        onBackPressed = {},
        state = GamesState.Error("No Internet Connection"),
        isSearchBarVisible = false,
        serviceEnabled = false
    )
}
@Preview
@Composable
fun GamesScreenPreview3() {
    GamesScreen(
        onEvent = {},
        onBackPressed = {},
        state = GamesState.Success(
            games = fakeGames
        ),
        isSearchBarVisible = false,
        serviceEnabled = false
    )
}
@Preview
@Composable
fun GamesScreenPreview4() {
    GamesScreen(
        onEvent = {},
        onBackPressed = {},
        state = GamesState.Loading,
        isSearchBarVisible = true,
        serviceEnabled = true
    )
}
@Preview
@Composable
fun GamesScreenPreview5() {
    GamesScreen(
        onEvent = {},
        onBackPressed = {},
        state = GamesState.Error("No Internet Connection"),
        isSearchBarVisible = true,
        serviceEnabled = true
    )
}
@Preview
@Composable
fun GamesScreenPreview6() {
    GamesScreen(
        onEvent = {},
        onBackPressed = {},
        state = GamesState.Success(games = fakeGames),
        isSearchBarVisible = true,
        serviceEnabled = true
    )
}