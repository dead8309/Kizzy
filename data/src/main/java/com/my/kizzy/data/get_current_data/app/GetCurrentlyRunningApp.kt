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

package com.my.kizzy.data.get_current_data.app

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import com.blankj.utilcode.util.AppUtils
import com.my.kizzy.data.rpc.CommonRpc
import com.my.kizzy.data.rpc.RpcImage
import com.my.kizzy.preference.Prefs
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.Objects
import java.util.SortedMap
import java.util.TreeMap
import javax.inject.Inject

class GetCurrentlyRunningApp @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(beginTime: Long = System.currentTimeMillis() - 10000): CommonRpc {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTimeMillis = System.currentTimeMillis()
        val queryUsageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, beginTime, currentTimeMillis
        )
        val apps = Prefs[Prefs.ENABLED_APPS, "[]"]
        val enabledPackages: ArrayList<String> = Json.decodeFromString(apps)
        if (queryUsageStats != null && queryUsageStats.size > 1) {
            val treeMap: SortedMap<Long, UsageStats> = TreeMap()
            for (usageStats in queryUsageStats) {
                treeMap[usageStats.lastTimeUsed] = usageStats
            }
            if (!(treeMap.isEmpty() || treeMap[treeMap.lastKey()]?.packageName == "com.my.kizzy" || treeMap[treeMap.lastKey()]?.packageName == "com.discord")) {
                val packageName = treeMap[treeMap.lastKey()]!!.packageName
                Objects.requireNonNull(packageName)
                if (enabledPackages.contains(packageName)) {
                    return CommonRpc(
                        name = AppUtils.getAppName(packageName),
                        largeImage = RpcImage.ApplicationIcon(packageName, context),
                        packageName = packageName
                    )
                }
            }
        }
        return CommonRpc()
    }
}