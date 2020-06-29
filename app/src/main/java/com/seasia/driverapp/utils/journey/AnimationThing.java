package com.seasia.driverapp.utils.journey;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;

public class AnimationThing {
    Paint paint;
    Path animPath;
    PathMeasure pathMeasure;
    float pathLength;
    Bitmap bm;
    int bm_offsetX, bm_offsetY;
    float step;
    float distance;
    float[] pos;
    float[] tan;
    Matrix matrix;

    public AnimationThing(Paint paint,
                          Path animPath,
                          Bitmap bm,
                          float step) {
        this.paint = paint;

        this.animPath = animPath;
        pathMeasure = new PathMeasure(this.animPath, false);
        pathLength = pathMeasure.getLength();

        this.bm = bm;
        bm_offsetX = bm.getWidth()/2;
        bm_offsetY = bm.getHeight()/2;

        this.step = step;
        distance = 0;
        pos = new float[2];
        tan = new float[2];

        matrix = new Matrix();
    }
}
