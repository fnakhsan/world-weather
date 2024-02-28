package com.fnakhsan.worldweather.ui.utils

import android.content.Context
import android.view.View
import com.fnakhsan.worldweather.R
import com.google.android.material.snackbar.Snackbar


object BaseSnackBar {
    fun errorSnackBar(view: View, context: Context, message: String, code: String) {
        Snackbar.make(
            view,
            context.resources.getString(R.string.error, message, code),
            Snackbar.LENGTH_SHORT
        ).setTextMaxLines(2).show()
    }
}