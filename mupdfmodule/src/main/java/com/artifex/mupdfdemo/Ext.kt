package com.artifex.mupdfdemo

import android.graphics.RectF

class Ext {
    companion object {
       // var onClick: ((isClicked: Boolean) -> Unit)? = null
        var onClick: ((x: Float, y: Float, rectF: RectF) -> Unit)? = null

    }
}