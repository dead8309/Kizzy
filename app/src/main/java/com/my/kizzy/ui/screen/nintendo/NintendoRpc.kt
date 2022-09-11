package com.my.kizzy.ui.screen.nintendo

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.my.kizzy.service.AppDetectionService
import com.my.kizzy.service.CustomRpcService
import com.my.kizzy.service.MediaRpcService
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.SwitchBar
import com.my.kizzy.ui.screen.custom.Rpc
import com.my.kizzy.utils.AppUtils
import com.skydoves.landscapist.glide.GlideImage


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NintendoRpc(onBackPressed: () -> Unit) {
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
                navigationIcon = { BackButton{ onBackPressed() } },
                scrollBehavior = scrollBehavior
            )
        }
    ){
        LazyColumn(Modifier
            .padding(it)
            .fillMaxSize()){

            item {
                SwitchBar(title = "Enable Switch Rpc", checked = isSwitchRpcRunning) {
                    isSwitchRpcRunning = !isSwitchRpcRunning
                    when (isSwitchRpcRunning) {
                        true -> {
                            context.stopService(Intent(context, AppDetectionService::class.java))
                            context.stopService(Intent(context, MediaRpcService::class.java))
                            context.startService(intent)
                        }
                        false -> context.stopService(Intent(context, CustomRpcService::class.java))
                    }
                }
            }
            items(getGamesData(context)){ game ->
                GameItem(
                    games = game,
                    selected = game.title == selected
                ){ info ->
                    selected = game.title
                    val string = Gson().toJson(
                        Rpc(
                            name = info.title,
                            details = "",
                            state = "",
                            startTime = "",
                            StopTime = "",
                            status = "",
                            button1 = "",
                            button2 = "",
                            button1Url = "",
                            button2Url = "",
                            largeImg = info.image,
                            smallImg = "",
                            type = "0",
                        )
                    )
                    intent.removeExtra("RPC")
                    intent.putExtra("RPC", string)
                }
            }

        }
    }
}

@Composable
fun GameItem(
    games: Games,
    selected: Boolean,
    onClick: (game: Games) -> Unit,
) {
    Surface(
        modifier = Modifier.clickable { onClick(games) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlideImage(
                imageModel = games.image,
                modifier = Modifier
                    .size(size = 70.dp)
                    .padding(10.dp),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            ) {
                Text(
                    text = games.title,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.onSurface, overflow = TextOverflow.Ellipsis
                )
            }
            RadioButton(
                selected = selected ,
                onClick = { onClick(games) },
                modifier = Modifier.padding(start = 20.dp, end = 6.dp),
            )
        }
    }
}