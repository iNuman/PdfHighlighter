package com.example.myapplication

import android.view.View
import androidx.core.view.isVisible

fun View.showIfNotVisible() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

fun View.hideIfVisible() {
    if (isVisible) {
        visibility = View.GONE
    }
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}
