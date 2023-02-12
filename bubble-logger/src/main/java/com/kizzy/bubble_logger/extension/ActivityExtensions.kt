package com.kizzy.bubble_logger.extension

import android.app.Activity
import android.os.Build
import android.view.View

/**
 * Set system decor for edge-to-edge UI.
 */
fun Activity.applyEdgeToEdge() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.setDecorFitsSystemWindows(true)
    } else {
        window.decorView.apply {
            @Suppress("DEPRECATION")
            systemUiVisibility = systemUiVisibility or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
    }
}