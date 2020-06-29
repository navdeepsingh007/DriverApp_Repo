package com.seasia.driverapp.utils.journey;

//public class MyJourney {
//}

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

import com.seasia.driverapp.R;
import com.seasia.driverapp.application.MyApplication;

public class MyJourney1 extends View {

    public Paint paint;
    public Path path;
    public Canvas mCanvas;
    private Animation animation;

    private boolean coloredPath = false;
    private boolean currentPath = false;

    public MyJourney1(Context context) {
        super(context);
        init();
    }

    public MyJourney1(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public MyJourney1(Context context, AttributeSet attrs, int defStyle) {
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

        Log.d("MyJourney1", "==========> onDraw CALLED (1)");

        mCanvas = canvas;

        path = new Path();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(10);

        // Canvas paint center align
//        int xPos = (canvas.getWidth() / 2);
//        int yPos  = (canvas.getHeight() / 2);

//        paint.setTextAlign(Paint.Align.CENTER);

//        path.moveTo(45, 11);
//        path.cubicTo(18, 178, 266, 380, 265, 484);

        path.moveTo(172, 99);
        path.cubicTo(209, 313, 405, 315, 403, 482);

        DashPathEffect dashPath = new DashPathEffect(new float[]{20, 20}, (float) 5.0);
        paint.setPathEffect(dashPath);

        if (coloredPath) {
            paint.setColor(Color.GREEN);
        }
        if (currentPath) {
            paint.setColor(MyApplication.instance.getResources().getColor(R.color.colorOrange));
        }
//        if (animation == null)

        // Set animation for current path
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                path.cubicTo(18, 178, 266, 380, 265, 484);
//                ObjectAnimator animator = ObjectAnimator.ofFloat(this, View.X, View.Y, path);
//                animator.setDuration(5000);
//                animator.start();
        }


//            initAnimation();
//            scaleView(45, 11);
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

    private void initAnimation() {
//        android.view.animation.
//        animation = AnimationUtils.loadAnimation(MyApplication.instance,
//                android.R.interpolator.accelerate_cubic);
//                android.R.interpolator.linear_out_slow_in);

//        animation = new RotateAnimation(0, 360, 150, 150);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        animation.setDuration(7500L);
        animation.setInterpolator(new AccelerateInterpolator());
        startAnimation(animation);
    }

/*    public void scaleView(float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                1f, 1f, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(5000);
        startAnimation(anim);
    }*/
}
