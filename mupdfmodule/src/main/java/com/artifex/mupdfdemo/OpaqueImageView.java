package com.artifex.mupdfdemo;

import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;

class OpaqueImageView extends AppCompatImageView {

    public OpaqueImageView(Context context) {
        super(context);
    }

    @Override
    public boolean isOpaque() {
        return true;
    }
}
