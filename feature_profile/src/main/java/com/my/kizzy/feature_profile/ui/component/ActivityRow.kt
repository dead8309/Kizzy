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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.my.kizzy.domain.model.rpc.RpcConfig
import com.my.kizzy.resources.R
import com.my.kizzy.ui.theme.DISCORD_BLURPLE
import kotlinx.coroutines.delay
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun ActivityRow(
    rpcConfig: RpcConfig?,
    showTs: Boolean,
    special: String?,
) {
    val elapsed by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var elapsedState by remember { mutableStateOf(formatTime(elapsed)) }
    val isPlaying =
        rpcConfig?.type.isNullOrEmpty() || rpcConfig?.type?.toIntOrNull()?.equals(0) == true
    val isPlayingMedia =
        rpcConfig?.type?.toIntOrNull()?.equals(2) == true || rpcConfig?.type?.toIntOrNull()
            ?.equals(3) == true

    // Media
    var elapsedText by remember { mutableStateOf("00:00") }
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(elapsedState) {
        while (true) {
            delay(1000)
            elapsedState = formatTime(elapsed)
            if (isPlayingMedia && rpcConfig?.timestampsStart?.toLongOrNull() != null && rpcConfig?.timestampsStop?.toLongOrNull() != null) {
                val startTime = rpcConfig.timestampsStart.toLong()
                val endTime = rpcConfig.timestampsStop.toLong()
                val difference = endTime - startTime
                val elapsed1 = System.currentTimeMillis() - startTime
                sliderPosition = (elapsed1.toFloat() / difference.toFloat())
                elapsedText = if (elapsed1 < difference) {
                    getFormatFromMs(elapsed1)
                } else {
                    getFormatFromMs(difference)
                }
            }
        }
    }


    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp, 4.dp)
        ) {

            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(DISCORD_BLURPLE),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = if (isAsset(rpcConfig?.largeImg)) "https://media.discordapp.net/${rpcConfig?.largeImg}" else
                        rpcConfig?.largeImg,
                    error = painterResource(id = R.drawable.editing_rpc_pencil),
                    placeholder = painterResource(R.drawable.editing_rpc_pencil),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    modifier = if (!rpcConfig?.largeImg.isNullOrEmpty())
                        Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(6.dp))
                    else
                        Modifier
                            .size(48.dp)
                            .background(DISCORD_BLURPLE, RoundedCornerShape(6.dp))
                )
                if (!rpcConfig?.smallImg.isNullOrEmpty()) {
                    AsyncImage(
                        model =
                        if (isAsset(rpcConfig?.smallImg))
                            "https://media.discordapp.net/${rpcConfig?.smallImg}"
                        else
                            rpcConfig?.smallImg,
                        error = painterResource(id = R.drawable.ic_rpc_placeholder),
                        placeholder = painterResource(R.drawable.ic_rpc_placeholder),
                        contentDescription = null,
                        contentScale = ContentScale.FillHeight,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .align(Alignment.BottomEnd)
                    )
                }
            }
            Column {
                ProfileText(
                    text = if (isPlaying) {
                        rpcConfig?.name.takeIf { !it.isNullOrEmpty() }
                            ?: stringResource(id = R.string.user_profile)
                    } else {
                        rpcConfig?.details
                    },
                    style = MaterialTheme.typography.titleSmall
                        .copy(
                            fontSize = 16.sp,
                        ),
                    modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 2.dp)
                )
                ProfileText(
                    text = if (isPlaying) {
                        rpcConfig?.details
                    } else {
                        rpcConfig?.state
                    },
                    style = MaterialTheme.typography.titleSmall
                        .copy(
                            fontSize = 12.sp
                        ),
                    modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 2.dp),
                    bold = false
                )
                ProfileText(
                    text = (if (isPlaying) {
                        rpcConfig?.state
                    } else {
                        rpcConfig?.largeText
                    } +
                            if (
                                rpcConfig?.partyCurrentSize?.toIntOrNull() != null &&
                                rpcConfig.partyMaxSize.toIntOrNull() != null &&
                                rpcConfig.partyCurrentSize.toIntOrNull()!! > 0 &&
                                rpcConfig.partyMaxSize.toIntOrNull()!! > 0 &&
                                rpcConfig.partyCurrentSize.toIntOrNull()!! <= rpcConfig.partyMaxSize.toIntOrNull()!!
                            ) {
                                " " + stringResource(
                                    R.string.user_profile_party,
                                    rpcConfig.partyCurrentSize.toInt(),
                                    rpcConfig.partyMaxSize.toInt()
                                )
                            } else {
                                ""
                            }).takeIf { rpcConfig?.state?.isNotEmpty() == true },
                    style = MaterialTheme.typography.titleSmall
                        .copy(
                            fontSize = 12.sp
                        ),
                    modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 2.dp),
                    bold = false
                )
                if (
                    showTs &&
                    (isPlaying ||
                            (isPlayingMedia &&
                                    (rpcConfig?.timestampsStart?.toLongOrNull() == null || rpcConfig?.timestampsStop?.toLongOrNull() == null)))
                ) {
                    ProfileText(
                        text = stringResource(
                            id = rpcConfig?.timestampsStop?.toLongOrNull()?.let {
                                if (it > 0) R.string.time_left else R.string.time_elapsed
                            } ?: R.string.time_elapsed,
                            formatTime(
                                rpcConfig?.timestampsStart?.toLongOrNull(),
                                rpcConfig?.timestampsStop?.toLongOrNull()
                            )
                                .takeIf { it.isNotEmpty() } ?: elapsedState
                        ),
                        style = MaterialTheme.typography.labelSmall
                            .copy(
                                fontSize = 12.sp
                            ),
                        bold = false,
                        modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)
                    )
                }
            }
        }
        if (showTs && isPlayingMedia && rpcConfig?.timestampsStart?.toLongOrNull() != null && rpcConfig?.timestampsStop?.toLongOrNull() != null) {
            // Seekbar for media
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 6.dp, 20.dp, 0.dp)
                    .height(4.dp)
                    .background(Color.LightGray.copy(alpha = 0.5f))
                    .clip(RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .fillMaxWidth(sliderPosition)
                        .height(4.dp)
                        .background(DISCORD_BLURPLE)
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 4.dp, 20.dp, 6.dp)
            ) {
                ProfileText(
                    // Time elapsed must be less than the total time
                    text = elapsedText,
                    style = MaterialTheme.typography.labelSmall
                        .copy(
                            fontSize = 12.sp
                        ),
                    bold = false,
                    modifier = Modifier
                )
                ProfileText(
                    text = getFormatFromMs(rpcConfig.timestampsStop.toLong() - rpcConfig.timestampsStart.toLong()),
                    style = MaterialTheme.typography.labelSmall
                        .copy(
                            fontSize = 12.sp
                        ),
                    bold = false,
                    modifier = Modifier
                )
            }
        }
        if (showTs && rpcConfig == null) {
            ProfileButton(
                label = stringResource(id = R.string.user_profile_special_button),
                link = special
            )
        }
        if (rpcConfig != null) {
            ProfileButton(label = rpcConfig.button1, link = rpcConfig.button1link)
            ProfileButton(label = rpcConfig.button2, link = rpcConfig.button2link)
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(19.dp, 0.dp, 19.dp, 5.dp)
                .height(1.5.dp)
        )
    }
}

private fun isAsset(url: String?): Boolean {
    return url?.startsWith("attachments") == true || url?.startsWith("external") == true
}

private fun getFormatFromMs(ms: Long): String {
    var remainingMs = ms

    val daysDifference = TimeUnit.MILLISECONDS.toDays(remainingMs)
    remainingMs -= TimeUnit.DAYS.toMillis(daysDifference)
    val hoursDifference = TimeUnit.MILLISECONDS.toHours(remainingMs)
    remainingMs -= TimeUnit.HOURS.toMillis(hoursDifference)
    val minutesDifference = TimeUnit.MILLISECONDS.toMinutes(remainingMs)
    remainingMs -= TimeUnit.MINUTES.toMillis(minutesDifference)
    val secondsDifference = TimeUnit.MILLISECONDS.toSeconds(remainingMs)

    return (if (hoursDifference >= 1) String.format(Locale.US, "%02d:", hoursDifference) else "") +
            String.format(Locale.US, "%02d:%02d", minutesDifference, secondsDifference)
}

private fun formatTime(start: Long?, end: Long? = null): String {
    val startTime = end ?: start ?: return ""
    val endTime = System.currentTimeMillis()

    val difference = if (end != null) (startTime - endTime) else (endTime - startTime)
    if (difference < 0) return "00:00"

    return getFormatFromMs(difference)
}