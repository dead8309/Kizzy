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

package com.my.kizzy.feature_profile.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.my.kizzy.domain.model.rpc.RpcConfig
import com.my.kizzy.resources.R
import com.my.kizzy.ui.theme.DarkBlueBg
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ActivityRow(
    elapsed: Int,
    rpcConfig: RpcConfig?,
    showTs: Boolean,
    special: String?
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
                    imageModel = if (rpcConfig?.largeImg?.startsWith("attachments") == true) "https://media.discordapp.net/${rpcConfig.largeImg}" else
                        rpcConfig?.largeImg,
                    error = painterResource(id =  R.drawable.editing_rpc_pencil),
                    previewPlaceholder = R.drawable.editing_rpc_pencil,
                    contentDescription = null,
                    modifier = if (!rpcConfig?.largeImg.isNullOrEmpty())
                        Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(15.dp))
                    else
                        Modifier
                            .size(48.dp)
                            .background(DarkBlueBg, RoundedCornerShape(15.dp))
                )
                if (!rpcConfig?.smallImg.isNullOrEmpty()) {
                    GlideImage(
                        imageModel =
                        if (rpcConfig?.smallImg?.startsWith("attachments") == true)
                            "https://media.discordapp.net/${rpcConfig.largeImg}"
                        else
                            rpcConfig?.smallImg,
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
                    text = rpcConfig?.name.takeIf { !it.isNullOrEmpty() }?:"User Profile",
                    style = MaterialTheme.typography.titleSmall
                        .copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp
                        )
                )
                ProfileText(
                    text = rpcConfig?.details,
                    style = MaterialTheme.typography.titleSmall
                        .copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                )
                ProfileText(
                    text = rpcConfig?.state,
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
            ProfileButton(label = "Special Button", link = special)
        if (rpcConfig != null) {
            ProfileButton(label = rpcConfig.button1, link = rpcConfig.button1link )
            ProfileButton(label = rpcConfig.button2, link = rpcConfig.button2link )
        }

    }
}