/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * CustomScreenViewModel.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_custom_rpc

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.UriUtils
import com.my.kizzy.domain.use_case.upload_galleryImage.UploadGalleryImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class CustomScreenViewModel @Inject constructor(
    private val uploadGalleryImageUseCase: UploadGalleryImageUseCase
) : ViewModel() {
    var name by mutableStateOf("")
    var details by mutableStateOf("")
    var state by mutableStateOf("")
    var startTimestamps by mutableStateOf("")
    var stopTimestamps by mutableStateOf("")
    var status by mutableStateOf("")
    var button1 by mutableStateOf("")
    var button2 by mutableStateOf("")
    var button1Url by mutableStateOf("")
    var button2Url by mutableStateOf("")
    var largeImg by mutableStateOf("")
    var smallImg by mutableStateOf("")
    var largeImgText by mutableStateOf("")
    var smallImgText by mutableStateOf("")
    var type by mutableStateOf("")
    var url by mutableStateOf("")
    var activityTypeisExpanded by mutableStateOf(false)
    var menuClicked by mutableStateOf(false)
    var showLoadDialog by mutableStateOf(false)
    var showSaveDialog by mutableStateOf(false)
    var showDeleteDialog by mutableStateOf(false)
    var showPreviewDialog by mutableStateOf(false)
    var showStartTimeStampsPickerDialog by mutableStateOf(false)
    var showStopTimeStampsPickerDialog by mutableStateOf(false)

    fun uploadImage(uri: Uri, result: (String) -> Unit) {
        viewModelScope.launch {
            UriUtils.uri2File(uri)?.let { file ->
                uploadGalleryImageUseCase(file)?.let {
                    result(it.drop(3))
                }
            }
        }
    }
}
