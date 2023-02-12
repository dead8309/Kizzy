package com.kizzy.bubble_logger.extension

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * Clear focus of the [EditText] and hide IME.
 */
fun EditText.clearFocusAndHideIme() {
    clearFocus()
    (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(windowToken, 0)
}