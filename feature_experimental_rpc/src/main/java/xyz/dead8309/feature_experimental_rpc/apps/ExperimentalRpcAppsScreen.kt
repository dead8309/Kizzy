/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ExperimentalRpcAppsScreen.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package xyz.dead8309.feature_experimental_rpc.apps

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.BackButton
import com.my.kizzy.ui.components.KSwitch
import com.my.kizzy.ui.components.SearchBar
import xyz.dead8309.feature_experimental_rpc.ExperimentalRpcViewmodel
import xyz.dead8309.feature_experimental_rpc.UiEvent
import xyz.dead8309.feature_experimental_rpc.UiState

private val activityTypeMap = mapOf(
    0 to "Playing",
    1 to "Streaming",
    2 to "Listening",
    3 to "Watching",
    5 to "Competing"
)

@Composable
private fun loadAppIcon(packageName: String): ImageBitmap? {
    val context = LocalContext.current
    return remember(packageName) {
        try {
            val pm = context.packageManager
            val drawable = pm.getApplicationIcon(packageName)
            val bitmap = drawable.toBitmap(72, 72)
            bitmap.asImageBitmap()
        } catch (_: Exception) {
            null
        }
    }
}

@Composable
private fun getActivityTypeIcon(type: String): ImageVector {
    return when (type) {
        "Playing" -> Icons.Default.SportsEsports
        "Streaming" -> Icons.Default.LiveTv
        "Listening" -> Icons.Default.Headphones
        "Watching" -> Icons.Default.Visibility
        "Competing" -> Icons.Default.SportsMma
        else -> Icons.Default.Edit
    }
}

@Composable
private fun getActivityTypeColor(type: String): Color {
    return when (type) {
        "Playing" -> Color(0xFF1976D2)
        "Streaming" -> Color(0xFF9C27B0)
        "Listening" -> Color(0xFF43A047)
        "Watching" -> Color(0xFFFF9800)
        "Competing" -> Color(0xFFF44336)
        else -> MaterialTheme.colorScheme.primary
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExperimentalRpcAppsScreen(
    onBackPressed: () -> Unit,
    viewModel: ExperimentalRpcViewmodel,
    state: UiState = viewModel.uiState.collectAsState().value,
    onEvent: (UiEvent) -> Unit = viewModel::onEvent
) {
    val activityTypes = activityTypeMap.entries.toList()
    var searchText by remember { mutableStateOf("") }
    var isSearchBarVisible by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Selection") },
                navigationIcon = { BackButton(onClick = onBackPressed) },
                actions = {
                    if (isSearchBarVisible) {
                        Column(
                            modifier = Modifier.padding(10.dp)
                        ) {
                            SearchBar(
                                text = searchText,
                                onTextChanged = { searchText = it },
                                onClose = { isSearchBarVisible = false },
                                placeholder = stringResource(R.string.search_placeholder)
                            )
                        }
                    } else {
                        IconButton(onClick = { isSearchBarVisible = true }) {
                            Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (state.isAppsLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    items(state.installedApps.filter {
                        searchText.isEmpty() ||
                        it.name.contains(searchText, ignoreCase = true) ||
                        it.pkg.contains(searchText, ignoreCase = true)
                    }) { app ->
                        val isChecked = state.enabledApps[app.pkg] ?: false
                        val selectedTypeId = state.appActivityTypes[app.pkg] ?: 0
                        val selectedType = activityTypeMap[selectedTypeId] ?: "Playing"
                        var expanded by remember { mutableStateOf(false) }
                        val iconBitmap = loadAppIcon(app.pkg)

                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(12.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (iconBitmap != null) {
                                        Image(
                                            painter = BitmapPainter(iconBitmap),
                                            contentDescription = null,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_apps),
                                            contentDescription = null,
                                            modifier = Modifier.size(32.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = app.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = app.pkg,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                KSwitch(
                                    checked = isChecked,
                                    enable = true,
                                    onClick = { onEvent(UiEvent.ToggleAppEnabled(app.pkg)) }
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    SuggestionChip(
                                        onClick = { expanded = true },
                                        label = { Text(selectedType) },
                                        icon = {
                                            Icon(
                                                imageVector = getActivityTypeIcon(selectedType),
                                                contentDescription = null,
                                                modifier = Modifier.size(18.dp),
                                                tint = getActivityTypeColor(selectedType)
                                            )
                                        },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = getActivityTypeColor(selectedType).copy(alpha = 0.2f),
                                            labelColor = getActivityTypeColor(selectedType)
                                        ),
                                        border = null,
                                        modifier = Modifier.align(Alignment.CenterEnd)
                                    )

                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier
                                            .width(200.dp)
                                            .background(MaterialTheme.colorScheme.surface)
                                    ) {
                                        activityTypes.forEach { (id, type) ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = type,
                                                        color = getActivityTypeColor(type)
                                                    )
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = getActivityTypeIcon(type),
                                                        contentDescription = null,
                                                        tint = getActivityTypeColor(type)
                                                    )

                                                },
                                                trailingIcon = {
                                                    if (selectedTypeId == id) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    onEvent(UiEvent.SetAppActivityType(app.pkg, id.toString()))
                                                    expanded = false
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
        }
    }
}

