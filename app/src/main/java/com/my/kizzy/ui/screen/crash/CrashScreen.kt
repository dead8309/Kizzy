package com.my.kizzy.ui.screen.crash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.blankj.utilcode.util.ClipboardUtils
import com.my.kizzy.R
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrashScreen(trace: String?) {
    val uriHandler = LocalUriHandler.current
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_crashed),
                        style = MaterialTheme.typography.headlineLarge
                            .copy(fontWeight = FontWeight.ExtraBold)
                    )
                },
                actions = {
                    IconButton(onClick = {
                        exitProcess(0)
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = Icons.Default.Close.name
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    ClipboardUtils.copyText("Kizzy CrashLog", trace)
                    val url = "https://discord.com/channels/948712005223735336/948712005731229748"
                    uriHandler.openUri(url)
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share Logs",
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(text = stringResource(id = R.string.share_crash_logs))
            }
        }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it))
        {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)) {
                Text(
                    text = stringResource(id = R.string.share_crash_logs_desc),
                    style = MaterialTheme.typography.titleMedium
                        .copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                ElevatedCard(modifier = Modifier
                    .fillMaxSize(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    )) {
                    LazyColumn {
                        item {
                            if (trace != null) {
                                Text(
                                    modifier = Modifier.padding(10.dp),
                                    text = trace,
                                    style = MaterialTheme.typography.bodyMedium
                                        .copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}