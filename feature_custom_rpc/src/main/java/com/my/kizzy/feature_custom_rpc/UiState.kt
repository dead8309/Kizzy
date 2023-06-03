/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * UiState.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_custom_rpc

import com.my.kizzy.domain.model.rpc.RpcConfig

data class UiState(
    val activityTypeIsExpanded: Boolean = false,
    val showBottomSheet: Boolean = false,
    val showLoadDialog: Boolean = false,
    val showSaveDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showPreviewDialog: Boolean = false,
    val showStoragePermissionRequestDialog: Boolean = false,
    val showStartTimeStampsPickerDialog: Boolean = false,
    val showStopTimeStampsPickerDialog: Boolean = false,
    val rpcConfig: RpcConfig = RpcConfig()
)