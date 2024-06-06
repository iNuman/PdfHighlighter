package com.artifex.mupdfdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;



public class MuPDFPageAdapter extends BaseAdapter {

    private final Context mContext;
    private final MuPDFCore mCore;
    private final SparseArray<PointF> mPageSizes = new SparseArray<PointF>();
    private Bitmap mSharedHqBm;

    private OnPageChangeListener mOnPageChangeListener;
    public MuPDFPageAdapter(Context c, MuPDFCore core,OnPageChangeListener onPageChangeListener) {
        mContext = c;
        mCore = core;
        mOnPageChangeListener=onPageChangeListener;
    }
    @Override
    public int getCount() {
        return mCore.countPages();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    public void releaseBitmaps()
    {
        //  recycle and release the shared bitmap.
        if (mSharedHqBm!=null)
            mSharedHqBm.recycle();
        mSharedHqBm = null;
    }

    public void PageUp(int i){
        Log.i("PageIs", "PageUp: "+i);
        mOnPageChangeListener.onPageChanged(i);

    }

    public void PageDown(int i){
        Log.i("PageIs", "PageDown: "+i);
        mOnPageChangeListener.onPageChanged(i);
    }

    public void PageUp(int i, View pageView){
        Log.i("PageIs", "PageUp: "+i);
        mOnPageChangeListener.onPageChanged(i, pageView);

    }

    public void PageDown(int i, View pageView){
        Log.i("PageIs", "PageDown: "+i);
        mOnPageChangeListener.onPageChanged(i, pageView);
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final MuPDFPageView pageView;
        if (convertView == null) {
            if (mSharedHqBm == null || mSharedHqBm.getWidth() != parent.getWidth() || mSharedHqBm.getHeight() != parent.getHeight())
                mSharedHqBm = Bitmap.createBitmap(parent.getWidth(), parent.getHeight(), Bitmap.Config.ARGB_8888);
            pageView = new MuPDFPageView(mContext, mCore, new Point(parent.getWidth(), parent.getHeight()), mSharedHqBm);
        } else {
            pageView = (MuPDFPageView) convertView;
        }

        PointF pageSize = mPageSizes.get(position);
        if (pageSize != null) {
            // We already know the page size. Set it up
            // immediately
            pageView.setPage(position, pageSize);
        } else {
            // Page size as yet unknown. Blank it for now, and
            // start a background task to find the size
            pageView.blank(position);
            AsyncTask<Void,Void,PointF> sizingTask = new AsyncTask<Void,Void,PointF>() {
                @Override
                protected PointF doInBackground(Void... arg0) {
                    return mCore.getPageSize(position);
                }

                @Override
                protected void onPostExecute(PointF result) {
                    super.onPostExecute(result);
                    // We now know the page size
                    mPageSizes.put(position, result);
                    // Check that this view hasn't been reused for
                    // another page since we started
                    if (pageView.getPage() == position)
                        pageView.setPage(position, result);
                }
            };

            sizingTask.execute((Void)null);
        }
        return pageView;
    }

}
