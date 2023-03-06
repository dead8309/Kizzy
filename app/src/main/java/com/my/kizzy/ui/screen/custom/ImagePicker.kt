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

package com.my.kizzy.ui.screen.custom

//
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.my.kizzy.R
import com.my.kizzy.ui.components.BrowseFilesButton
import com.my.kizzy.utils.Log
import com.my.kizzy.utils.getFileName
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ImagePicker(
    visible: Boolean,
    onDismiss: () -> Unit,
    showProgress: Boolean,
    onImageSelected: (uri: Uri) -> Unit,
) {
    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) {
        Log.logger.d("file:"+it?.let { it1 -> context.getFileName(it1).toString() },it.toString())
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

@OptIn(ExperimentalComposeUiApi::class)
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
                            previewPlaceholder = R.drawable.editing_rpc_pencil,
                            error = Icons.Default.BrokenImage
                        )
                    }
            }
        }
    )
}