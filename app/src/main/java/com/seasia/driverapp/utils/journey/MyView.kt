package com.seasia.driverapp.utils.journey

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class MyView : View {
    var paint: Paint? = null
    var path: Path? = null

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

    private fun init() {
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
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path!!, paint!!)
    }
}