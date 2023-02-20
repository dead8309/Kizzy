/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * GetAppsUseCase.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.domain.use_case.get_current_data.get_apps

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import com.blankj.utilcode.util.AppUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.my.kizzy.domain.use_case.get_current_data.SharedRpc
import com.my.kizzy.rpc.RpcImage
import com.my.kizzy.preference.Prefs
import java.util.*

fun getCurrentRunningApp(context: Context): SharedRpc {
    val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val currentTimeMillis = System.currentTimeMillis()
    val queryUsageStats = usageStatsManager.queryUsageStats(
        UsageStatsManager.INTERVAL_DAILY, currentTimeMillis - 10000, currentTimeMillis
    )
    val apps = Prefs[Prefs.ENABLED_APPS, "[]"]
    val enabledPackages: ArrayList<String> = Gson().fromJson(
        apps, object : TypeToken<ArrayList<String>?>() {}.type
    )
    if (queryUsageStats != null && queryUsageStats.size > 1) {
        val treeMap: SortedMap<Long, UsageStats> = TreeMap()
        for (usageStats in queryUsageStats) {
            treeMap[usageStats.lastTimeUsed] = usageStats
        }
        if (!(treeMap.isEmpty() || treeMap[treeMap.lastKey()]?.packageName == "com.my.kizzy" || treeMap[treeMap.lastKey()]?.packageName == "com.discord")) {
            val packageName = treeMap[treeMap.lastKey()]!!.packageName
            Objects.requireNonNull(packageName)
            if (enabledPackages.contains(packageName)) {
                return SharedRpc(
                    name = AppUtils.getAppName(packageName),
                    details = "",
                    state = "",
                    large_image = RpcImage.ApplicationIcon(packageName, context),
                    package_name = packageName
                )
            }
        }
    }
    return SharedRpc()
}