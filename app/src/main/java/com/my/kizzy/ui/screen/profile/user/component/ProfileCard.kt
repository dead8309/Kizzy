/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ProfileCard.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.screen.profile.user.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.my.kizzy.R
import com.my.kizzy.data.remote.User
import com.my.kizzy.preference.Prefs
import com.my.kizzy.ui.screen.custom.RpcIntent
import com.my.kizzy.ui.screen.profile.user.Base
import com.my.kizzy.ui.screen.settings.style.color
import com.my.kizzy.utils.Constants
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.delay

@Composable
fun ProfileCard(
    user: User?,
    padding: Dp = 30.dp,
    type: String = "USING KIZZY RICH PRESENCE",
    rpcData: RpcIntent? = null,
    showTs: Boolean = true
) {
    var elapsed by remember {
        mutableStateOf(0)
    }
    LaunchedEffect(elapsed) {
        if (elapsed == 60)
            elapsed = 0
        else {
            delay(1000)
            elapsed++
        }
    }
    val colors = user?.themeColors?.map {
        it.color
    }?: listOf("#FFA3A1ED".color, "#FFA77798".color)
    val profileBackground = listOf(
        colors.first(),
        colors.first().copy(alpha = 0.8f),
        colors.last().copy(alpha = 0.8f),
    )
    val profileBorder =  listOf(
        colors.first(),
        colors.last()
    )
    Card(
        modifier = Modifier
            .padding(padding)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = profileBackground
                )
            ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            5.dp, Brush.verticalGradient(colors = profileBorder)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        )
    ) {
        if (user != null) {
            Box {
                val avatar = user.avatar?.let {
                    if (it.startsWith("a_"))
                        "$Base/avatars/${user.id}/${it}.gif"
                    else
                        "$Base/avatars/${user.id}/${it}.png"
                }
                val banner = user.banner?.let {
                    if (it.startsWith("a_"))
                        "$Base/banners/${user.id}/${it}.gif"
                    else
                        "$Base/banners/${user.id}/${it}.png"
                }

                GlideImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    imageModel = banner ?: Constants.USER_BANNER,
                    previewPlaceholder = R.drawable.ic_profile_banner
                )

                GlideImage(
                    imageModel = avatar,
                    placeHolder = ImageBitmap.imageResource(id = R.drawable.error_avatar),
                    error = ImageBitmap.imageResource(id = R.drawable.error_avatar),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp, 64.dp, 16.dp, 6.dp)
                        .size(110.dp)
                        .border(
                            width = 8.dp,
                            color = profileBackground.first(),
                            shape = CircleShape
                        )
                        .clip(CircleShape),
                    previewPlaceholder = R.drawable.error_avatar
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(15.dp, 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(0.6f))
                ) {
                    if (Prefs[Prefs.USER_NITRO, false]) {
                        GlideImage(
                            imageModel = Constants.NITRO_ICON,
                            previewPlaceholder = R.drawable.editing_rpc_pencil,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(5.dp)
                        )
                    }
                    user.badges?.let {
                        it.forEach { badge ->
                            GlideImage(
                                imageModel = badge?.icon,
                                previewPlaceholder = R.drawable.editing_rpc_pencil,
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(5.dp)
                            )
                        }
                    }
                }
            }
            Column(
                Modifier
                    .padding(15.dp, 5.dp, 15.dp, 15.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(0.6f))
            ) {
                ProfileText(
                    text = user.username + "#" + user.discriminator,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(19.dp, 0.dp, 19.dp, 5.dp)
                        .height(1.5.dp)
                        .background(profileBackground.first())
                )
                ProfileText(
                    text = "ABOUT ME",
                    style = MaterialTheme.typography.titleSmall
                )
                ProfileText(
                    text = user.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    bold = false
                )
                ProfileText(
                    text = type,
                    style = MaterialTheme.typography.titleSmall
                )
                ActivityRow(
                    elapsed = elapsed,
                    rpcData = rpcData,
                    showTs = showTs,
                    special = Pair(colors.first(),user.special)
                )
            }
        }
    }
}

@Composable
fun ProfileText(
    text: String?,
    style: TextStyle,
    bold: Boolean = true,
) {
    if (text != null && text.isNotEmpty()) {
        Text(
            text = text,
            style = if (!bold) style
            else style.copy(fontWeight = FontWeight.ExtraBold),
            modifier = Modifier.padding(20.dp, 4.dp),
            color = Color.Black.copy(alpha = 0.9f)
        )
    }
}

@Composable
fun ProfileButton(label: String?, link: String?, color: Color) {
    val uriHandler = LocalUriHandler.current
    if(!label.isNullOrEmpty()) {
        ElevatedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 0.dp),
            onClick = {
                if (!link.isNullOrEmpty()) {
                    uriHandler.openUri(link)
                }
            },
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = color,
                contentColor = Color.White.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(label)
        }
    }
}

@Preview
@Composable
fun PreviewProfileCard() {
    ProfileCard(user = User())
}

