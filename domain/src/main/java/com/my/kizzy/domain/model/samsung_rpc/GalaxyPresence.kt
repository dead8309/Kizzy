/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * GalaxyPresence.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.domain.model.samsung_rpc

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GalaxyPresence(
    @SerialName("package_name")
    val packageName: String,
    @SerialName("update")
    val update: UpdateEvent
)