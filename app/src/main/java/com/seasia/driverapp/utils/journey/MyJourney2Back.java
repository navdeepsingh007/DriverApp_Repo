package com.seasia.driverapp.utils.journey;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.seasia.driverapp.R;
import com.seasia.driverapp.application.MyApplication;

/**
 * Points calculated using - http://blogs.sitepointstatic.com/examples/tech/canvas-curves/bezier-curve.html
 */

public class MyJourney2Back extends View {

    public Paint paint;
    public Path path;

    private boolean coloredPath = false;
    private boolean currentPath = false;

    public MyJourney2Back(Context context) {
        super(context);
        init();
    }

    public MyJourney2Back(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public MyJourney2Back(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        path = new Path();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(10);

        // Right align path
        path.moveTo(384, 0);
//        path.cubicTo(389, 269, 301, 272, 300, 458);
        path.cubicTo(380, 268, 479, 294, 482, 493);

        DashPathEffect dashPath = new DashPathEffect(new float[]{20, 20}, (float) 5.0);
        paint.setPathEffect(dashPath);

        if (coloredPath) {
            paint.setColor(Color.GREEN);
        }
        if (currentPath) {
            paint.setColor(MyApplication.instance.getResources().getColor(R.color.colorOrange));
        }
        canvas.drawPath(path, paint);
    }

    public void drawColoredPath(boolean status) {
        coloredPath = status;
    }

    public void drawDriverCurrentPath(boolean status) {
        currentPath = status;
    }
}
