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

package com.my.kizzy.feature_profile.ui.component

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.my.kizzy.domain.model.rpc.RpcConfig
import com.my.kizzy.domain.model.user.User
import com.my.kizzy.resources.R
import com.my.kizzy.ui.theme.DISCORD_LIGHT_DARK

const val NITRO_ICON = "https://cdn.discordapp.com/badge-icons/2ba85e8026a8614b640c2837bcdfe21b.png"
const val USER_BANNER = "https://discord.com/assets/97ac61a0b98fd6f01b4de370c9ccdb56.png"

@Composable
fun ProfileCard(
    user: User?,
    borderColors: List<Color> = listOf(Color(0xFFa3a1ed), Color(0xFFA77798)),
    backgroundColors: List<Color> = listOf(Color(0xFFC2C0FA), Color(0xFFFADAF0)),
    padding: Dp = 30.dp,
    type: String? = stringResource(id = R.string.user_profile_rpc_name),
    rpcConfig: RpcConfig? = null,
    showTs: Boolean = true,
) {
    Card(
        modifier = Modifier
            .padding(padding)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = Brush.verticalGradient(colors = backgroundColors)
            ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            4.dp, Brush.verticalGradient(colors = borderColors)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
        )
    ) {
        if (user != null) {
            Box {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.getBannerImage() ?: USER_BANNER)
                        .decoderFactory(
                            if (SDK_INT >= 28) {
                                ImageDecoderDecoder.Factory()
                            } else {
                                GifDecoder.Factory()
                            }
                        )
                        .build(),
                    contentScale = ContentScale.FillWidth,
                    placeholder = painterResource(R.drawable.broken_image),
                    contentDescription = "User Avatar"
                )

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(user.getAvatarImage())
                        .decoderFactory(
                            if (SDK_INT >= 28) {
                                ImageDecoderDecoder.Factory()
                            } else {
                                GifDecoder.Factory()
                            }
                        )
                        .build(),
                    placeholder = painterResource(id = R.drawable.error_avatar),
                    error = painterResource(id = R.drawable.error_avatar),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp, 64.dp, 16.dp, 6.dp)
                        .size(110.dp)
                        .border(
                            width = 8.dp,
                            color = borderColors.first(),
                            shape = CircleShape
                        )
                        .clip(CircleShape),
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(15.dp, 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                ) {
                    if (user.nitro == true) {
                        AsyncImage(
                            model = NITRO_ICON,
                            placeholder = painterResource(R.drawable.broken_image),
                            modifier = Modifier
                                .size(40.dp)
                                .padding(5.dp),
                            contentDescription = "Nitro Icon"
                        )
                    }
                    user.badges?.let {
                        it.forEach { badge ->
                            AsyncImage(
                                model = badge.icon,
                                placeholder = painterResource(R.drawable.broken_image),
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(5.dp),
                                contentDescription = badge.name
                            )
                        }
                    }
                }
            }
            Column(
                Modifier
                    .padding(15.dp, 5.dp, 15.dp, 15.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
            ) {
                ProfileText(
                    text = user.globalName ?: user.username,
                    style = MaterialTheme.typography.titleLarge.copy(),
                    modifier = Modifier.padding(20.dp, 12.dp, 20.dp, 0.dp)
                )
                ProfileText(
                    text = user.username + if (user.discriminator != "0") "#" + user.discriminator else "",
                    style = MaterialTheme.typography.labelSmall,
                    bold = false
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(19.dp, 0.dp, 19.dp, 5.dp)
                        .height(1.5.dp)
                        .background(Color(0xFFC2C0FA))
                )
                if (!user.bio.isNullOrEmpty()) {
                    ProfileText(
                        text = stringResource(id = R.string.user_profile_bio),
                        style = MaterialTheme.typography.titleSmall
                    )
                    ProfileText(
                        text = user.bio,
                        style = MaterialTheme.typography.bodyMedium,
                        bold = false
                    )
                }
                ProfileText(
                    text = type,
                    style = MaterialTheme.typography.titleSmall
                )
                ActivityRow(
                    rpcConfig = rpcConfig,
                    showTs = showTs,
                    special = user.special
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
    modifier: Modifier = Modifier.padding(20.dp, 4.dp),
) {
    if (!text.isNullOrEmpty()) {
        Text(
            text = text,
            style = if (!bold) style
            else style.copy(fontWeight = FontWeight.ExtraBold),
            modifier = modifier,
            color = Color.Black.copy(alpha = 0.9f)
        )
    }
}

@Composable
fun ProfileButton(label: String?, link: String?) {
    val uriHandler = LocalUriHandler.current
    if (!label.isNullOrEmpty()) {
        ElevatedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 0.dp, 20.dp, 0.dp),
            onClick = {
                if (!link.isNullOrEmpty()) {
                    uriHandler.openUri(link)
                }
            },
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = DISCORD_LIGHT_DARK,
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
    val user = User(
        accentColor = null,
        avatar = null,
        avatarDecoration = null,
        badges = null,
        banner = null,
        bannerColor = null,
        discriminator = null,
        id = null,
        publicFlags = null,
        username = "yizzK",
        special = null,
        verified = false
    )
    ProfileCard(user = user)
}

