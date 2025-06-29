package com.my.kizzy.feature_settings

import android.app.StatusBarManager
import android.content.ComponentName
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.my.kizzy.domain.model.user.User
import com.my.kizzy.resources.R
import com.my.kizzy.ui.components.Subtitle
import com.my.kizzy.ui.components.chips

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDrawer(
    user: User?,
    showKizzyQuickieRequestItem: Boolean,
    componentName: ComponentName,
    navigateToProfile: () -> Unit,
    navigateToStyleAndAppearance: () -> Unit,
    navigateToLanguages: () -> Unit,
    navigateToAbout: () -> Unit,
    navigateToRpcSettings: () -> Unit,
    navigateToLogsScreen: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.offset(8.dp, (-8).dp)
                    ) {
                        Text(text = BuildConfig.VERSION_NAME)
                    }
                },
                modifier = Modifier.padding(top = 8.dp, bottom = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleSmall
                )
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.weight(8f)
            ) {
                item {
                    SettingsItemCard(
                        title = stringResource(id = R.string.display),
                        icon = Icons.Outlined.Palette
                    ) {
                        navigateToStyleAndAppearance()
                    }
                }
                item {
                    SettingsItemCard(
                        title = stringResource(R.string.language),
                        icon = Icons.Outlined.Language,
                    ) {
                        navigateToLanguages()
                    }
                }
                item {
                    SettingsItemCard(
                        title = stringResource(id = R.string.logs),
                        icon = Icons.Outlined.BugReport
                    ) {
                        navigateToLogsScreen()
                    }
                }
                item {
                    SettingsItemCard(
                        title = stringResource(id = R.string.settings),
                        icon = Icons.Outlined.Settings
                    ) {
                        navigateToRpcSettings()
                    }
                }
                item {
                    HorizontalDivider()
                }
                item {
                    Subtitle(
                        text = stringResource(id = R.string.drawer_subtitle_help),
                        modifier = Modifier
                    )
                }
                item {
                    SettingsItemCard(
                        title = stringResource(id = R.string.drawer_faq),
                        icon = Icons.AutoMirrored.Rounded.HelpOutline
                    ) {
                        uriHandler.openUri("https://kizzy.vercel.app/#FAQ")
                    }
                }
                item {
                    SettingsItemCard(
                        title = "Discord",
                        icon = ImageVector.vectorResource(id = R.drawable.ic_discord)
                    ) {
                        //Discord Server Link
                        uriHandler.openUri(chips.first().url)
                    }
                }
                item {
                    SettingsItemCard(
                        title = stringResource(id = R.string.about),
                        icon = Icons.Outlined.Info
                    ) {
                        navigateToAbout()
                    }
                }
                item {
                    RequestQsTile(
                        visible = showKizzyQuickieRequestItem,
                        componentName = componentName
                    )
                }
            }
            if (user != null) {
                ProfileCardSmall(user = user) { navigateToProfile() }
            }
        }
    }
}

@Composable
fun RequestQsTile(
    visible: Boolean = true,
    componentName: ComponentName,
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val ctx = LocalContext.current
    val label = stringResource(R.string.qs_tile_label)
    val statusBarManager: StatusBarManager = ctx.getSystemService(StatusBarManager::class.java)
    AnimatedVisibility(
        visible = visible,
        exit = fadeOut(tween(800))
    ) {
        SettingsItemCard(
            title = label,
            icon = Icons.Outlined.Star,
            selected = true,
        ) {
            statusBarManager.requestAddTileService(
                componentName,
                label,
                android.graphics.drawable.Icon.createWithResource(ctx, R.drawable.ic_tile_play),
                {},
            ) {}
        }
    }
}


@Composable
fun SettingsItemCard(
    title: String,
    icon: ImageVector,
    selected: Boolean = false,
    onClick: () -> Unit = {},
) {
    NavigationDrawerItem(
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp)
                    .copy(fontWeight = FontWeight.SemiBold),
            )
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier
                    .padding(end = 5.dp)
                    .size(28.dp)
            )
        },
        selected = selected,
        onClick = { onClick() }
    )
}

@Composable
fun ProfileCardSmall(
    user: User?,
    navigateToProfile: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp)),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        onClick = navigateToProfile
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                AsyncImage(
                    model = user?.getAvatarImage(),
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(50.dp)
                        .clip(CircleShape),
                    contentDescription = user?.username,
                    error = painterResource(R.drawable.error_avatar),
                    placeholder = painterResource(R.drawable.error_avatar),
                )
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Left
                            ).toSpanStyle()
                        ) {
                            append((user?.globalName ?: user?.username) + "\r\n")
                        }
                        withStyle(
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                textAlign = TextAlign.Left
                            ).toSpanStyle()
                        ) {
                            append(user?.username)
                            if (user?.discriminator != "0")
                                append("#${user?.discriminator}")
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun SettingsDrawerPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        SettingsDrawer(
            showKizzyQuickieRequestItem = false,
            componentName = ComponentName("", ""),
            user = null,
            navigateToProfile = {},
            navigateToStyleAndAppearance = {},
            navigateToLanguages = {},
            navigateToAbout = {},
            navigateToRpcSettings = {}
        ) {}
    }
}