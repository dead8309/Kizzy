/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * AppsItem.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_apps_rpc

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.AppUtils
import com.my.kizzy.ui.components.KSwitch
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun AppsItem(
    name: String,
    pkg: String,
    isChecked: Boolean,
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = Modifier.clickable {
            onClick()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlideImage(
                    imageModel = AppUtils.getAppIcon(pkg),
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .padding(10.dp),
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    overflow = TextOverflow.Ellipsis
                )
                if (pkg.isNotEmpty())
                    Text(
                        text = pkg,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        style = MaterialTheme.typography.bodyMedium,
                    )
            }
            KSwitch(
                checked = isChecked,
                modifier = Modifier.padding(start = 20.dp, end = 6.dp),
            )
        }
    }
}