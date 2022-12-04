/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * UserScreen.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.screen.profile.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.my.kizzy.BuildConfig
import com.my.kizzy.R
import com.my.kizzy.common.Constants
import com.my.kizzy.data.remote.User
import com.my.kizzy.ui.common.AnimatedShimmer
import com.my.kizzy.ui.common.BackButton
import com.my.kizzy.ui.common.ShimmerProfileCard
import com.my.kizzy.ui.screen.custom.RpcIntent
import com.my.kizzy.ui.theme.DISCORD_LIGHT_DARK
import com.my.kizzy.ui.theme.DarkBlueBg
import com.my.kizzy.utils.Log.vlog
import com.my.kizzy.utils.Prefs
import com.my.kizzy.utils.Prefs.USER_BIO
import com.my.kizzy.utils.Prefs.USER_NITRO
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val Base = "https://cdn.discordapp.com"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    viewModel: UserViewModel,
    onBackPressed: () -> Unit
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(title = { },
                navigationIcon = { BackButton { onBackPressed() } })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (state.error.isNotEmpty()) {
                ProfileNetworkError(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .align(Alignment.TopCenter),
                    error = state.error
                )
            }
            if (state.loading) {
                AnimatedShimmer {
                    ShimmerProfileCard(brush = it)
                }
            } else {
                ProfileCard(state.user)
                Logout(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Are you sure ?",
                            actionLabel = "Yes",
                            duration = SnackbarDuration.Short,
                            withDismissAction = true
                        ).run {
                            when (this) {
                                SnackbarResult.ActionPerformed -> try {
                                    val runtime = Runtime.getRuntime()
                                    runtime.exec("pm clear ${BuildConfig.APPLICATION_ID}")
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    vlog.e("Error",e.message.toString())
                                }
                                SnackbarResult.Dismissed -> Unit
                            }
                        }

                    }

                }
            }
        }
    }
}

@Composable
fun ProfileNetworkError(
    modifier: Modifier,
    error: String
) {
    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        border = BorderStroke(0.5.dp, Color.Yellow)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.Warning,
                "networkError",
                modifier = Modifier.size(32.dp),
                tint = Color.Yellow
            )
            Text(
                text = "Could not Update User Profile:\n$error",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
            )
        }
    }
}

@Composable
fun ProfileCard(
    user: User?,
    borderColors: List<Color> = listOf(Color(0xFFa3a1ed), Color(0xFFA77798)),
    backgroundColors: List<Color> = listOf(Color(0xFFC2C0FA), Color(0xFFFADAF0)),
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
                            color = borderColors.first(),
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
                        .background(Color.White)
                ) {
                  if (Prefs[USER_NITRO, false]) {
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
                                imageModel = badge.icon,
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
                    .background(Color.White)
            ) {
                CardText(
                    text = user.username + "#" + user.discriminator,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(19.dp, 0.dp, 19.dp, 5.dp)
                        .height(1.5.dp)
                        .background(Color(0xFFC2C0FA))
                )
                CardText(
                    text = "ABOUT ME",
                    style = MaterialTheme.typography.titleSmall
                )
                CardText(
                    text = Prefs[USER_BIO, ""],
                    style = MaterialTheme.typography.bodyMedium,
                    bold = false
                )

                CardText(
                    text = type,
                    style = MaterialTheme.typography.titleSmall
                )
                RpcRow(
                    elapsed = elapsed,
                    rpcData = rpcData,
                    showTs = showTs,
                    special = user.special
                )
            }
        }
    }
}

@Composable
fun CardText(
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
fun RpcRow(
    elapsed: Int,
    rpcData: RpcIntent?,
    showTs: Boolean,
    special: String?
) {
    Column(Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(1.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp, 4.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(DarkBlueBg),
                contentAlignment = Alignment.Center
            ) {
                GlideImage(
                    imageModel = if (rpcData?.largeImg?.startsWith("attachments") == true) "https://media.discordapp.net/${rpcData.largeImg}" else
                        rpcData?.largeImg,
                    error = painterResource(id =  R.drawable.editing_rpc_pencil),
                    previewPlaceholder = R.drawable.editing_rpc_pencil,
                    contentDescription = null,
                    modifier = if (!rpcData?.largeImg.isNullOrEmpty())
                        Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(15.dp))
                    else
                        Modifier
                            .size(48.dp)
                            .background(DarkBlueBg, RoundedCornerShape(15.dp))
                )
                if (!rpcData?.smallImg.isNullOrEmpty()) {
                    GlideImage(
                        imageModel =
                        if (rpcData?.smallImg?.startsWith("attachments") == true)
                            "https://media.discordapp.net/${rpcData.largeImg}"
                        else
                            rpcData?.smallImg,
                        error = painterResource(id = R.drawable.ic_rpc_placeholder),
                        previewPlaceholder = R.drawable.ic_rpc_placeholder,
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
            Column {
                CardText(
                    text = rpcData?.name.takeIf { !it.isNullOrEmpty() }?:"User Profile",
                    style = MaterialTheme.typography.titleSmall
                        .copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp
                        )
                )
                CardText(
                    text = rpcData?.details,
                    style = MaterialTheme.typography.titleSmall
                        .copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                )
                CardText(
                    text = rpcData?.state,
                    style = MaterialTheme.typography.titleSmall
                        .copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                )
                if(showTs) {
                    CardText(
                        text = "00:$elapsed",
                        style = MaterialTheme.typography.titleSmall
                            .copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                    )
                }
            }
        }
        if (showTs)
            ProfileButton(label = "Special Button", link = special)
        if (rpcData != null) {
            ProfileButton(label = rpcData.button1, link = rpcData.button1link )
            ProfileButton(label = rpcData.button2, link = rpcData.button2link )
        }

    }
}

@Composable
fun ProfileButton(label: String?, link: String?) {
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
                containerColor = DISCORD_LIGHT_DARK,
                contentColor = Color.White.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(label)
        }
    }
}

@Composable
fun Logout(
    modifier: Modifier = Modifier,
    shape: Shape,
    onClicked: () -> Unit,
) {
    Button(
        modifier = modifier,
        shape = shape,
        onClick = { onClicked() },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Text(text = "Logout")
    }
}