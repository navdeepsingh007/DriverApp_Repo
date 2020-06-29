package com.seasia.driverapp.utils.journey;

//public class MyJourney {
//}

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.seasia.driverapp.R;
import com.seasia.driverapp.application.MyApplication;

public class MyJourney4 extends View {

    Paint paint;
    Path path;

    private boolean coloredPath = false;
    private boolean currentPath = false;

    public MyJourney4(Context context) {
        super(context);
        init();
    }

    public MyJourney4(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public MyJourney4(Context context, AttributeSet attrs, int defStyle) {
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

        // Canvas paint center align
//        int xPos = (canvas.getWidth() / 2);
//        int yPos  = (canvas.getHeight() / 2);

//        paint.setTextAlign(Paint.Align.CENTER);

//        path.moveTo(40, 492);
//        path.cubicTo(68, 246, 421, 256, 449, 8);
        path.moveTo(180, 493);
        path.cubicTo(157, 251, 441, 336, 499, 86);

        DashPathEffect dashPath = new DashPathEffect(new float[]{20, 20}, (float) 5.0);
        paint.setPathEffect(dashPath);

        if (coloredPath) {
            paint.setColor(Color.GREEN);
        }
        if (currentPath) {
            paint.setColor(MyApplication.instance.getResources().getColor(R.color.colorOrange));
        }
        canvas.drawPath(path, paint);

//        path.reset();
//        paint.setColor(Color.GRAY);
//        paint.setStrokeWidth(2);
//
//        canvas.drawPath(path, paint);
    }

    public void drawColoredPath(boolean status) {
        coloredPath = status;
    }

    public void drawDriverCurrentPath(boolean status) {
        currentPath = status;
    }
}
