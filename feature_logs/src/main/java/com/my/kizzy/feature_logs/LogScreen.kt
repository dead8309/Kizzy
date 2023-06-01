/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * LogScreen.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_logs

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.my.kizzy.domain.model.logs.LogEvent
import com.my.kizzy.preference.Prefs
import com.my.kizzy.ui.components.KSwitch
import com.my.kizzy.ui.components.SearchBar
import com.my.kizzy.ui.theme.LogColors.color
import java.text.DateFormat
import java.util.*

@Composable
fun LogScreen(viewModel: LogsViewModel) {
    val lazyListState = rememberLazyListState()
    val logs by remember {
        derivedStateOf {
            viewModel.filter()
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { ToolBar(viewModel) }
    ) { paddingValues ->
        LaunchedEffect(viewModel.logs.size) {
            if (viewModel.logs.size > 0)
                lazyListState.animateScrollToItem(viewModel.logs.size - 1)
        }
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            itemsIndexed(
                logs,// viewModel.logs,
            ) { i, it ->
                if (viewModel.showCompat.value)
                    Text(
                        text = it.annotated(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (i % 2 == 0)
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) else MaterialTheme.typography.labelMedium.color
                        ),
                        modifier = Modifier.padding(4.dp),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 10
                    )
                else
                    LogsCard(it)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolBar(viewModel: LogsViewModel) {
    var menuClicked by remember {
        mutableStateOf(false)
    }
    TopAppBar(
        title = {
            if(viewModel.isSearchBarVisible.value) {
                SearchBar(
                    text = viewModel.filterStrings.value,
                    placeholder = "Search...",
                    onClose = { viewModel.isSearchBarVisible.value = false }
                ) {
                    viewModel.filterStrings.value = it
                }
            } else
                Text(
                    text = "Logs",
                    style = MaterialTheme.typography.headlineMedium
                )
        },
        actions = {
            if(!viewModel.isSearchBarVisible.value){
                IconButton(onClick = { viewModel.isSearchBarVisible.value = true }) {
                    Icon(Icons.Default.Search, "search")
                }
            }
            IconButton(onClick = { menuClicked = !menuClicked }) {
                Icon(
                    imageVector = Icons.Default.MoreVert, contentDescription = "menu"
                )
                DropdownMenu(expanded = menuClicked,
                    onDismissRequest = { menuClicked = !menuClicked }) {
                    DropdownMenuItem(onClick = {},
                        text = {
                            Row(
                                Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        viewModel.showCompat.value = !viewModel.showCompat.value
                                        Prefs[Prefs.SHOW_LOGS_IN_COMPACT_MODE] = viewModel.showCompat.value
                                    }, horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Text("Compact Mode")
                                KSwitch(checked = viewModel.showCompat.value)
                            }
                        })
                    DropdownMenuItem(
                        text = { Text("Clear") },
                        onClick = {
                            viewModel.clearLogs()
                            menuClicked = false
                        }
                    )
                }
            }
        })
}

/**
 * source https://github.com/wingio/Logra
 */
@Composable
fun LogsCard(logEvent: LogEvent) {
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawWithCache {
                    onDrawBehind {
                        drawRect(
                            color = logEvent.level.color(),
                            topLeft = Offset.Zero,
                            size = Size(12f, size.height),
                        )
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        logEvent.tag,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .clip(RoundedCornerShape(5.dp))
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp))
                            .padding(5.dp)
                    )

                    Text(
                        text = logEvent.text,
                        style = MaterialTheme.typography.labelMedium,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 10
                    )
                    Text(
                        text = DateFormat.getTimeInstance().format(Date(logEvent.createdAt)),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
    Spacer(
        modifier = Modifier
            .height(10.dp)
            .fillMaxWidth()
    )
}

@SuppressLint("SimpleDateFormat")
@Composable
fun LogEvent.annotated() = buildAnnotatedString {
    withStyle(
        style = SpanStyle(
            color = MaterialTheme.colorScheme.onBackground,
            background = level.color().copy(0.4f)
        )
    ) {
        append(" ${level.name[0]} ")
    }
    withStyle(
        style = SpanStyle(
            fontWeight = FontWeight.ExtraBold,
            background = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
        )
    ) {
        append(SimpleDateFormat("h:mm:ssa").format(createdAt))
    }
    append(" $tag: $text")
}