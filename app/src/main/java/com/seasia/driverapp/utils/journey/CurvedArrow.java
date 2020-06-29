package com.seasia.driverapp.utils.journey;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.core.content.ContextCompat;

public class CurvedArrow {

    public CurvedArrow drawCurvedArrow(Context context, int x1, int y1, int x2, int y2, int curveRadius,
                                       int color, int lineWidth) {

        Paint paint  = new Paint();
        Canvas canvas = new Canvas();

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(ContextCompat.getColor(context, color));

        final Path path = new Path();
        int midX            = x1 + ((x2 - x1) / 2);
        int midY            = y1 + ((y2 - y1) / 2);
        float xDiff         = midX - x1;
        float yDiff         = midY - y1;
        double angle        = (Math.atan2(yDiff, xDiff) * (180 / Math.PI)) - 90;
        double angleRadians = Math.toRadians(angle);
        float pointX        = (float) (midX + curveRadius * Math.cos(angleRadians));
        float pointY        = (float) (midY + curveRadius * Math.sin(angleRadians));

        path.moveTo(x1, y1);
        path.cubicTo(x1,y1,pointX, pointY, x2, y2);
        canvas.drawPath(path, paint);

        return this;
    }
}
