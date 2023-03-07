package com.my.kizzy.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.my.kizzy.BuildConfig
import com.my.kizzy.R
import com.my.kizzy.data.remote.User
import com.my.kizzy.ui.components.Subtitle
import com.my.kizzy.ui.screen.home.chips
import com.my.kizzy.ui.screen.profile.user.Base
import com.my.kizzy.ui.screen.settings.about.app_home_page
import com.skydoves.landscapist.glide.GlideImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDrawer(
    user: User?,
    navigateToProfile: () -> Unit,
    navigateToStyleAndAppearance: () -> Unit,
    navigateToAbout: () -> Unit,
    navigateToRpcSettings: () -> Unit,
    navigateToLogsScreen: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    Surface(modifier = Modifier
        .fillMaxHeight()
        .width(300.dp)) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.tertiary,
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
            LazyColumn (
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.weight(8f)
            ){
                item {
                    SettingsItemCard(
                        title = stringResource(id = R.string.display),
                        icon = Icons.Outlined.Palette
                    ){
                       navigateToStyleAndAppearance()
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
                    SettingsItemCard(
                        title = "Logs",
                        icon = Icons.Outlined.BugReport
                    ) {
                        navigateToLogsScreen()
                    }
                }
                item {
                    Divider()
                }
                item { 
                    Subtitle(
                        text = "HELP",
                        modifier = Modifier
                    )
                }
                item {
                    SettingsItemCard(
                        title = "FAQ",
                        icon = Icons.Rounded.HelpOutline
                    ) {
                        uriHandler.openUri("$app_home_page/#FAQ")
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
            }
            if (user != null){
                ProfileCardSmall(user = user) { navigateToProfile() }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItemCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit = {},
) {
    NavigationDrawerItem(
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp).copy(fontWeight = FontWeight.SemiBold),
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
        selected = false,
        onClick = { onClick() }
    )
}

@Composable
fun ProfileCardSmall(
    user: User?,
    navigateToProfile: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp)),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
                GlideImage(
                    imageModel = "$Base/avatars/${user?.id}/${user?.avatar}.png",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    error = ImageBitmap.imageResource(id = R.drawable.error_avatar),
                    previewPlaceholder = R.drawable.error_avatar
                )
                Text(
                    modifier = Modifier
                        .weight(9f)
                        .padding(5.dp),
                    text = user?.username+"#"+user?.discriminator,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                    overflow = TextOverflow.Ellipsis
                )

            IconButton(onClick = { navigateToProfile() }) {
                Icon(
                    imageVector = Icons.Rounded.ArrowForwardIos,
                    modifier = Modifier.size(28.dp),
                    contentDescription = "Go to Profile",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Preview
@Composable
fun SettingsDrawerPreview() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.LightGray)){
        SettingsDrawer(
            user = null,
            navigateToProfile = {},
            navigateToStyleAndAppearance = {},
            navigateToAbout = {},
            navigateToRpcSettings = {}
        ) {}
    }

}