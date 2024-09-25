package com.dexter9ja.android.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class TrapezoidView extends RelativeLayout {


    public TrapezoidView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void draw(Canvas canvas) {
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(getWidth(), 0);
        path.lineTo(0, getHeight() - (getHeight() / 2));
        path.lineTo(getHeight() / 4, getHeight()); // toggle with line beneath
        //path.lineTo(0, getHeight());
        path.lineTo(getWidth(), getHeight());
        path.lineTo(getWidth(), 0);
        canvas.save();
        canvas.clipPath(path);
        super.draw(canvas);
        canvas.restore();
    }
}
