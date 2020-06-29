package com.seasia.driverapp.utils.journey

import android.content.Context
import android.graphics.*
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.View


class MyView2 : View {
    var paint: Paint? = null
    var path: Path? = null

    var missileDrawPath: Path? = null
    var missilePaint: Paint?= null


    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
        init()
    }

   private fun init() {/*
//        missilePath = Path()
        val missileDrawPathLocal = Path()
        val mPaint = Paint()

        mPaint.setStrokeWidth(3f)
        mPaint.setColor(Color.BLACK)
        mPaint.setStyle(Paint.Style.STROKE)
        mPaint.setAntiAlias(true)
        val dashEffect: PathEffect = DashPathEffect(floatArrayOf(2f, 4f), 0f)
        mPaint.setPathEffect(dashEffect)
        val DRAW_TIME: Long = 5000
        val timer = object : CountDownTimer(DRAW_TIME, 100) {
            var measure = PathMeasure()
            var segmentPath = Path()
            var start = 0f
            override fun onTick(millisUntilFinished: Long) {
                measure.setPath(missilePath, false)
                val percent =
                    (DRAW_TIME - millisUntilFinished).toFloat() / DRAW_TIME.toFloat()
                val length = measure.length * percent
                measure.getSegment(start, length, segmentPath, true)
                start = length
                missileDrawPathLocal.addPath(segmentPath)
                invalidate()
            }

            override fun onFinish() {
                measure.getSegment(start, measure.length, segmentPath, true)
                missileDrawPathLocal.addPath(segmentPath)
                invalidate()
            }
        }
        timer.start()

        missilePaint = mPaint
        missileDrawPath = missileDrawPathLocal
    */}


/*    private fun init() {
        paint = Paint()
        paint!!.color = Color.BLUE
        paint!!.strokeWidth = 10f
        paint!!.style = Paint.Style.STROKE
        path = Path()
//        path!!.moveTo(50f, 50f)
//        path!!.lineTo(50f, 500f)
//        path!!.lineTo(200f, 500f)
//        path!!.lineTo(200f, 300f)
//        path!!.lineTo(350f, 300f)

        path!!.moveTo(50f, 50f)
        path!!.lineTo(55f, 500f)
        path!!.lineTo(180f, 480f)
        path!!.lineTo(200f, 320f)
        path!!.lineTo(320f, 288f)

        val intervals = floatArrayOf(50.0f, 20.0f)
        val phase = 0f
        val dashPathEffect = DashPathEffect(intervals, phase)
        paint!!.pathEffect = dashPathEffect
    }*/

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        canvas.drawPath(path!!, paint!!)
        canvas.drawPath(missileDrawPath!!, missilePaint!!)
    }
}