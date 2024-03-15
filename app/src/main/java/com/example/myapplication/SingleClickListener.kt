package com.example.myapplication

import android.os.SystemClock
import android.view.View

class SingleClickListener(private val block: () -> Unit, private val yesCount: Boolean) :
    View.OnClickListener {

    private var lastClickTime = 0L

    override fun onClick(view: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 600) {
            return
        }
        if (yesCount)
//            checkAndUpdateClickCount()
            lastClickTime = SystemClock.elapsedRealtime()
        block()
    }
}

fun View.setOnSingleClickListener(block: () -> Unit, yesCount: Boolean = true) {
    setOnClickListener(SingleClickListener(block, yesCount))
}