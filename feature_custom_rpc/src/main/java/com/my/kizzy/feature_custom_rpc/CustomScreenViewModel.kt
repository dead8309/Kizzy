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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.kizzy.domain.model.rpc.RpcConfig
import com.my.kizzy.domain.use_case.upload_galleryImage.UploadGalleryImageUseCase
import com.my.kizzy.feature_custom_rpc.components.sheet.stringToData
import com.my.kizzy.preference.Prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class CustomScreenViewModel @Inject constructor(
    private val uploadGalleryImageUseCase: UploadGalleryImageUseCase
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState
    private val uiEventChannel = Channel<UiEvent>(capacity = Channel.UNLIMITED)

    init {
        if (Prefs[Prefs.APPLY_FIELDS_FROM_LAST_RUN_RPC, false]) {
            _uiState.value = _uiState.value.copy(
                rpcConfig = Prefs[Prefs.LAST_RUN_CUSTOM_RPC, ""].stringToData()
            )
        }
        viewModelScope.launch {
            uiEventChannel.consumeAsFlow()
                .collect { event ->
                    processEvent(event)
                }
        }
    }

    private suspend fun uploadImage(file: File, result: (String) -> Unit) {
        uploadGalleryImageUseCase(file)?.let {
            withContext(Dispatchers.Main) {
                result(it.drop(3))
            }
        }
    }

    fun onEvent(event: UiEvent) {
        viewModelScope.launch {
            uiEventChannel.send(event)
        }
    }

    private suspend fun processEvent(event: UiEvent) {
        when (event) {
            is UiEvent.SetFieldsFromConfig -> {
                _uiState.value = _uiState.value.copy(rpcConfig = event.config)
            }

            UiEvent.TriggerBottomSheet -> {
                _uiState.value =
                    _uiState.value.copy(showBottomSheet = !_uiState.value.showBottomSheet)
            }

            UiEvent.TriggerActivityTypeDropDownMenu -> {
                _uiState.value =
                    _uiState.value.copy(activityTypeIsExpanded = !_uiState.value.activityTypeIsExpanded)
            }

            UiEvent.TriggerStartTimeStampsDialog -> {
                _uiState.value =
                    _uiState.value.copy(showStartTimeStampsPickerDialog = !_uiState.value.showStartTimeStampsPickerDialog)
            }

            UiEvent.TriggerStopTimeStampsDialog -> {
                _uiState.value =
                    _uiState.value.copy(showStopTimeStampsPickerDialog = !_uiState.value.showStopTimeStampsPickerDialog)
            }

            is UiEvent.UploadImage -> {
                uploadImage(event.file) {
                    event.callback(it)
                }
            }

            // Sheet Events
            UiEvent.SheetEvent.TriggerStoragePermissionRequest -> {
                _uiState.value = _uiState.value.copy(
                    showStoragePermissionRequestDialog = !_uiState.value.showStoragePermissionRequestDialog,
                    showBottomSheet = false
                )
            }

            UiEvent.SheetEvent.TriggerDeleteDialog -> {
                _uiState.value = _uiState.value.copy(
                    showDeleteDialog = !_uiState.value.showDeleteDialog,
                    showBottomSheet = false
                )
            }

            UiEvent.SheetEvent.TriggerLoadDialog -> {
                _uiState.value = _uiState.value.copy(
                    showLoadDialog = !_uiState.value.showLoadDialog,
                    showBottomSheet = false
                )
            }

            UiEvent.SheetEvent.TriggerPreviewDialog -> {
                _uiState.value = _uiState.value.copy(
                    showPreviewDialog = !_uiState.value.showPreviewDialog,
                    showBottomSheet = false
                )
            }

            UiEvent.SheetEvent.TriggerSaveDialog -> {
                _uiState.value = _uiState.value.copy(
                    showSaveDialog = !_uiState.value.showSaveDialog,
                    showBottomSheet = false
                )
            }

            UiEvent.SheetEvent.ClearAllFields -> {
                _uiState.value = _uiState.value.copy(
                    rpcConfig = RpcConfig(),
                    showBottomSheet = false
                )
            }
        }
    }
}
