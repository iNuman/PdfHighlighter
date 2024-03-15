package com.artifex.mupdfdemo;

import android.graphics.PointF;
import android.graphics.RectF;


public interface MuPDFView{
    void setPage(int page, PointF size);
    void setScale(float scale);
    int getPage();
    void blank(int page);
    Hit passClickEvent(float x, float y);
    LinkInfo hitLink(float x, float y);
    void selectText(float x0, float y0, float x1, float y1);
    void deselectText();
    boolean copySelection();
    boolean isTextSelected();
    /**
     * After the text is selected, the text will be processed and annotated according to the type of highlighting, underlining, and strikethrough.
     * @param type
     * @return
     */
    boolean markupSelection(Annotation.Type type);
    void deleteSelectedAnnotation();
    void setSearchBoxes(RectF searchBoxes[]);
    void setLinkHighlighting(boolean f);
    void deselectAnnotation();
    void startDraw(float x, float y);

    void continueDraw(float x, float y);
    void undoDraw();
    void redoDraw();
    void cancelDraw();
    boolean saveDraw();
    void setChangeReporter(Runnable reporter);
    void update();
    void updateHq(boolean update);
    void removeHq();
    void releaseResources();
    void releaseBitmaps();
    /**
     * Set hyperlink color
     * @param color color value
     */
    void setLinkHighlightColor(int color);
    /**
     * Set brush color
     * @param color color value
     */
    void setInkColor(int color);
    /**
     * Set brush thickness
     * @param inkThickness thickness value
     */
    void setPaintStrockWidth(float inkThickness);
    void setPageMode(String nightDayTheme);
    void setContinuousPageScroll(Boolean continueScroll);

    float getCurrentScale();

    void showCopyRect(float x, float y, Float direction,Boolean isTextSelected,Boolean actionDetection);

}
