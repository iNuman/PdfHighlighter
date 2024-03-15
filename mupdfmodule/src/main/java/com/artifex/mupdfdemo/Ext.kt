package com.artifex.mupdfdemo

class Ext {
    companion object {
       // var onClick: ((isClicked: Boolean) -> Unit)? = null
        var onClick: ((x: Float, y: Float, direction:Float,action:Boolean,isClicked: Boolean) -> Unit)? = null

    }
}