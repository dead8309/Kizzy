package com.my.kizzy.ui.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SettingsSuggest
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.my.kizzy.R
import com.my.kizzy.data.remote.User
import com.my.kizzy.ui.common.PreferenceSubtitle
import com.my.kizzy.ui.screen.home.chips
import com.my.kizzy.ui.screen.profile.user.Base
import com.my.kizzy.ui.screen.settings.about.app_home_page
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun SettingsDrawer(
    user: User?,
    navigateToProfile: () -> Unit,
    navigateToStyleAndAppeareance: () -> Unit,
    navigateToAbout: () -> Unit,
    navigateToRpcSettings: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    Surface(modifier = Modifier
        .fillMaxHeight()
        .width(300.dp)) {
        Column(
            modifier = Modifier.padding(25.dp)
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 5.dp, bottom = 15.dp)
                    .weight(1f),
                text = stringResource(id = R.string.settings),
                style = MaterialTheme.typography.headlineLarge
            )
            LazyColumn (
                verticalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.weight(8f)){
                item {
                    SettingsItemCard(
                        title = stringResource(id = R.string.display),
                        description = stringResource(id = R.string.display_desc),
                        icon = Icons.Outlined.Palette
                    ){
                       navigateToStyleAndAppeareance()
                    }
                }
                item {
                    SettingsItemCard(
                        title = stringResource(id = R.string.rpc_settings),
                        description = stringResource(id = R.string.rpc_settings_desc),
                        icon = Icons.Outlined.SettingsSuggest
                    ) {
                        navigateToRpcSettings()
                    }
                }
                item {
                    Divider()
                }
                item { 
                    PreferenceSubtitle(
                        text = "HELP",
                        modifier = Modifier
                    )
                }
                item {
                    SettingsItemCard(
                        title = "FAQ",
                        description = "Help & FAQ",
                        icon = Icons.Rounded.HelpOutline
                    ) {
                        uriHandler.openUri("$app_home_page/#FAQ")
                    }
                }
                item {
                    SettingsItemCard(
                        title = "Discord",
                        description = "Join for updates",
                        icon = ImageVector.vectorResource(id = R.drawable.ic_discord)
                    ) {
                        //Discord Server Link
                        uriHandler.openUri(chips.first().url)
                    }
                }
                item {
                    SettingsItemCard(
                        title = stringResource(id = R.string.about),
                        description = stringResource(id = R.string.about_desc),
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


@Composable
fun SettingsItemCard(
    title: String,
    description: String = "",
    icon: ImageVector,
    onClick: () -> Unit = {},
) {
    ElevatedCard(modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(15.dp))
        .clickable { onClick() },) {
        Row(modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically){
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier
                    .padding(end = 5.dp)
                    .size(28.dp)
            )
            Column {
                with(MaterialTheme) {
                    Text(
                        text = title,
                        maxLines = 1,
                        style = typography.titleLarge.copy(fontSize = 20.sp).copy(fontWeight = FontWeight.SemiBold),
                    )
                    Text(
                        text = description,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = typography.bodyMedium,
                    )
                }
            }
        }
    }


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
            navigateToStyleAndAppeareance = {},
            navigateToAbout = {}
        ) {}
    }

}