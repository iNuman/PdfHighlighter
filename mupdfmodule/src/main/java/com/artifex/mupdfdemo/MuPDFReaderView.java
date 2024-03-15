package com.artifex.mupdfdemo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;


public class MuPDFReaderView extends ReaderView {

    private MuPDFReaderViewListener listener;

    public enum Mode {Viewing, Selecting, Drawing, Undo, Redo}

    private final Context mContext;
    private boolean mLinksEnabled = false;
    private boolean isLinkHighlightColor = false;
    private MuPDFReaderView.Mode mMode = MuPDFReaderView.Mode.Viewing;
    private boolean tapDisabled = false;
    private int tapPageMargin;

    private int mLinkHighlightColor;

    protected void onTapMainDocArea() {
        checkMuPDFReaderViewListener();
        listener.onTapMainDocArea();
    }

    protected void onDocMotion() {
        checkMuPDFReaderViewListener();
        listener.onDocMotion();
    }

    protected void onHit(Hit item) {
        checkMuPDFReaderViewListener();
        listener.onHit(item);
    }
    protected void onLongPress(){
        checkMuPDFReaderViewListener();
        listener.onLongPress();
    }

    /**
     * Set whether hyperlinks are highlighted
     *
     * @param b
     */
    public void setLinksEnabled(boolean b) {
        mLinksEnabled = b;
        resetupChildren();
    }

    /**
     * Set hyperlink color
     *
     * @param color color value
     */
    public void setLinkHighlightColor(int color) {
        isLinkHighlightColor = true;
        mLinkHighlightColor = color;
        resetupChildren();
    }

    /**
     * Set search text color
     *
     * @param color color value
     */
    public void setSearchTextColor(int color) {
        SharedPreferencesUtil.put(SPConsts.SP_COLOR_SEARCH_TEXT, color);
        resetupChildren();
    }

    /**
     * Set brush color
     *
     * @param color color value
     */
    public void setInkColor(int color) {
//		SharedPreferencesUtil.put(SPConsts.SP_COLOR_SEARCH_TEXT, color);
        ((MuPDFView) getCurrentView()).setInkColor(color);
    }

    /**
     * Set brush thickness
     *
     * @param inkThickness thickness value
     */
    public void setPaintStrockWidth(float inkThickness) {
//		SharedPreferencesUtil.put(SPConsts.SP_COLOR_SEARCH_TEXT, color);
        ((MuPDFView) getCurrentView()).setPaintStrockWidth(inkThickness);
    }

    public float getCurrentScale() {
        return ((MuPDFView) getCurrentView()).getCurrentScale();
    }

    public void setMode(MuPDFReaderView.Mode m) {
        mMode = m;
    }

    private void setup() {
        // Get the screen size etc to customise tap margins.
        // We calculate the size of 1 inch of the screen for tapping.
        // On some devices the dpi values returned are wrong, so we
        // sanity check it: we first restrict it so that we are never
        // less than 100 pixels (the smallest Android device screen
        // dimension I've seen is 480 pixels or so). Then we check
        // to ensure we are never more than 1/5 of the screen width.
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        tapPageMargin = (int) dm.xdpi;
        if (tapPageMargin < 100) {
            tapPageMargin = 100;
        }
        if (tapPageMargin > dm.widthPixels / 5) {
            tapPageMargin = dm.widthPixels / 5;
        }
        // set view backgroundColor
        setBackgroundColor(ContextCompat.getColor(mContext, R.color.muPDFReaderView_bg));
    }

    public MuPDFReaderView(Context context) {
        super(context);
        mContext = context;
        setup();
    }

    public MuPDFReaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setup();

    }
/*onSingleTapUp got null object reference crash */

    //  Method triggered by clicking the screen to turn the page
/*    public boolean onSingleTapUp(MotionEvent e) {
        LinkInfo link;
        if (mMode == MuPDFReaderView.Mode.Viewing && !tapDisabled) {
            MuPDFView pageView = (MuPDFView) getDisplayedView();
*//*          commented by me due to crash
  Hit item = pageView.passClickEvent(e.getX(), e.getY());
            onHit(item);
            if (item == Hit.Nothing) {
                if (mLinksEnabled && (link = pageView.hitLink(e.getX(), e.getY())) != null) {
                    link.acceptVisitor(new LinkInfoVisitor() {
                        @Override
                        public void visitInternal(LinkInfoInternal li) {
                            // Clicked on an internal (GoTo) link
                            setDisplayedViewIndex(li.pageNumber);
                        }

                        @Override
                        public void visitExternal(LinkInfoExternal li) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri
                                    .parse(li.url));
                            mContext.startActivity(intent);
                        }

                        @Override
                        public void visitRemote(LinkInfoRemote li) {
                            // Clicked on a remote (GoToR) link
                        }
                    });
                } else if (e.getX() < tapPageMargin) {
                    super.smartMoveBackwards();
                } else if (e.getX() > super.getWidth() - tapPageMargin) {
                    super.smartMoveForwards();
                } else if (e.getY() < tapPageMargin) {
                    super.smartMoveBackwards();
                } else if (e.getY() > super.getHeight() - tapPageMargin) {
                    super.smartMoveForwards();
                } else {
                    onTapMainDocArea();
                }
            }*//*
        }
        return super.onSingleTapUp(e);
    }*/

    public boolean onSingleTapUp(MotionEvent e) {
        LinkInfo link;

        if (mMode == Mode.Viewing && !tapDisabled) {
            MuPDFView pageView = (MuPDFView) getDisplayedView();
            Hit item = pageView.passClickEvent(e.getX(), e.getY());
            onHit(item);
            if (item == Hit.Nothing) {
                if (mLinksEnabled && (link = pageView.hitLink(e.getX(), e.getY())) != null) {
//                    link.acceptVisitor(new LinkInfoVisitor() {
//                        @Override
//                        public void visitInternal(LinkInfoInternal li) {
//                            // Clicked on an internal (GoTo) link
//                            setDisplayedViewIndex(li.pageNumber);
//                        }
//
//                        @Override
//                        public void visitExternal(LinkInfoExternal li) {
//                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri
//                                    .parse(li.url));
//                            mContext.startActivity(intent);
//                        }
//
//                        @Override
//                        public void visitRemote(LinkInfoRemote li) {
//                            // Clicked on a remote (GoToR) link
//                        }
//                    });
                } else if (e.getX() < tapPageMargin) {
                    super.smartMoveBackwards();
                } else if (e.getX() > super.getWidth() - tapPageMargin) {
                    super.smartMoveForwards();
                } else if (e.getY() < tapPageMargin) {
                    super.smartMoveBackwards();
                } else if (e.getY() > super.getHeight() - tapPageMargin) {
                    super.smartMoveForwards();
                } else {
                    onTapMainDocArea();
                }
            }
        }
        return super.onSingleTapUp(e);
    }


    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        MuPDFView pageView = (MuPDFView) getDisplayedView();
        switch (mMode) {
            case Viewing:
                if (!tapDisabled)
                    onDocMotion();
                return super.onScroll(e1, e2, distanceX, distanceY);
            case Selecting:
                if (pageView != null)
                    pageView.selectText(e1.getX(), e1.getY(), e2.getX(), e2.getY());
                return true;
            default:
                return true;
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        switch (mMode) {
            case Viewing:
                return super.onFling(e1, e2, velocityX, velocityY);
            default:
                return true;
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);
      //  MuPDFView pageView = (MuPDFView) getDisplayedView();
        switch (mMode) {
            case Viewing:
                onLongPress();
            default:

        }




    }

    public boolean onScaleBegin(ScaleGestureDetector d) {
        // Disabled showing the buttons until next touch.
        // Not sure why this is needed, but without it
        // pinch zoom can make the buttons appear
        tapDisabled = true;
        return super.onScaleBegin(d);
    }
    float initialY;
    float startYPoint;
    String moveDirection = "";
    public boolean onTouchEvent(MotionEvent event) {

        if (mMode == MuPDFReaderView.Mode.Drawing) {
            float x = event.getX();
            float y = event.getY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    //added by me not tested may b cause problem
//                    Log.d("DSID", "undoDraw ----ACTION_UP Called");
                  /*
                  currently giving crash here
                  MuPDFPageView pageView = null;
                    if(pageView.path!=null){
                        Log.d("DSID", "onDraw ACTION_UP:I am here called ");
                        pageView.addPathToHistory(pageView.path);
                        pageView.path = null;
                    }*/
                    // invalidate();
                    break;
            }
        }
        if (mMode == Mode.Selecting  ) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = event.getX();
                    y = event.getY();
                    actionDSelection();
                    showCopyBtn(-1f, -1f,-1f,false);
                    initialY = y;
                    //Log.i("TETXTTOUCH", "onTouchEvent: ACTION_DOWN");
                    break;
                case MotionEvent.ACTION_MOVE:
                    //  Log.i("TETXTTOUCH", "onTouchEvent: ACTION_MOVE");
                    float deltaY = event.getY() - initialY;
                    if (deltaY > 0) {
                        // User moved from top to bottom
                        moveDirection = "UP";
                        Log.i("TETXTTOUCH", "onTouchEvent: Moved from TOP to BOTTOM");
                    } else if (deltaY < 0) {
                        // User moved from bottom to top
                        moveDirection = "DOWN";
                        Log.i("TETXTTOUCH", "onTouchEvent: Moved from BOTTOM to TOP");
                    }
                    break;
                case MotionEvent.ACTION_UP:
                   if(moveDirection.equals("DOWN")) {
                       currentY = event.getY();
                   }else{
                       currentY = -1f;
                   }
                    MuPDFView pageView1 = (MuPDFView) getDisplayedView();
                    if (pageView1 != null) {
                        pageView1.isTextSelected();
                        showCopyBtn(x, y,currentY,true);
                    }

                    Log.i("TETXTTOUCH", "onTouchEvent: ACTION_UP");
                    break;
            }
        }

        if ((event.getAction() & event.getActionMasked()) == MotionEvent.ACTION_DOWN) {
            tapDisabled = false;
        }

        return super.onTouchEvent(event);
    }

    private void actionDSelection() {
        MuPDFView pageView = (MuPDFView) getDisplayedView();
        if (pageView != null) {
            pageView.deselectText();
        }
    }


    String action ="";
    float currentY =-1f;
    float x = 0f;
    float y = 0f;

    private float mX, mY;
    private float cX, cY;

    private static final float TOUCH_TOLERANCE = 2;

    private void touch_start(float x, float y) {

        MuPDFView pageView = (MuPDFView) getDisplayedView();
        if (pageView != null) {
            pageView.startDraw(x, y);
        }
        mX = x;
        mY = y;
    }

    private void showCopyBtn(float x, float y, Float direction, Boolean actionDetect) {
        MuPDFView pageView = (MuPDFView) getDisplayedView();
        if (pageView != null) {
            pageView.showCopyRect(x, y,direction,pageView.isTextSelected(),actionDetect);
        }
//        cX = x;
//        cY = y;
    }


    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            MuPDFView pageView = (MuPDFView) getDisplayedView();
            if (pageView != null) {
                pageView.continueDraw(x, y);
            }
            mX = x;
            mY = y;
        }
    }

    protected void onChildSetup(int i, View v) {
        if (SearchTaskResult.get() != null && SearchTaskResult.get().pageNumber == i) {
            ((MuPDFView) v).setSearchBoxes(SearchTaskResult.get().searchBoxes);
        } else {
            ((MuPDFView) v).setSearchBoxes(null);
        }

        ((MuPDFView) v).setLinkHighlighting(mLinksEnabled);

        // Set hyperlink color
        if (isLinkHighlightColor) {
            ((MuPDFView) v).setLinkHighlightColor(mLinkHighlightColor);
        }

        ((MuPDFView) v).setChangeReporter(new Runnable() {
            public void run() {
                applyToChildren(new ReaderView.ViewMapper() {
                    @Override
                    public void applyToView(View view) {
                        ((MuPDFView) view).update();
                    }
                });
            }
        });
    }

    protected void onMoveToChild(int i) {
        if (SearchTaskResult.get() != null
                && SearchTaskResult.get().pageNumber != i) {
            SearchTaskResult.set(null);
            resetupChildren();
        }
        checkMuPDFReaderViewListener();
        listener.onMoveToChild(i);
    }

    @Override
    protected void onMoveOffChild(int i) {
        View v = getView(i);
        if (v != null)
            ((MuPDFView) v).deselectAnnotation();
    }

    protected void onSettle(View v) {
        // When the layout has settled ask the page to render
        // in HQ
        ((MuPDFView) v).updateHq(false);
    }

    protected void onUnsettle(View v) {
        // When something changes making the previous settled view
        // no longer appropriate, tell the page to remove HQ
        ((MuPDFView) v).removeHq();
    }

    @Override
    protected void onNotInUse(View v) {
        ((MuPDFView) v).releaseResources();
    }

    @Override
    protected void onScaleChild(View v, Float scale) {
        ((MuPDFView) v).setScale(scale);
    }

    /**
     * Set up listening events
     *
     * @param listener
     */
    public void setListener(MuPDFReaderViewListener listener) {
        this.listener = listener;
    }

    private void checkMuPDFReaderViewListener() {
        if (listener == null) {
            listener = new MuPDFReaderViewListener() {
                @Override
                public void onMoveToChild(int i) {

                }

                @Override
                public void onTapMainDocArea() {

                }

                @Override
                public void onDocMotion() {

                }

                @Override
                public void onHit(Hit item) {

                }

                @Override
                public void onLongPress() {
                    Log.i("LONGPRESS", "onLongPress: hitting Long press READERVIEW");

                }


            };
        }
    }
}

