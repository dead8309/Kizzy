package com.my.kizzy.ui.screen.nintendo

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.my.kizzy.R
import com.my.kizzy.service.AppDetectionService
import com.my.kizzy.service.CustomRpcService
import com.my.kizzy.service.MediaRpcService
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.SwitchBar
import com.my.kizzy.ui.screen.custom.IntentRpcData
import com.my.kizzy.utils.AppUtils
import com.skydoves.landscapist.glide.GlideImage


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NintendoScreen(
    onBackPressed: () -> Unit,
    viewModel: NintendoViewModel,
) {
    val state = viewModel.state
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true })

    var selected by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
    var isSwitchRpcRunning by remember {
        mutableStateOf(AppUtils.customRpcRunning(context = context))
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
                        text = "Nintendo Rpc",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                },
                navigationIcon = { BackButton { onBackPressed() } },
                scrollBehavior = scrollBehavior
            )
        }
    ) {
        LazyColumn(Modifier
            .padding(it)
            .fillMaxSize()) {

            item {
                SwitchBar(title = stringResource(id = R.string.enable_nintendoRpc), checked = isSwitchRpcRunning) {
                    isSwitchRpcRunning = !isSwitchRpcRunning
                    when (isSwitchRpcRunning) {
                        true -> {
                            if (intent.hasExtra("RPC")){
                                context.stopService(Intent(context,
                                    AppDetectionService::class.java))
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
                    onInputValueChange = { query ->
                        viewModel.onAction(
                            Actions.TextFieldInput(query)
                        )
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
                    games = game,
                    selected = game.title == selected
                ) { info ->
                    selected = game.title
                    val string = Gson().toJson(
                        IntentRpcData(
                            name = if (info.label == "wii") "Wii U" else "Nintendo Switch",
                            details = info.title,
                            state = "",
                            startTime = System.currentTimeMillis().toString(),
                            StopTime = "",
                            status = "",
                            button1 = "",
                            button2 = "",
                            button1Url = "",
                            button2Url = "",
                            largeImg = info.link,
                            smallImg = if (info.label == "nintendo")
                                "attachments/948828217312178227/1018855932496719932/default.png"
                            else "attachments/948828217312178227/1020010414576255017/default.png",
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

@Composable
fun SingleChoiceGameItem(
    games: GameItem,
    selected: Boolean,
    onClick: (game: GameItem) -> Unit,
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
                onClick(games)
            },
            horizontalArrangement = Arrangement.SpaceEvenly) {
            GlideImage(
                imageModel = games.link,
                modifier = Modifier
                    .size(90.dp),
                previewPlaceholder = R.drawable.ic_nintendo_switch
            )
            Column(
                modifier = Modifier
                    .weight(9f)
                    .padding(5.dp)
            ) {
                Text(
                    text = games.title,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis
                )
            }
            GlideImage(
                imageModel = if (games.label == "nintendo")
                    "https://media.discordapp.net/attachments/948828217312178227/1018855932496719932/default.png"
                else
                    "https://media.discordapp.net/attachments/948828217312178227/1020010414576255017/default.png",
                modifier = Modifier
                    .size(40.dp),
                previewPlaceholder = R.drawable.ic_nintendo_switch
            )
        }
    }