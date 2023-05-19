/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ImagePicker.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *  
 *
 */

package com.my.kizzy.feature_custom_rpc.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.my.kizzy.ui.components.BrowseFilesButton
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ImagePicker(
    visible: Boolean,
    onDismiss: () -> Unit,
    showProgress: Boolean,
    onImageSelected: (uri: Uri) -> Unit,
) {
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) {
        imageUri.value = it
    }
    if (visible){
        UploadDialog(
            image = imageUri,
            onBrowse = {
                launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType("image/*")))
            },
            onPicked = {
                onImageSelected(it)
            },
            onDismiss = {
                onDismiss()
            },
            showProgress = showProgress
        )
    }
}

@Composable
fun UploadDialog(
    image: MutableState<Uri?>,
    onBrowse: () -> Unit,
    onDismiss: () -> Unit,
    showProgress: Boolean,
    onPicked: (uri: Uri) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.padding(20.dp),
        confirmButton = {
            if (image.value != null) {
                TextButton(
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        onPicked(image.value!!)
                    },
                ) {
                    Text(text = "Confirm")
                    if (showProgress) {
                        Spacer(modifier = Modifier.width(2.dp))
                        CircularProgressIndicator(Modifier.size(20.dp))
                    }
                }
            }
        },
        title = { Text(text = "Upload Image") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BrowseFilesButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                ) {
                    onBrowse()
                }
                if (image.value != null)
                    runCatching{
                        GlideImage(
                            imageModel = image.value,
                            contentDescription = "",
                            modifier = Modifier.size(200.dp),
                            error = Icons.Default.BrokenImage
                        )
                    }
            }
        }
    )
}