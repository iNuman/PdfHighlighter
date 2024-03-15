package com.artifex.mupdfdemo;


public interface MuPDFReaderViewListener {
    void onMoveToChild(int i);
    void onTapMainDocArea();
    void onDocMotion();
    void onHit(Hit item);
    void onLongPress();
}
