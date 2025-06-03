/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * UiEvent.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package xyz.dead8309.feature_experimental_rpc

sealed interface UiEvent {
    data class ToggleAppsRpcPart(val enabled: Boolean) : UiEvent
    data class ToggleMediaRpcPart(val enabled: Boolean) : UiEvent
    data class SetTemplateName(val value: String) : UiEvent
    data class SetTemplateDetails(val value: String) : UiEvent
    data class SetTemplateState(val value: String) : UiEvent
    data class ToggleAppEnabled(val packageName: String) : UiEvent
    data class SetAppActivityType(val packageName: String, val activityType: String) : UiEvent
    data class ToggleShowCoverArt(val enabled: Boolean) : UiEvent
    data class ToggleShowAppIcon(val enabled: Boolean) : UiEvent
    data class ToggleShowPlaybackState(val enabled: Boolean) : UiEvent
    data class ToggleEnableTimestamps(val enabled: Boolean) : UiEvent
    data class ToggleHideOnPause(val enabled: Boolean) : UiEvent
}
