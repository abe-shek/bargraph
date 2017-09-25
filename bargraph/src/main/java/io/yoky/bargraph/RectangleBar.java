package io.yoky.bargraph;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RectangleBar extends View {

    int w, h;
    Paint bgPaint, lPaint, tPaint;
    RectF rectFThin, rectFThick;
    float r;
    int minH = 600, mode = 0;
    Point pt;
    public int pos = -1;
    boolean isHovered = false, animate = false;
    public String textString1 = "", textString2 = "";
    Shader gradientShader;
    Path pathMode0, pathMode1, pathMode2;

    public RectangleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RectangleBar(Context ctx, int m) {
        super(ctx);
        mode = m;
        init();
    }

    private void init() {
        if (mode == 0) {
            bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bgPaint.setStyle(Paint.Style.FILL);
            lPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            lPaint.setColor(0x6C000000);
            lPaint.setStyle(Paint.Style.FILL);
            tPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            tPaint.setColor(0x9CFFFFFF);
            tPaint.setStyle(Paint.Style.FILL);
        } else if(mode == 1){
            bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            bgPaint.setColor(0xCCFFFFFF);
            bgPaint.setStyle(Paint.Style.FILL);
            setClickable(false);
            requestLayout();
            invalidate();
        }
    }


    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        if(mode == 0) {
            if (animate) {
                int h = Math.round(this.h * r);
                rectFThin = new RectF(w / 5, 0, 4 * w / 5, h);
                setRotation(180);
                bgPaint.setShader(new LinearGradient(0, 0, 0, h, Color.rgb(232, 125, 119), Color.rgb(242, 196, 159), Shader.TileMode.MIRROR));
            } else {
                setRotation(0);
                bgPaint.setShader(gradientShader);
            }
            c.drawRoundRect(isHovered ? rectFThick : rectFThin, 2, 2, bgPaint);
            if (isHovered) {
                if (pos == 0) {
                    c.drawLine(pt.x, pt.y, pt.x, 0, lPaint);
                    c.drawPath(pathMode0, lPaint);
                    tPaint.setTextSize(40);
                    tPaint.setColor(Color.rgb(220, 190, 30));
                    c.drawText(textString1, pt.x - 42, pt.y - 100, tPaint);
                    tPaint.setTextSize(30);
                    tPaint.setColor(Color.rgb(200, 200, 30));
                    c.drawText(textString2, pt.x - 32, pt.y - 60, tPaint);
                } else if (pos == 1) {
                    c.drawLine(pt.x, pt.y, pt.x, 0, lPaint);
                    c.drawPath(pathMode1, lPaint);
                    tPaint.setTextSize(40);
                    tPaint.setColor(Color.rgb(220, 190, 30));
                    c.drawText(textString1, pt.x + 40, pt.y - 100, tPaint);
                    tPaint.setTextSize(30);
                    tPaint.setColor(Color.rgb(200, 200, 30));
                    c.drawText(textString2, pt.x + 40, pt.y - 60, tPaint);
                } else if (pos == 2) {
                    c.drawLine(pt.x, pt.y, pt.x, 0, lPaint);
                    c.drawPath(pathMode2, lPaint);
                    tPaint.setTextSize(40);
                    tPaint.setColor(Color.rgb(220, 190, 30));
                    c.drawText(textString1, pt.x - 160, pt.y - 100, tPaint);
                    tPaint.setTextSize(30);
                    tPaint.setColor(Color.rgb(200, 200, 30));
                    c.drawText(textString2, pt.x - 160, pt.y - 60, tPaint);
                }
            }
        }else if(mode == 1){
            c.drawRoundRect(isHovered?rectFThick:rectFThin, 2, 2, bgPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
        initViews();
    }

    private void initViews() {
        if(mode==0) {
            if (rectFThick == null) rectFThick = new RectF(0, 0, w, h);
            if (rectFThin == null) rectFThin = new RectF(w / 5, 0, (4 * w) / 5, h);
            if (gradientShader == null)
                gradientShader = new LinearGradient(0, 0, 0, h, Color.rgb(242, 196, 159), Color.rgb(232, 125, 119), Shader.TileMode.MIRROR);
            if (pt == null) pt = new Point(w / 2, h - minH);
            if (pos == 0 && pathMode0 == null) {
                pathMode0 = new Path();
                pathMode0.moveTo(pt.x, pt.y - 40);
                pathMode0.arcTo(new RectF(pt.x - 100, pt.y - 40, pt.x, pt.y), 0, -90);
                pathMode0.moveTo(pt.x - 100, pt.y - 40);
                pathMode0.lineTo(pt.x - 100, pt.y - 140);
                pathMode0.lineTo(pt.x + 100, pt.y - 140);
                pathMode0.lineTo(pt.x + 100, pt.y - 40);
                pathMode0.moveTo(pt.x, pt.y - 40);
                pathMode0.arcTo(new RectF(pt.x, pt.y - 40, pt.x + 100, pt.y), 180, 90);
            } else if (pos == 1 && pathMode1 == null) {
                pathMode1 = new Path();
                pathMode1.moveTo(pt.x, pt.y - 40);
                pathMode1.arcTo(new RectF(pt.x, pt.y - 40, pt.x + 200, pt.y), 180, 90);
                pathMode1.lineTo(pt.x + 200, pt.y - 40);
                pathMode1.lineTo(pt.x + 200, pt.y - 140);
                pathMode1.lineTo(pt.x, pt.y - 140);
                pathMode1.lineTo(pt.x, pt.y);
            } else if (pos == 2 && pathMode2 == null) {
                pathMode2 = new Path();
                pathMode2.moveTo(pt.x, pt.y - 40);
                pathMode2.arcTo(new RectF(pt.x - 200, pt.y - 40, pt.x, pt.y), 0, -90);
                pathMode2.lineTo(pt.x - 200, pt.y - 40);
                pathMode2.lineTo(pt.x - 200, pt.y - 140);
                pathMode2.lineTo(pt.x, pt.y - 140);
                pathMode2.lineTo(pt.x, pt.y);
            }
        }else if(mode == 1){
            if(rectFThick==null) rectFThick = new RectF(0, 0, w, h);
            if(rectFThin==null) rectFThin = new RectF(w/3, 0, (2*w)/3, h);
        }
    }

    public void showView(int duration) {
        if (animate || mode !=0) {
            Log.e("rectangleBar", "showView: " + animate + " " + mode );
            return;
        }
        animate = true;
        ValueAnimator am = ValueAnimator.ofFloat(0f, 1f);
        am.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                r = (float) animation.getAnimatedValue();
                invalidate();
                requestLayout();
                if (r == 1f) animate = false;
            }
        });
        am.setDuration(duration);
        am.start();
    }

}
