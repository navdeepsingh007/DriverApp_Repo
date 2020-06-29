package com.seasia.driverapp.utils.journey;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.PathInterpolator;

import com.seasia.driverapp.R;
import com.seasia.driverapp.application.MyApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Points calculated using - http://blogs.sitepointstatic.com/examples/tech/canvas-curves/bezier-curve.html
 */

public class MyJourney2 extends View {

    public Paint paint;
    public Path path;

    private boolean coloredPath = false;
    private boolean currentPath = false;

    public MyJourney2(Context context) {
        super(context);
        init();
    }

    public MyJourney2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public MyJourney2(Context context, AttributeSet attrs, int defStyle) {
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

//        path.moveTo(446, 14);
//        path.cubicTo(451, 357, 168, 107, 50, 485);
        // For motorcycle icon size - 30dp
//        path.moveTo(169, 471);
//        path.cubicTo(199, 234, 403, 354, 489, 95);
        // For motorcycle icon size - 40dp
//        path.moveTo(169, 475);
//        path.cubicTo(179, 313, 441, 370, 481, 130);

//        path.moveTo(169, 486);
//        path.cubicTo(181, 233, 409, 370, 378, 91);

//        path.moveTo(170, 550);
//        path.cubicTo(156, 189, 394, 360, 410, -8);

//        path.moveTo(170, 550);
//        path.cubicTo(156, 189, 394, 360, 463, -8);

        // Left align path
//        path.moveTo(170, 550);
//        path.cubicTo(156, 189, 394, 360, 373, -8);

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

            // Line below line
//            Path whitePath = new Path();
//            paint.setColor(Color.WHITE);
//            paint.setStrokeWidth(10);
//            DashPathEffect dashPath2 = new DashPathEffect(new float[]{20, 20}, (float) 5.0);
//            paint.setPathEffect(dashPath2);
//            path.moveTo(384, 0);
//            path.cubicTo(380, 268, 479, 294, 482, 493);
//            canvas.drawPath(whitePath, paint);
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
