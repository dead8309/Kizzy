/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * UiEvents.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.feature_custom_rpc
import android.net.Uri
import com.my.kizzy.domain.model.RpcConfig

sealed interface UiEvent {
    object TriggerBottomSheet: UiEvent
    object TriggerActivityTypeDropDownMenu: UiEvent
    class SetFieldsFromConfig(val config: RpcConfig): UiEvent
    object TriggerStartTimeStampsDialog: UiEvent
    object TriggerStopTimeStampsDialog: UiEvent
    class UploadImage(val uri: Uri,val callback: (result: String) -> Unit): UiEvent

    sealed interface SheetEvent: UiEvent {
        object TriggerLoadDialog: SheetEvent
        object TriggerSaveDialog: SheetEvent
        object TriggerDeleteDialog: SheetEvent
        object TriggerPreviewDialog: SheetEvent
        object TriggerStoragePermissionRequest: SheetEvent
    }
}

