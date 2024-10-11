/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * PreviewDialog.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_custom_rpc.components.sheet

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.my.kizzy.domain.model.rpc.RpcConfig
import com.my.kizzy.domain.model.user.User
import com.my.kizzy.feature_profile.ui.component.ProfileCard
import com.my.kizzy.resources.R

@Composable
fun PreviewDialog(
    user: User,
    rpc: RpcConfig? = null,
    onDismiss: () -> Unit
) {
    val ctx = LocalContext.current
    Dialog(onDismissRequest = {
        onDismiss()
    }) {
        ProfileCard(
            user = user, padding = 0.dp,
            rpcConfig = rpc,
            type = rpc?.type.getType(ctx, rpc?.name),
            showTs = false
        )
    }
}

private fun String?.getType(ctx: Context, name: String?): String {
    val type: Int = try {
        if (!this.isNullOrEmpty()) this.toDouble().toInt()
        else 0
    } catch (ex: NumberFormatException) {
        0
    }
    return when (type) {
        1 -> ctx.getString(R.string.activity_streaming_title, name)
        2 -> ctx.getString(R.string.activity_listening_title, name)
        3 -> ctx.getString(R.string.activity_watching_title, name)
        4 -> ""
        5 -> ctx.getString(R.string.activity_competiting_title, name)
        else -> ctx.getString(R.string.activity_playing_title)
    }
}