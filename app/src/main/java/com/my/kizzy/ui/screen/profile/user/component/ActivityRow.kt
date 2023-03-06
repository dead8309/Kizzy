/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ActivityRow.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.ui.screen.profile.user.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.my.kizzy.R
import com.my.kizzy.ui.screen.custom.RpcIntent
import com.my.kizzy.ui.theme.DarkBlueBg
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ActivityRow(
    elapsed: Int,
    rpcData: RpcIntent?,
    showTs: Boolean,
    special: Pair<Color, String?>
) {
    Column(
        Modifier.fillMaxWidth(),
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
                ProfileText(
                    text = rpcData?.name.takeIf { !it.isNullOrEmpty() }?:"User Profile",
                    style = MaterialTheme.typography.titleSmall
                        .copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp
                        )
                )
                ProfileText(
                    text = rpcData?.details,
                    style = MaterialTheme.typography.titleSmall
                        .copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                )
                ProfileText(
                    text = rpcData?.state,
                    style = MaterialTheme.typography.titleSmall
                        .copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                )
                if(showTs) {
                    ProfileText(
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
            ProfileButton(label = "Special Button", link = special.second, color = special.first)
        if (rpcData != null) {
            ProfileButton(label = rpcData.button1, link = rpcData.button1link, color = special.first )
            ProfileButton(label = rpcData.button2, link = rpcData.button2link, color = special.first)
        }

    }
}