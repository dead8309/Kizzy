/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * UpdateEvent.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *  
 *
 */

package com.my.kizzy.domain.model.samsung_rpc

import kotlinx.serialization.Serializable

@Serializable(UpdateEventSerializer::class)
enum class UpdateEvent(val value: String) {
    /** Starts the Samsung galaxy rpc */
    START("START"),

    /** Stops the Samsung galaxy rpc */
    STOP("STOP"),

    /** Updates an already running Samsung galaxy rpc */
    UPDATE("UPDATE")
}