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

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.my.kizzy.domain.model.rpc.RpcConfig
import com.my.kizzy.domain.model.user.User
import com.my.kizzy.feature_profile.ui.component.ProfileCard

@Composable
fun PreviewDialog(
    user: User,
    rpc: RpcConfig? = null,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = {
        onDismiss()
    }) {
        ProfileCard(
            user = user, padding = 0.dp,
            rpcConfig = rpc,
            type = rpc?.type.getType(rpc?.name),
            showTs = false
        )
    }
}

private fun String?.getType(name: String?): String {
    val type: Int = try {
        if (!this.isNullOrEmpty()) this.toDouble().toInt()
        else 0
    } catch (ex: NumberFormatException) {
        0
    }
    return when (type) {
        1 -> "Streaming on $name"
        2 -> "Listening $name"
        3 -> "Watching $name"
        4 -> ""
        5 -> "Competing in $name"
        else -> "Playing a game"
    }
}