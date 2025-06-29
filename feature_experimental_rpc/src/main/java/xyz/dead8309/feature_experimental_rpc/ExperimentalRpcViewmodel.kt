/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * ExperimentalRpcViewmodel.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package xyz.dead8309.feature_experimental_rpc

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.my.kizzy.data.utils.getInstalledApps
import com.my.kizzy.preference.Prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExperimentalRpcViewmodel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        UiState(
            isAppsLoading = true,
            isAppsRpcPartEnabled = Prefs[Prefs.EXPERIMENTAL_RPC_USE_APPS_RPC, true],
            isMediaRpcPartEnabled = Prefs[Prefs.EXPERIMENTAL_RPC_USE_MEDIA_RPC, true],
            templateName = Prefs[Prefs.EXPERIMENTAL_RPC_TEMPLATE_NAME, ""],
            templateDetails = Prefs[Prefs.EXPERIMENTAL_RPC_TEMPLATE_DETAILS, ""],
            templateState = Prefs[Prefs.EXPERIMENTAL_RPC_TEMPLATE_STATE, ""],
            showCoverArt = Prefs[Prefs.EXPERIMENTAL_RPC_SHOW_COVER_ART, false],
            showAppIcon = Prefs[Prefs.EXPERIMENTAL_RPC_SHOW_APP_ICON, false],
            showPlaybackState = Prefs[Prefs.EXPERIMENTAL_RPC_SHOW_PLAYBACK_STATE, false],
            enableTimestamps = Prefs[Prefs.EXPERIMENTAL_RPC_ENABLE_TIMESTAMPS, false],
            hideOnPause = Prefs[Prefs.EXPERIMENTAL_RPC_HIDE_ON_PAUSE, false],
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.Default) {
            val appList = getInstalledApps(
                context = context,
                isEnabled = Prefs::isExperimentalAppEnabled
            )
            val enabledApps = appList.associate { it.pkg to it.isChecked }
            val savedActivityTypes = Prefs.getAppActivityTypes()
            val appActivityTypes = appList.associate { it.pkg to (savedActivityTypes[it.pkg] ?: 0) }
            _uiState.update {
                it.copy(
                    installedApps = appList,
                    enabledApps = enabledApps,
                    appActivityTypes = appActivityTypes,
                    isAppsLoading = false
                )
            }
        }
    }

    fun onEvent(event: UiEvent) {
        viewModelScope.launch {
            when (event) {
                is UiEvent.ToggleAppsRpcPart -> {
                    Prefs[Prefs.EXPERIMENTAL_RPC_USE_APPS_RPC] = event.enabled
                    _uiState.value = _uiState.value.copy(isAppsRpcPartEnabled = event.enabled)
                }

                is UiEvent.ToggleMediaRpcPart -> {
                    Prefs[Prefs.EXPERIMENTAL_RPC_USE_MEDIA_RPC] = event.enabled
                    _uiState.value = _uiState.value.copy(isMediaRpcPartEnabled = event.enabled)
                }

                is UiEvent.SetTemplateName -> {
                    Prefs[Prefs.EXPERIMENTAL_RPC_TEMPLATE_NAME] = event.value
                    _uiState.update { it.copy(templateName = event.value) }
                }

                is UiEvent.SetTemplateDetails -> {
                    Prefs[Prefs.EXPERIMENTAL_RPC_TEMPLATE_DETAILS] = event.value
                    _uiState.update { it.copy(templateDetails = event.value) }
                }

                is UiEvent.SetTemplateState -> {
                    Prefs[Prefs.EXPERIMENTAL_RPC_TEMPLATE_STATE] = event.value
                    _uiState.update { it.copy(templateState = event.value) }
                }

                is UiEvent.ToggleAppEnabled -> {
                    val current = _uiState.value.enabledApps[event.packageName] ?: false
                    Prefs.saveExperimentalAppToPrefs(event.packageName)
                    _uiState.update { state ->
                        state.copy(
                            enabledApps = state.enabledApps.toMutableMap().apply {
                                this[event.packageName] = !current
                            }
                        )
                    }
                }

                is UiEvent.SetAppActivityType -> {
                    val typeInt = event.activityType
                    Prefs.saveAppActivityType(event.packageName, typeInt)
                    _uiState.update { state ->
                        state.copy(
                            appActivityTypes = state.appActivityTypes.toMutableMap().apply {
                                this[event.packageName] = typeInt
                            }
                        )
                    }
                }
                is UiEvent.ToggleShowCoverArt -> {
                    Prefs[Prefs.EXPERIMENTAL_RPC_SHOW_COVER_ART] = event.enabled
                    _uiState.update { it.copy(showCoverArt = event.enabled) }
                }

                is UiEvent.ToggleShowAppIcon -> {
                    Prefs[Prefs.EXPERIMENTAL_RPC_SHOW_APP_ICON] = event.enabled
                    _uiState.update {
                        it.copy(showAppIcon = event.enabled)
                    }
                    if (event.enabled) {
                        Prefs[Prefs.EXPERIMENTAL_RPC_SHOW_PLAYBACK_STATE] = false
                        _uiState.update { it.copy(showPlaybackState = false) }
                    }
                }

                is UiEvent.ToggleShowPlaybackState -> {
                    Prefs[Prefs.EXPERIMENTAL_RPC_SHOW_PLAYBACK_STATE] = event.enabled
                    _uiState.update {
                        it.copy(showPlaybackState = event.enabled)
                    }
                    if (event.enabled) {
                        Prefs[Prefs.EXPERIMENTAL_RPC_SHOW_APP_ICON] = false
                        _uiState.update { it.copy(showAppIcon = false) }
                    }
                }

                is UiEvent.ToggleEnableTimestamps -> {
                    Prefs[Prefs.EXPERIMENTAL_RPC_ENABLE_TIMESTAMPS] = event.enabled
                    _uiState.update { it.copy(enableTimestamps = event.enabled) }
                }

                is UiEvent.ToggleHideOnPause -> {
                    Prefs[Prefs.EXPERIMENTAL_RPC_HIDE_ON_PAUSE] = event.enabled
                    _uiState.update { it.copy(hideOnPause = event.enabled) }
                }
            }
        }
    }
}
