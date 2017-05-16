package io.yoky.bargraph;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class BarGraph {
    private Context mContext;
    private LinearLayout mBarCon, mBottomCon;
    private Random random;
    private int mTotalBars, mLeftMargin, mRightMargin;
    private BarGraphTouchListener mBarGraphOnTouchListener;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            float blockWidth = view.getWidth() / mTotalBars, xPointer = motionEvent.getRawX(), hLeft = view.getLeft();
            int blockIndex = Math.round((xPointer - hLeft - (mLeftMargin+mRightMargin)) / blockWidth) + 2;
            View child = ((LinearLayout) view).getChildAt((blockIndex >= mTotalBars)? mTotalBars-1 : blockIndex >= 0 ? blockIndex: 0);
            if (child == null) return false;
            boolean isHovered;
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE: {
                    isHovered = true;
                }
                break;
                case MotionEvent.ACTION_UP: {
                    isHovered = false;
                }
                break;
                default:
                    isHovered = false;
            }
            disableClipOnParents(child,false);
            for (int i = 0; i < mTotalBars; i++) {
                RectangleBar ch = (RectangleBar) mBottomCon.getChildAt(i);
                RectangleBar bar = (RectangleBar) mBarCon.getChildAt(i);
                if (i == blockIndex) {
                    bar.isHovered = isHovered;
                    bar.invalidate();
                    ch.isHovered = isHovered;
                    ch.invalidate();
                    continue;
                }
                if (ch.isHovered) {
                    ch.isHovered = false;
                    ch.invalidate();
                }
                if (bar.isHovered) {
                    bar.isHovered = false;
                    bar.invalidate();
                }
            }

            mBarGraphOnTouchListener.onTouch(isHovered, blockIndex);
            return true;
        }
    };

    public BarGraph(Context ctx, LinearLayout barCon, LinearLayout bottomCon) {
        mContext = ctx;
        mBarCon = barCon;
        mBottomCon = bottomCon;
        random = new Random();
    }

    public void addGraph(ArrayList<Integer> dataList,int leftMargin, int rightMargin, BarGraphTouchListener bargraphTouchListener, int animationDuration) {
        if(dataList==null||dataList.size()==0)return;
        mTotalBars = dataList.size();
        mLeftMargin = leftMargin;
        mRightMargin = rightMargin;
        mBarCon.setWeightSum(mTotalBars);
        mBottomCon.setWeightSum(mTotalBars);

        for (int i = 0; i < mTotalBars; i++) {
            RectangleBar view = new RectangleBar(mContext,0);
            int height = Math.round((500*dataList.get(i))/1440);
            int hr = Math.round(dataList.get(i)/60);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, height);
            lp.setMargins(0, 0, 0, 0);
            lp.weight = 1f;
            lp.gravity = Gravity.BOTTOM;
            view.setLayoutParams(lp);
            view.setId(random.nextInt(1000000) + 1);
            view.pos = i >= 3 && i <= mTotalBars-4 ? 0 : i < 3 ? 1 : 2;
            view.textString1 = "" + hr + (hr > 1 ? " Hrs" : " Hr");
            view.textString2 = new SimpleDateFormat("dd/MM", Locale.ENGLISH).format(new Date());
            mBarCon.addView(view);
            mBarCon.invalidate();

            RectangleBar view2 = new RectangleBar(mContext,1);
            lp = new LinearLayout.LayoutParams(0, 20);
            lp.setMargins(5, 0, 5, 0);
            lp.weight = 1f;
            lp.gravity = Gravity.CENTER;
            view2.setLayoutParams(lp);
            view2.setId(random.nextInt(1000000));
            mBottomCon.addView(view2);
            mBottomCon.invalidate();
        }

        if(animationDuration>0)animateGraph(animationDuration);
        mBarGraphOnTouchListener = bargraphTouchListener;
        mBottomCon.setOnTouchListener(onTouchListener);
        mBarCon.setOnTouchListener(onTouchListener);
    }

    public void animateGraph(int duration) {
        for(int i=0;i<mTotalBars;i++){
            ((RectangleBar) mBarCon.getChildAt(i)).showView(duration);
        }
    }

    private void disableClipOnParents(View v, boolean disable) {
        if (v.getParent() == null) {
            return;
        }

        if (v instanceof ViewGroup) {
            ((ViewGroup) v).setClipChildren(disable);
        }

        if (v.getParent() instanceof View) {
            disableClipOnParents((View) v.getParent(), disable);
        }
    }
}
