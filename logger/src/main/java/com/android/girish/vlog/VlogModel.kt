package com.android.girish.vlog

import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

internal class VlogModel(@param:LogPriority val logPriority: Int, val tag: String, val logMessage: String) {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(VERBOSE, DEBUG, INFO, WARN, ERROR)
    annotation class LogPriority

    companion object {
        /**
         * Priority constants
         */
        const val VERBOSE = 1
        const val DEBUG = 2
        const val INFO = 3
        const val WARN = 4
        const val ERROR = 5
    }
}
