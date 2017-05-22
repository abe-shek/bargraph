package io.yoky.graphtest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;

public class YTextView extends android.support.v7.widget.AppCompatTextView {
    public float yKern = 1;
    public int ybgType = 0;
    public String yFont = "league";
    private CharSequence originalText;
    int w, h;
    Paint p;

    public YTextView(Context context) {
        super(context);
        setTypeface(G.getTypeface(context, "gill"));
    }

    public YTextView(Context context, String yfont, int ybgType) {
        super(context);
        setTypeface(G.getTypeface(context, yfont));
        this.ybgType = ybgType;
        init();
    }

    public YTextView(Context context, String yfont, int ykern, int ybgType) {
        super(context);
        setTypeface(G.getTypeface(context, yfont));
        this.ybgType = ybgType;
        this.yKern = ykern;
        init();
    }

    public YTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.YTextView, 0, 0);
        try {
            yKern = a.getFloat(R.styleable.YTextView_ykern, 1);
            yFont = a.getString(R.styleable.YTextView_yfont);
            setTypeface(G.getTypeface(context, yFont));
            ybgType = a.getInt(R.styleable.YTextView_ybgtype, 0);
        } finally {
            a.recycle();
        }
        init();
        setText(originalText);
    }

    public YTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.YTextView, 0, 0);
        try {
            yFont = a.getString(0);
            setTypeface(G.getTypeface(context, yFont));
            yKern = a.getFloat(R.styleable.YTextView_ykern, 1);
            ybgType = a.getInt(R.styleable.YTextView_ybgtype, 0);
        } finally {
            a.recycle();
        }
        init();
        setText(originalText);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        originalText = text;
        applySpacing();
    }

    @Override
    public CharSequence getText() {
        return originalText;
    }

    private void applySpacing() {
        if (this == null || this.originalText == null) return;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < originalText.length(); i++) {
            builder.append(originalText.charAt(i));
            if (i + 1 < originalText.length() && yKern > 0) {
                builder.append("\u00A0");
            }
        }
        SpannableString finalText = new SpannableString(builder.toString());
        if (builder.toString().length() > 1) {
            for (int i = 1; i < builder.toString().length(); i += 2) {
                finalText.setSpan(new ScaleXSpan((yKern + 1) / 10), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        super.setText(finalText, BufferType.SPANNABLE);
    }

    private void init() {
        if (ybgType == 1 || ybgType == 4) {
            p = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
            p.setStrokeWidth(1.5f);
            p.setColor(ybgType == 1 ? 0xFFFFFFFF : 0xFF000000);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
        } else if (ybgType == 2 || ybgType == 3 || ybgType == 5 || ybgType == 6) {
            p = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
            p.setStrokeWidth(1.5f);
            p.setColor(ybgType == 5 ? 0xFF000000 : ybgType == 6 ? 0XFFE97DFF : 0xFFFFFFFF);
            p.setStyle(Paint.Style.STROKE);
        }
        requestLayout();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas c) {
        if (w == 0 || h == 0) return;
        super.onDraw(c);
        if (ybgType == 1 || ybgType == 4) {
            c.drawLine(6, h - 6, w - 6, h - 6, p);
            c.drawCircle(6, h - 6, 6, p);
            c.drawCircle(w - 6, h - 6, 6, p);
        } else if (ybgType == 2 || ybgType == 5 || ybgType == 6) {
            c.drawLine(2, h - 6, w - 2, h - 6, p);
        } else if (ybgType == 3) {
            c.drawLine(2, 2, w - 2, 2, p);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
    }
}
