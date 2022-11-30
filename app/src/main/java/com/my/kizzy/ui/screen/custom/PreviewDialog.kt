package com.my.kizzy.ui.screen.custom

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.my.kizzy.data.remote.User
import com.my.kizzy.ui.screen.profile.user.ProfileCard

@Composable
fun PreviewDialog(
    user: User,
    intentRpcData: IntentRpcData? = null,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = {
        onDismiss()
    }) {
        ProfileCard(
            user = user, padding = 0.dp, rpcData = intentRpcData,
            type = intentRpcData?.type.getType(intentRpcData?.name),
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