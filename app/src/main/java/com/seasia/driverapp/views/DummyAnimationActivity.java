package com.seasia.driverapp.views;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;

import com.seasia.driverapp.R;
import com.seasia.driverapp.utils.journey.AnimationThing;
import com.seasia.driverapp.utils.journey.AnimationView;

public class DummyAnimationActivity extends AppCompatActivity {

    AnimationView myAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dummy_animation);
        myAnimationView = (AnimationView)findViewById(R.id.MyAnimationView);

        prepareThings();
    }

    private void prepareThings(){
        Paint paint;
        Path animPath;
        float step;
        Bitmap bm;

        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);

        bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        animPath = new Path();
        animPath.moveTo(100, 100);
        animPath.lineTo(200, 100);
        animPath.lineTo(300, 50);
        animPath.lineTo(400, 150);
        animPath.lineTo(100, 300);
        animPath.lineTo(600, 300);
        animPath.lineTo(100, 100);
        animPath.close();

        step = 1;

        AnimationThing thing = new AnimationThing(paint, animPath, bm, step);
        myAnimationView.insertThing(thing);

        //The second thing
        bm = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_add);

        animPath.reset();
        animPath.addCircle(400, 400, 300, Path.Direction.CW);
        step = 3;
        thing = new AnimationThing(paint, animPath, bm, step);
        myAnimationView.insertThing(thing);
    }
}
