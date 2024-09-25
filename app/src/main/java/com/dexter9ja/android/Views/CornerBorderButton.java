package com.dexter9ja.android.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.dexter9ja.android.R;

public class CornerBorderButton extends androidx.appcompat.widget.AppCompatButton {

    Path mPath;
    float borderRadius;

    public CornerBorderButton(Context context) {
        super(context);
    }

    public CornerBorderButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
    }

    public CornerBorderButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttributes(attrs);
    }

    private void setAttributes(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CornerBorderLinearLayout);
        borderRadius = typedArray.getInt(R.styleable.CornerBorderLinearLayout_borderRadius, 0);
        typedArray.recycle();
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.clipPath(mPath);
        super.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(borderRadius < 0)
            borderRadius = (float) w / 2;
        RectF r = new RectF(0, 0, w, h);
        mPath = new Path();
        mPath.addRoundRect(r, borderRadius, borderRadius, Path.Direction.CW);
        mPath.close();
    }
}
