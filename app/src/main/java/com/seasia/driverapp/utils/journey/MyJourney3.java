package com.seasia.driverapp.utils.journey;

//public class MyJourney {
//}

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

import com.seasia.driverapp.R;
import com.seasia.driverapp.application.MyApplication;

public class MyJourney3 extends View {

    Paint paint;
    Path path;

    private boolean coloredPath = false;
    private boolean currentPath = false;

    public MyJourney3(Context context) {
        super(context);
        init();
    }

    public MyJourney3(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyJourney3(Context context, AttributeSet attrs, int defStyle) {
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

//        path.moveTo(24, 23);
//        path.cubicTo(33, 347, 327, 168, 432, 489);
//        path.moveTo(174, 92);
//        path.cubicTo(166, 297, 477, 238, 484, 491);

//        path.moveTo(182, 109);
//        path.cubicTo(194, 214, 337, 151, 371, 387);

//        path.moveTo(145, 87);
//        path.cubicTo(133, 383, 449, 196, 407, 504);

        //        path.moveTo(145, 87);
//        path.cubicTo(133, 383, 449, 196, 450, 504);


//        path.moveTo(145, 87);
//        path.cubicTo(133, 383, 449, 196, 360, 504);
        path.moveTo(165, 87);
        path.cubicTo(133, 383, 449, 196, 375, 504);

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
//            whitePath.moveTo(165, 87);
//            whitePath.cubicTo(133, 383, 449, 196, 375, 504);
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


/*    public void PathInterpolatorGingerbread(Path path) {
        final PathMeasure pathMeasure = new PathMeasure(path, false *//* forceClosed *//*);

        final float pathLength = pathMeasure.getLength();
        final int numPoints = (int) (pathLength / PRECISION) + 1;

        mX = new float[numPoints];
        mY = new float[numPoints];

        final float[] position = new float[2];
        for (int i = 0; i < numPoints; ++i) {
            final float distance = (i * pathLength) / (numPoints - 1);
            pathMeasure.getPosTan(distance, position, null *//* tangent *//*);

            mX[i] = position[0];
            mY[i] = position[1];
        }
    }*/
}
