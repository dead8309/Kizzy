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
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.my.kizzy.R
import com.my.kizzy.domain.model.Game
import com.my.kizzy.service.AppDetectionService
import com.my.kizzy.service.CustomRpcService
import com.my.kizzy.service.MediaRpcService
import com.my.kizzy.ui.common.AnimatedShimmer
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.ShimmerGameItems
import com.my.kizzy.ui.common.SwitchBar
import com.my.kizzy.ui.screen.custom.RpcIntent
import com.my.kizzy.ui.screen.nintendo.SearchBar
import com.my.kizzy.utils.AppUtils
import com.my.kizzy.utils.Prefs
import com.skydoves.landscapist.glide.GlideImage


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun GamesScreen(
    onBackPressed: () -> Unit,
    viewModel: GamesViewModel
) {
    val state = viewModel.state.value
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })

    var selected by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
    var isConsoleRpcRunning by remember {
        mutableStateOf(AppUtils.customRpcRunning())
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val intent = Intent(context, CustomRpcService::class.java)
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Console Rpc",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = { BackButton { onBackPressed() } },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()){
            if(state.success){
                LazyColumn(
                    Modifier
                        .padding(padding)
                        .fillMaxSize()) {

                    item {
                        SwitchBar(
                            title = stringResource(id = R.string.enable_console_rpc),
                            checked = isConsoleRpcRunning
                        ) {
                            isConsoleRpcRunning = !isConsoleRpcRunning
                            when (isConsoleRpcRunning) {
                                true -> {
                                    if (intent.hasExtra("RPC")) {
                                        Prefs[Prefs.LAST_RUN_CONSOLE_RPC] = intent.getStringExtra("RPC")
                                        context.stopService(
                                            Intent(
                                                context,
                                                AppDetectionService::class.java
                                            )
                                        )
                                        context.stopService(Intent(context, MediaRpcService::class.java))
                                        context.startService(intent)
                                    }
                                }
                                false -> context.stopService(Intent(context, CustomRpcService::class.java))
                            }
                        }
                    }
                    item {
                        SearchBar(
                            onInputValueChange = {
                                viewModel.onSearch(it)
                            },
                            text = state.searchText,
                            onSearchClicked = {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        )
                    }
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
                                    largeImg = info.large_image?:"",
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
            if(state.error.isNotBlank()) {
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
            if(state.isLoading) {
                Column(
                    Modifier
                        .padding(padding)
                        .fillMaxSize()) {
                    AnimatedShimmer {
                        ShimmerGameItems(brush = it)
                    }
                }
            }
        }
    }
}

@Composable
fun SingleChoiceGameItem(
    game: Game,
    selected: Boolean,
    onClick: (game: Game) -> Unit,
) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (selected)
                    MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable {
                onClick(game)
            },
            horizontalArrangement = Arrangement.SpaceEvenly) {
            GlideImage(
                imageModel = game.large_image,
                modifier = Modifier
                    .size(90.dp),
                previewPlaceholder = R.drawable.ic_console_games
            )
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
                    .size(40.dp),
                previewPlaceholder = R.drawable.ic_console_games
            )
        }
    }