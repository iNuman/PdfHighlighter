package com.artifex.mupdfdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatImageView;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.Stack;

import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.functions.Function5;

class OpaqueImageView extends AppCompatImageView {

    public OpaqueImageView(Context context) {
        super(context);
    }

    @Override
    public boolean isOpaque() {
        return true;
    }
}

interface TextProcessor {
    void onStartLine();

    void onWord(TextWord word);

    void onEndLine();
}

class TextSelector {
    final private TextWord[][] mText;
    final private RectF mSelectBox;

    public TextSelector(TextWord[][] text, RectF selectBox) {
        mText = text;
        mSelectBox = selectBox;
    }

    public void select(TextProcessor tp) {
        if (mText == null || mSelectBox == null)
            return;

        ArrayList<TextWord[]> lines = new ArrayList<TextWord[]>();
        for (TextWord[] line : mText)
            if (line[0].bottom > mSelectBox.top && line[0].top < mSelectBox.bottom)
                lines.add(line);

        Iterator<TextWord[]> it = lines.iterator();
        while (it.hasNext()) {
            TextWord[] line = it.next();
            boolean firstLine = line[0].top < mSelectBox.top;
            boolean lastLine = line[0].bottom > mSelectBox.bottom;
            float start = Float.NEGATIVE_INFINITY;
            float end = Float.POSITIVE_INFINITY;

            if (firstLine && lastLine) {
                start = Math.min(mSelectBox.left, mSelectBox.right);
                end = Math.max(mSelectBox.left, mSelectBox.right);
            } else if (firstLine) {
                start = mSelectBox.left;
            } else if (lastLine) {
                end = mSelectBox.right;
            }

            tp.onStartLine();
            for (TextWord word : line)
                if (word.right > start && word.left < end)
                    tp.onWord(word);

            tp.onEndLine();
        }
    }
}

public abstract class PageView extends ViewGroup {
    private static final float ITEM_SELECT_BOX_WIDTH = 4.0f;// The width of the border when selected

    //private static final int HIGHLIGHT_COLOR = 0x80ff5722;// Color when text is selected
    private static final int HIGHLIGHT_COLOR = 0x80ade1f6;// Color when text is selected
    private int LINK_COLOR = 0x80ff5722;// Hyperlink color
    //    private static final int BOX_COLOR = 0xFF4444FF;
    private static final int BOX_COLOR = 0xFF696969;// The color of the border when selected
    //    private static final int INK_COLOR = 0xFF000000;// 绘制时画笔颜色
    private int INK_COLOR = 0xFF000000;//Brush color when drawing
    //    private static final float INK_THICKNESS = 10.0f;// 绘制时画笔宽
    private float INK_THICKNESS = 10.0f;//Brush width when drawing
    private float current_scale;

    private static final int BACKGROUND_COLOR = 0xFFFFFFFF;


    private static final int PROGRESS_DIALOG_DELAY = 200;
    protected final Context mContext;
    protected int mPageNumber;
    private Point mParentSize;
    protected Point mSize;   // Size of page at minimum zoom
    protected float mSourceScale;

    private ImageView mEntire; // Image rendered at minimum zoom
    private Bitmap mEntireBm;
    private Matrix mEntireMat;
    private AsyncTask<Void, Void, TextWord[][]> mGetText;
    private AsyncTask<Void, Void, LinkInfo[]> mGetLinkInfo;
    private CancellableAsyncTask<Void, Void> mDrawEntire;

    private Point mPatchViewSize; // View size on the basis of which the patch was created
    private Rect mPatchArea;
    private ImageView mPatch;
    private Bitmap mPatchBm;
    private CancellableAsyncTask<Void, Void> mDrawPatch;
    private RectF mSearchBoxes[];
    protected LinkInfo mLinks[];
    private RectF mSelectBox;
    private TextWord mText[][];
    private RectF mItemSelectBox;
    protected ArrayList<ArrayList<PointF>> mDrawing;
    protected ArrayList<ArrayList<PointF>> mDrawingRedo = new ArrayList<ArrayList<PointF>>();
    private View mSearchView;
    private boolean mIsBlank;
    private boolean mHighlightLinks;// Whether to highlight hyperlinks

    private ProgressBar mBusyIndicator;
    private final Handler mHandler = new Handler();
    public static String dayNight = "LIGHT";
    public static boolean continueScrolling = false;

    public PageView(Context c, Point parentSize, Bitmap sharedHqBm) {
        super(c);
        mContext = c;
        mParentSize = parentSize;
        setBackgroundColor(BACKGROUND_COLOR);
        mEntireBm = Bitmap.createBitmap(parentSize.x, parentSize.y, Bitmap.Config.ARGB_8888);
        mPatchBm = sharedHqBm;
        mEntireMat = new Matrix();
    }

    protected abstract CancellableTaskDefinition<Void, Void> getDrawPageTask(Bitmap bm, int sizeX, int sizeY, int patchX, int patchY, int patchWidth, int patchHeight);

    protected abstract CancellableTaskDefinition<Void, Void> getUpdatePageTask(Bitmap bm, int sizeX, int sizeY, int patchX, int patchY, int patchWidth, int patchHeight);

    protected abstract LinkInfo[] getLinkInfo();

    protected abstract TextWord[][] getText();

    protected abstract void addMarkup(PointF[] quadPoints, Annotation.Type type);

    private void reinit() {
        // Cancel pending render task
        if (mDrawEntire != null) {
            mDrawEntire.cancelAndWait();
            mDrawEntire = null;
        }

        if (mDrawPatch != null) {
            mDrawPatch.cancelAndWait();
            mDrawPatch = null;
        }

        if (mGetLinkInfo != null) {
            mGetLinkInfo.cancel(true);
            mGetLinkInfo = null;
        }

        if (mGetText != null) {
            mGetText.cancel(true);
            mGetText = null;
        }

        mIsBlank = true;
        mPageNumber = 0;

        if (mSize == null)
            mSize = mParentSize;

        if (mEntire != null) {
            mEntire.setImageBitmap(null);
            mEntire.invalidate();
        }

        if (mPatch != null) {
            mPatch.setImageBitmap(null);
            mPatch.invalidate();


        }

        mPatchViewSize = null;
        mPatchArea = null;

        mSearchBoxes = null;
        mLinks = null;
        mSelectBox = null;
        mText = null;
        mItemSelectBox = null;
    }


    private Bitmap invert(Bitmap src) {
        int height = src.getHeight();
        int width = src.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, src.getConfig());
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        ColorMatrix matrixGrayscale = new ColorMatrix();
        //  matrixGrayscale.setSaturation(0);

        ColorMatrix matrixInvert = new ColorMatrix();
        matrixInvert.set(new float[]
                {
                        -1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                });
        matrixInvert.preConcat(matrixGrayscale);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixInvert);
        paint.setColorFilter(filter);

        canvas.drawBitmap(src, 0, 0, paint);
        // src.recycle();
        return bitmap;
    }


    private Bitmap invertToYellow(Bitmap src) {
        int height = src.getHeight();
        int width = src.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(width, height, src.getConfig());
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        // Create a color matrix for dark orange tint
        ColorMatrix matrixOrange = new ColorMatrix();
        matrixOrange.set(new float[]{
                1.5f, 0.2f, 0, 0, 0,  // Boost red significantly
                0.1f, 0.9f, 0, 0, 0,  // Reduce green slightly
                0, 0.1f, 0.7f, 0, 0,  // Reduce blue more
                0, 0, 0, 1, 0   // Keep alpha unchanged
        });
        // Decrease brightness slightly
        ColorMatrix matrixBrightness = new ColorMatrix();
        matrixBrightness.setScale(0.9f, 0.9f, 0.9f, 1);  // Multiply all color channels by 0.9
        // Apply brightness adjustment to the orange matrix
        matrixOrange.preConcat(matrixBrightness);
        // Optionally, increase saturation slightly
        ColorMatrix matrixSaturate = new ColorMatrix();
        matrixSaturate.setSaturation(1.1f);
        matrixOrange.preConcat(matrixSaturate);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixOrange);
        paint.setColorFilter(filter);
        canvas.drawBitmap(src, 0, 0, paint);
        return bitmap;
    }


    public void releaseResources() {
        reinit();

        if (mBusyIndicator != null) {
            removeView(mBusyIndicator);
            mBusyIndicator = null;
        }
    }

    public void releaseBitmaps() {
        reinit();

        //  recycle bitmaps before releasing them.

        if (mEntireBm != null)
            mEntireBm.recycle();
        mEntireBm = null;

        if (mPatchBm != null)
            mPatchBm.recycle();
        mPatchBm = null;
    }

    public void blank(int page) {
        reinit();
        mPageNumber = page;

        if (mBusyIndicator == null) {
            mBusyIndicator = new ProgressBar(mContext);
            mBusyIndicator.setIndeterminate(true);
            addView(mBusyIndicator);
        }

        setBackgroundColor(BACKGROUND_COLOR);
    }

    private Stack<Path> drawingHistory = new Stack<>();
    Path path = null;


    @SuppressLint("StaticFieldLeak")
    public void setPage(int page, PointF size) {
        // Cancel pending render task
        if (mDrawEntire != null) {
            mDrawEntire.cancelAndWait();
            mDrawEntire = null;
        }

        mIsBlank = false;
        // Highlights may be missing because mIsBlank was true on last draw
        if (mSearchView != null)
            mSearchView.invalidate();

        mPageNumber = page;
        if (mEntire == null) {
            mEntire = new OpaqueImageView(mContext);
            mEntire.setScaleType(ImageView.ScaleType.MATRIX);
            addView(mEntire);
        }

        // Calculate scaled size that fits within the screen limits
        // This is the size at minimum zoom
        mSourceScale = Math.min(mParentSize.x / size.x, mParentSize.y / size.y);
        mSize = new Point((int) (size.x * mSourceScale), (int) (size.y * mSourceScale));
        // change height to full but height prob  mSize = new Point((int) (size.x * mSourceScale), mParentSize.y);
        mEntire.setImageBitmap(null);
        mEntire.invalidate();
        // Get the link info in the background
        mGetLinkInfo = new AsyncTask<Void, Void, LinkInfo[]>() {
            protected LinkInfo[] doInBackground(Void... v) {
                return getLinkInfo();
            }

            protected void onPostExecute(LinkInfo[] v) {
                mLinks = v;
                if (mSearchView != null)
                    mSearchView.invalidate();
            }
        };

        mGetLinkInfo.execute();

        // Render the page in the background
        mDrawEntire = new CancellableAsyncTask<Void, Void>(getDrawPageTask(mEntireBm, mSize.x, mSize.y, 0, 0, mSize.x, mSize.y)) {

            @Override
            public void onPreExecute() {
                setBackgroundColor(BACKGROUND_COLOR);
                mEntire.setImageBitmap(null);
                mEntire.invalidate();

                if (mBusyIndicator == null) {
                    mBusyIndicator = new ProgressBar(mContext);
                    mBusyIndicator.setIndeterminate(true);
                    addView(mBusyIndicator);
                    mBusyIndicator.setVisibility(INVISIBLE);
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            if (mBusyIndicator != null)
                                mBusyIndicator.setVisibility(VISIBLE);
                        }
                    }, PROGRESS_DIALOG_DELAY);
                }
            }

            @Override
            public void onPostExecute(Void result) {
                removeView(mBusyIndicator);
                mBusyIndicator = null;
                Log.i("ZOOMTHEMEPROB", "setPage: ----onPostExecute");
                if (Objects.equals(dayNight, "DARK")) {
                    mEntire.setImageBitmap(invert(mEntireBm));
                    mEntire.invalidate();
                } else if (Objects.equals(dayNight, "LIGHT")) {
                    mEntire.setImageBitmap(mEntireBm);
                    mEntire.invalidate();
                } else if (Objects.equals(dayNight, "EYES")) {
                    mEntire.setImageBitmap(invertToYellow(mEntireBm));
                    mEntire.invalidate();
                }
                // mEntire.setImageBitmap(mEntireBm);
                // mEntire.setImageBitmap(invert(mEntireBm));
                //mEntire.invalidate();
                setBackgroundColor(Color.TRANSPARENT);

            }
        };

        mDrawEntire.execute();

        if (mSearchView == null) {
            mSearchView = new View(mContext) {
                @Override
                protected void onDraw(final Canvas canvas) {
                    super.onDraw(canvas);
                    // Work out current total scale factor
                    // from source to view
                    final float scale = mSourceScale * (float) getWidth() / (float) mSize.x;
                    current_scale = scale;

                    final Paint paint = new Paint();

                    if (!mIsBlank && mSearchBoxes != null) {
                        //Search color
//                        paint.setColor(HIGHLIGHT_COLOR);
//                        paint.setColor(mContext.getResources().getColor(R.color.search_bg));
                        paint.setColor(SharedPreferencesUtil.getSearchTextColor());

                        for (RectF rect : mSearchBoxes)
                            canvas.drawRect(rect.left * scale, rect.top * scale,
                                    rect.right * scale, rect.bottom * scale,
                                    paint);
                    }
                    if (!mIsBlank && mLinks != null && mHighlightLinks) {
                        // Hyperlink color
                        paint.setColor(LINK_COLOR);
//                        paint.setColor(mContext.getResources().getColor(R.color.link_bg));
                        for (LinkInfo link : mLinks)
                            canvas.drawRect(link.rect.left * scale, link.rect.top * scale,
                                    link.rect.right * scale, link.rect.bottom * scale,
                                    paint);
                    }

                    if (mSelectBox != null && mText != null) {
                        paint.setColor(HIGHLIGHT_COLOR);
//                        paint.setColor(SharedPreferencesUtil.getSearchTextColor());
                        processSelectedText(new TextProcessor() {
                            RectF rect;

                            public void onStartLine() {
                                rect = new RectF();
                            }

                            public void onWord(TextWord word) {
                                rect.union(word);
                            }

                            public void onEndLine() {
                                if (!rect.isEmpty()) {
                                    Log.i("lineEnd", "onEndLine: Before draw rect");
                                    canvas.drawRect(rect.left * scale, rect.top * scale, rect.right * scale, rect.bottom * scale, paint);
                                }

                            }
                        });
                    }
                    /*Annotation Rectangle Removed */
//                    // Outer border when selected
//                    if (mItemSelectBox != null) {
//                        paint.setStyle(Paint.Style.STROKE);
//                        // Border width
//                        paint.setStrokeWidth(ITEM_SELECT_BOX_WIDTH);
//                        // border color
//                        paint.setColor(BOX_COLOR);
////                        paint.setColor(mContext.getResources().getColor(R.color.link_bg));
//                        canvas.drawRect(mItemSelectBox.left * scale, mItemSelectBox.top * scale, mItemSelectBox.right * scale, mItemSelectBox.bottom * scale, paint);
//                    }
                    if (mDrawing != null) {
                        path = new Path();
                        // Path path = new Path();
                        PointF p;
                        paint.setAntiAlias(true);
                        paint.setDither(true);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        paint.setStrokeCap(Paint.Cap.ROUND);
                        paint.setStyle(Paint.Style.FILL);
                        // Brush width when drawing
                        paint.setStrokeWidth(INK_THICKNESS * scale);
//                        Logger.e("zyw", "StrokeWidth = " + INK_THICKNESS * scale);
                        // Brush color when drawing
                        paint.setColor(INK_COLOR);
                        Iterator<ArrayList<PointF>> it = mDrawing.iterator();
                        while (it.hasNext()) {
                            ArrayList<PointF> arc = it.next();
                            if (arc.size() >= 2) {
                                Iterator<PointF> iit = arc.iterator();
                                p = iit.next();
                                float mX = p.x * scale;
                                float mY = p.y * scale;
                                path.moveTo(mX, mY);
                                while (iit.hasNext()) {
                                    p = iit.next();
                                    float x = p.x * scale;
                                    float y = p.y * scale;
                                    path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                                    mX = x;
                                    mY = y;
                                }
                                path.lineTo(mX, mY);
                            } else {
                                p = arc.get(0);
                                canvas.drawCircle(p.x * scale, p.y * scale, INK_THICKNESS * scale / 2, paint);
                            }
                        }
                        paint.setStyle(Paint.Style.STROKE);
                        canvas.drawPath(path, paint);
                    }
                    /*issue called multiple times call only when On Touch Action Up */
                   /* if(path!=null){
                        Log.d("DSID", "onDraw:I am here called ");
                        addPathToHistory(path);
                        path = null;
                    }*/
                }
            };
            addView(mSearchView);
        }
        requestLayout();
    }

    /**
     * added by me for undo redo functionality
     *
     * @param path
     */
    private void addPathToHistory(Path path) {
        drawingHistory.push(new Path(path));
        Log.d("DSID", "addPathToHistory" + drawingHistory.size());
    }

    public void setSearchBoxes(RectF searchBoxes[]) {
        mSearchBoxes = searchBoxes;
        if (mSearchView != null)
            mSearchView.invalidate();
    }


    public void showCopyRect(float x, float y, Float moveDirect,Boolean isTextSelected,Boolean actionDetection) {
        Log.i("TETXTTOUCH", "showCopyRect: yes I'm in PageView with " + x + " " + y);
        if(actionDetection){
            //check text selected visible button
            if ( isTextSelected ) {
                Function5<Float, Float, Float,Boolean, Boolean, Unit> onClick = Ext.Companion.getOnClick();
                if (onClick != null) {
                    //  onClick.invoke(x, y, moveDirect,isTextSelected);
                    onClick.invoke(x, y, moveDirect,true, getBoolean()); //before
                    //  mSearchView.invalidate();
                }
            }
        }else{
            //deselecttext button gone
                Function5<Float, Float, Float,Boolean, Boolean, Unit> onClick = Ext.Companion.getOnClick();
                if (onClick != null) {
                    onClick.invoke(x, y, moveDirect,false, false);
                }

        }

    }
    private void copyClickListen(){

    }

    public boolean getBoolean() {
        if(mSelectBox != null && mText != null){
            return true;
        } else {
            return false;
        }
    }


    /**
     * Set whether to highlight hyperlinks
     *
     * @param f boolean
     */
    public void setLinkHighlighting(boolean f) {
        mHighlightLinks = f;
        if (mSearchView != null)
            mSearchView.invalidate();
    }

    /**
     * Set hyperlink color
     *
     * @param color color value
     */
    public void setLinkHighlightColor(int color) {
        LINK_COLOR = color;
        if (mHighlightLinks) {
            if (mSearchView != null) {
                mSearchView.invalidate();
            }
        }
    }

    public void deselectText() {
        Log.i("TEXTHIGHLIGHT", "deselectText: before invalidate ");
        mSelectBox = null;
        mSearchView.invalidate();
    }

    @SuppressLint("StaticFieldLeak")
    public void selectText(float x0, float y0, float x1, float y1) {
        float scale = mSourceScale * (float) getWidth() / (float) mSize.x;
        float docRelX0 = (x0 - getLeft()) / scale;
        float docRelY0 = (y0 - getTop()) / scale;
        float docRelX1 = (x1 - getLeft()) / scale;
        float docRelY1 = (y1 - getTop()) / scale;

        // Order on Y but maintain the point grouping
        if (docRelY0 <= docRelY1){
            Log.i("TEXTSELECTPROB", "ORDER ON Y<: in fun");
            mSelectBox = new RectF(docRelX0, docRelY0 + 2, docRelX1, docRelY1 + 2);
        }
        else{
            Log.i("TEXTSELECTPROB", "ORDER ON ELSE : in fun");
            mSelectBox = new RectF(docRelX1, docRelY1 + 2, docRelX0, docRelY0 + 2);
        }
        mSearchView.invalidate();
        if (mGetText == null) {
            mGetText = new AsyncTask<Void, Void, TextWord[][]>() {
                @Override
                protected TextWord[][] doInBackground(Void... params) {
                    return getText();
                }

                @Override
                protected void onPostExecute(TextWord[][] result) {
                    mText = result;
                    Log.i("TEXTSELECTPROB", "onPostExecute");
                   // Log.i("TEXTSPACEISSUE", "textLines: "+mText[0][0].w);
                    mSearchView.invalidate();
                }
            };

            mGetText.execute();
        }
    }

    /**
     * start drawing from this function
     *
     * @param x
     * @param y
     */
    public void startDraw(float x, float y) {
        float scale = mSourceScale * (float) getWidth() / (float) mSize.x;
        float docRelX = (x - getLeft()) / scale;
        float docRelY = (y - getTop()) / scale;
        if (mDrawing == null)
            mDrawing = new ArrayList<ArrayList<PointF>>();
        ArrayList<PointF> arc = new ArrayList<PointF>();
        arc.add(new PointF(docRelX, docRelY));
        mDrawing.add(arc);
        mSearchView.invalidate();
    }

    public void undoDraw() {
        if (mDrawing != null && mDrawing.size() > 0) {
            try {
                ArrayList<PointF> removedDrawing = mDrawing.remove(mDrawing.size() - 1);
                if (mDrawingRedo.size() > 0) {
                    Log.d("DSID", "undoDraw ---:Before Adding" + mDrawingRedo.size());
                }
                mDrawingRedo.add(removedDrawing);
                Log.d("DSID", "undoDraw ---:Called" + mDrawingRedo.size());
                mSearchView.invalidate();
            } catch (Exception exception) {
                Log.d("DSID", "Exception" + exception.getMessage());
            }


        }
    }

    /**
     * after undo and redo testing delete this block of code
     */
       /*
        if (!drawingHistory.isEmpty()) {
                drawingHistory.pop();
                Log.d("DSID", "undoDraw IF--:Called"+drawingHistory.size());
                *//*should remove draw lines step by step not at once *//*
              //  mDrawing = null;
                mSearchView.invalidate();
            }else{
                Log.d("DSID", "undoDraw ELSE ---:Called");
            }*/
    public void redoDraw() {
        if (mDrawingRedo != null && mDrawingRedo.size() > 0) {
            ArrayList<PointF> removedObj = mDrawingRedo.remove(mDrawingRedo.size() - 1);
            Log.d("DSID", "redoDraw ---:Called" + mDrawingRedo.size());
            mDrawing.add(removedObj);
            mSearchView.invalidate();
        }
    }

    public void continueDraw(float x, float y) {
        float scale = mSourceScale * (float) getWidth() / (float) mSize.x;
        float docRelX = (x - getLeft()) / scale;
        float docRelY = (y - getTop()) / scale;

        if (mDrawing != null && mDrawing.size() > 0) {
            ArrayList<PointF> arc = mDrawing.get(mDrawing.size() - 1);
            arc.add(new PointF(docRelX, docRelY));
            mSearchView.invalidate();
        }
    }

    public void cancelDraw() {
        mDrawing = null;
        mDrawingRedo = null;
        mSearchView.invalidate();
    }

    protected PointF[][] getDraw() {
        if (mDrawing == null)
            return null;
        PointF[][] path = new PointF[mDrawing.size()][];
        for (int i = 0; i < mDrawing.size(); i++) {
            ArrayList<PointF> arc = mDrawing.get(i);
            path[i] = arc.toArray(new PointF[arc.size()]);
        }
        return path;
    }

    /**
     * Set brush color
     *
     * @param color color Value
     *              mSearchView invalidation added by me test this properly
     */
    public void setInkColor(int color) {
        INK_COLOR = color;
        if (mSearchView != null)
            mSearchView.invalidate();
    }

    /**
     * Set brush thickness
     *
     * @param inkThickness thickness value
     *                     mSearchView invalidation added by me test this properly
     */
    public void setPaintStrockWidth(float inkThickness) {
        INK_THICKNESS = inkThickness;
        if (mSearchView != null)
            mSearchView.invalidate();
    }

    protected float getInkThickness() {
        if (current_scale == 0) {
            return 9.07563f / 2;
        } else {
//            return (INK_THICKNESS * current_scale) / 2;
            return INK_THICKNESS / 2;
        }
    }

    public float getCurrentScale() {
        if (current_scale == 0) {
            return 9.07563f;
        }
        return current_scale;
    }

    protected float[] getColor() {

        return changeColor(INK_COLOR);
    }

    /**
     * Convert decimal color values to RGB format
     *
     * @param color
     * @return
     */
    private float[] changeColor(int color) {

        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        float colors[] = new float[3];
        colors[0] = red / 255f;
        colors[1] = green / 255f;
        colors[2] = blue / 255f;
        return colors;
    }

    private float[] changeAnnotationColor(int color) {

        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        float colors[] = new float[3];
        colors[0] = red / 255f;
        colors[1] = green / 255f;
        colors[2] = blue / 255f;

        return colors;
    }

    protected void processSelectedText(TextProcessor tp) {
        (new TextSelector(mText, mSelectBox)).select(tp);
    }

    public void setItemSelectBox(RectF rect) {
        mItemSelectBox = rect;
        if (mSearchView != null)
            mSearchView.invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int x, y;
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                x = mSize.x;
                break;
            default:
                x = MeasureSpec.getSize(widthMeasureSpec);
        }
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                y = mSize.y;
                break;
            default:
                y = MeasureSpec.getSize(heightMeasureSpec);
        }

        setMeasuredDimension(x, y);

        if (mBusyIndicator != null) {
            int limit = Math.min(mParentSize.x, mParentSize.y) / 2;
            mBusyIndicator.measure(MeasureSpec.AT_MOST | limit, MeasureSpec.AT_MOST | limit);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int w = right - left;
        int h = bottom - top;

        if (mEntire != null) {
            if (mEntire.getWidth() != w || mEntire.getHeight() != h) {
                mEntireMat.setScale(w / (float) mSize.x, h / (float) mSize.y);
                mEntire.setImageMatrix(mEntireMat);
                mEntire.invalidate();
            }
            mEntire.layout(0, 0, w, h);
        }

        if (mSearchView != null) {
            mSearchView.layout(0, 0, w, h);
        }

        if (mPatchViewSize != null) {
            if (mPatchViewSize.x != w || mPatchViewSize.y != h) {
                // Zoomed since patch was created
                mPatchViewSize = null;
                mPatchArea = null;
                if (mPatch != null) {
                    mPatch.setImageBitmap(null);
                    mPatch.invalidate();
                }
            } else {
                mPatch.layout(mPatchArea.left, mPatchArea.top, mPatchArea.right, mPatchArea.bottom);
            }
        }

        if (mBusyIndicator != null) {
            int bw = mBusyIndicator.getMeasuredWidth();
            int bh = mBusyIndicator.getMeasuredHeight();

            mBusyIndicator.layout((w - bw) / 2, (h - bh) / 2, (w + bw) / 2, (h + bh) / 2);
        }
    }

    public void updateHq(boolean update) {
        Rect viewArea = new Rect(getLeft(), getTop(), getRight(), getBottom());
        if (viewArea.width() == mSize.x || viewArea.height() == mSize.y) {
            // If the viewArea's size matches the unzoomed size, there is no need for an hq patch
            if (mPatch != null) {
                mPatch.setImageBitmap(null);
                mPatch.invalidate();
            }
        } else {
            final Point patchViewSize = new Point(viewArea.width(), viewArea.height());
            final Rect patchArea = new Rect(0, 0, mParentSize.x, mParentSize.y);

            // Intersect and test that there is an intersection
            if (!patchArea.intersect(viewArea))
                return;

            // Offset patch area to be relative to the view top left
            patchArea.offset(-viewArea.left, -viewArea.top);

            boolean area_unchanged = patchArea.equals(mPatchArea) && patchViewSize.equals(mPatchViewSize);

            // If being asked for the same area as last time and not because of an update then nothing to do
            if (area_unchanged && !update)
                return;

            boolean completeRedraw = !(area_unchanged);
            // Stop the drawing of previous patch if still going
            if (mDrawPatch != null) {
                mDrawPatch.cancelAndWait();
                mDrawPatch = null;
            }

            // Create and add the image view if not already done
            if (mPatch == null) {
                mPatch = new OpaqueImageView(mContext);
                mPatch.setScaleType(ImageView.ScaleType.MATRIX);
                addView(mPatch);
                mSearchView.bringToFront();
            }

            CancellableTaskDefinition<Void, Void> task;

            if (completeRedraw)
                task = getDrawPageTask(mPatchBm, patchViewSize.x, patchViewSize.y,
                        patchArea.left, patchArea.top,
                        patchArea.width(), patchArea.height());
            else
                task = getUpdatePageTask(mPatchBm, patchViewSize.x, patchViewSize.y,
                        patchArea.left, patchArea.top,
                        patchArea.width(), patchArea.height());

            mDrawPatch = new CancellableAsyncTask<Void, Void>(task) {

                public void onPostExecute(Void result) {
                    mPatchViewSize = patchViewSize;
                    mPatchArea = patchArea;
              /*      if (dayNight) {
                        mPatch.setImageBitmap(invert(mPatchBm));
                    } else {
                        mPatch.setImageBitmap(mPatchBm);
                    }*/
                    if (Objects.equals(dayNight, "DARK")) {
                        mPatch.setImageBitmap(invert(mPatchBm));
                    } else if (Objects.equals(dayNight, "LIGHT")) {
                        mPatch.setImageBitmap(mPatchBm);
                    } else if (Objects.equals(dayNight, "EYES")) {
                        mPatch.setImageBitmap(invertToYellow(mPatchBm));
                    }
                    /// mPatch.setImageBitmap(mPatchBm);
                    // mPatch.setImageBitmap(invert(mPatchBm));
                    mPatch.invalidate();
                    //requestLayout();
                    // Calling requestLayout here doesn't lead to a later call to layout. No idea
                    // why, but apparently others have run into the problem.
                    mPatch.layout(mPatchArea.left, mPatchArea.top, mPatchArea.right, mPatchArea.bottom);
                }
            };

            mDrawPatch.execute();
        }
    }

    public void update() {
        // Cancel pending render task
        if (mDrawEntire != null) {
            mDrawEntire.cancelAndWait();
            mDrawEntire = null;
        }

        if (mDrawPatch != null) {
            mDrawPatch.cancelAndWait();
            mDrawPatch = null;
        }


        // Render the page in the background
        mDrawEntire = new CancellableAsyncTask<Void, Void>(getUpdatePageTask(mEntireBm, mSize.x, mSize.y, 0, 0, mSize.x, mSize.y)) {

            public void onPostExecute(Void result) {
                Log.i("TEXTHIGHLIGHT", "update:before setBitmap ");
            /*    if (dayNight) {
                    mEntire.setImageBitmap(invert(mEntireBm));
                    mEntire.invalidate();

                } else {
                    mEntire.setImageBitmap(mEntireBm);
                    mEntire.invalidate();

                }*/

                if (Objects.equals(dayNight, "DARK")) {
                    mEntire.setImageBitmap(invert(mEntireBm));
                    mEntire.invalidate();
                } else if (Objects.equals(dayNight, "LIGHT")) {
                    mEntire.setImageBitmap(mEntireBm);
                    mEntire.invalidate();
                } else if (Objects.equals(dayNight, "EYES")) {
                    mEntire.setImageBitmap(invertToYellow(mEntireBm));
                    mEntire.invalidate();
                }
                // mEntire.invalidate();
            }
        };

        mDrawEntire.execute();
        updateHq(true);
    }

    public void removeHq() {
        // Stop the drawing of the patch if still going
        if (mDrawPatch != null) {
            mDrawPatch.cancelAndWait();
            mDrawPatch = null;
        }

        // And get rid of it
        mPatchViewSize = null;
        mPatchArea = null;
        if (mPatch != null) {
            mPatch.setImageBitmap(null);
            mPatch.invalidate();
        }
    }

    public int getPage() {
        return mPageNumber;
    }

    @Override
    public boolean isOpaque() {
        return true;
    }
}

