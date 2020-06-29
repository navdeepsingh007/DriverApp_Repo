package com.seasia.driverapp.utils.journey;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

public class MyJouneyAnimation extends View {
    private static final String TAG = "MyJouneyAnimation";

    int framesPerSecond = 60;
    long animationDuration = 10000; // 10 seconds

    Matrix matrix = new Matrix(); // transformation matrix

    Path path;       // your path
    Paint paint;    // your paint

    long startTime;

    public MyJouneyAnimation(Context context, Path path, Paint paint) {
        super(context);

        this.path = path;
        this.paint = paint;

        // start the animation:
        this.startTime = System.currentTimeMillis();
        this.postInvalidate();

        setWillNotDraw(false);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "==========> onDraw CALLED (ANIM)");

        long elapsedTime = System.currentTimeMillis() - startTime;

        matrix.postRotate(30 * elapsedTime / 1000);        // rotate 30° every second
        matrix.postTranslate(100 * elapsedTime / 1000, 0); // move 100 pixels to the right
        // other transformations...

        canvas.concat(matrix);        // call this before drawing on the canvas!!

        canvas.drawPath(path, paint); // draw on canvas

        if (elapsedTime < animationDuration)
            this.postInvalidateDelayed(1000 / framesPerSecond);
    }
}
