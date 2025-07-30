/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * GetInstalledApps.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.data.utils

import android.content.Context
import android.content.pm.PackageManager

data class AppsInfo(
    val name: String,
    val pkg: String,
    val isChecked: Boolean = false,
)

fun getInstalledApps(
    context: Context,
    isEnabled: (String) -> Boolean,
): List<AppsInfo> {
    val pm = context.packageManager
    val installedApps = pm.getInstalledApplications(PackageManager.GET_GIDS)
    val appDetailsList = installedApps
        .asSequence()
        .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
        .map { app ->
            AppsInfo(
                name = app.loadLabel(pm).toString(),
                pkg = app.packageName,
                isChecked = isEnabled(app.packageName)
            )
        }
        .sortedBy { it.name }
        .toList()
    return appDetailsList
}