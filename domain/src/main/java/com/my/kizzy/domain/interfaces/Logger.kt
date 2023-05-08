/*
 *
 *  ******************************************************************
 *  *  * Copyright (C) 2022
 *  *  * Logger.kt is part of Kizzy
 *  *  *  and can not be copied and/or distributed without the express
 *  *  * permission of yzziK(Vaibhav)
 *  *  *****************************************************************
 *
 *
 */

package com.my.kizzy.domain.interfaces

interface Logger {
    fun clear()
    fun i(tag: String, event: String)
    fun e(tag: String, event: String)
    fun d(tag: String, event: String)
    fun w(tag: String, event: String)
}

object NoOpLogger: Logger {
    override fun clear() {}
    override fun i(tag: String, event: String) {}
    override fun e(tag: String, event: String) {}
    override fun d(tag: String, event: String) {}
    override fun w(tag: String, event: String) {}
}