package com.seasia.driverapp.utils.journey;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class AnimationView extends View {

    List<AnimationThing> animationThingsList;
    public AnimationView(Context context) {
        super(context);
        initAnimationView();
    }

    public AnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAnimationView();
    }

    public AnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnimationView();
    }

    private void initAnimationView(){
        animationThingsList = new ArrayList<>();
    }

    public void insertThing(AnimationThing thing){
        animationThingsList.add(thing);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (AnimationThing thing : animationThingsList){

            //!!! Only the path of the last thing will be drawn on screen
            canvas.drawPath(thing.animPath, thing.paint);

            if(thing.distance < thing.pathLength){
                thing.pathMeasure.getPosTan(thing.distance, thing.pos, thing.tan);

                thing.matrix.reset();
                float degrees = (float)(Math.atan2(thing.tan[1], thing.tan[0])*180.0/Math.PI);
                thing.matrix.postRotate(degrees, thing.bm_offsetX, thing.bm_offsetY);
                thing.matrix.postTranslate(thing.pos[0]-thing.bm_offsetX, thing.pos[1]-thing.bm_offsetY);

                canvas.drawBitmap(thing.bm, thing.matrix, null);

                thing.distance += thing.step;
            }else{
                thing.distance = 0;
            }
        }

        invalidate();
    }
}
