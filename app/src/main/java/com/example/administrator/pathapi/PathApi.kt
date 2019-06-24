package com.example.administrator.pathapi

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.view.View

class PathApi : View {
    private var mPaint: Paint? = null
    private var mPath = Path()

    constructor(context: Context) : super(context) {
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint?.color = Color.RED
        mPaint?.strokeWidth = 4f
        mPaint?.style = Paint.Style.STROKE
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        mPaint?.style=Paint.Style.FILL

        //一阶贝塞尔曲线 ,表示是一条直线
//        mPath.moveTo(100f,70f)//移动
////        mPath.lineTo(140f,800f)//连线
//        //效果等同于 mPath.lineTo(140f,800f) 这行代码
//        mPath.rLineTo(40f,730f)
//        mPath.lineTo(250f,500f)
////        mPath.close()//设置曲线是否闭合
//        canvas.drawPath(mPath,mPaint)

        //添加addXXX
//        /**
//         * 添加圆弧，left、top、right、bottom组成圆弧矩形区域，startAngle：起始角度，sweepAngle：圆弧旋转的角度。ps：此方法在API 19以上有效
//         */
//        mPath.addArc(200f,200f,400f,400f,-255f,255f)
//        /**
//         * 添加矩形，left、top、right、bottom组成矩形区域，dir：线的闭合方向（CW顺时针方向 | CCW逆时针方向）
//         */
//        mPath.addRect(300f,300f,100f,100f,Path.Direction.CW)
//        //添加一个圆
//        mPath.addCircle(200f,200f,100f,Path.Direction.CCW)
//
//        //添加一个椭圆
//        mPath.addOval(0f,0f,200f,300f,Path.Direction.CCW)

        //追加图形
        //xxxTo划线
        /**
         * 绘制圆弧，left、top、right、bottom组成圆弧矩形区域，startAngle：起始角度，sweepAngle：圆弧旋转的角度，forceMoveTo：是否在绘制圆弧前移动（moveTo）path画笔位置
         */
//        mPath.arcTo(400f,200f,600f,400f,-180f,225f,false)

        //forceMoveTo，true，绘制时移动起点，false，绘制时连接最后一个点与圆弧起点
//        mPath.moveTo(0f,0f)
//        mPath.lineTo(100f,100f)
//        mPath.arcTo(400f,200f,600f,400f,0f,270f,true)

        //添加一个路径

//        mPath.moveTo(100f, 70f)
//        mPath.lineTo(140f, 180f)
//        mPath.lineTo(250f, 330f)
//        mPath.lineTo(400f, 630f)
//        mPath.lineTo(100f, 830f)
//        var newPath =  Path()
//        newPath.moveTo(100f, 700f)
//        newPath.lineTo(600f, 800f)
//        newPath.lineTo(400f, 1000f)
//        mPath.addPath(newPath)


//        //添加圆角矩形， CW顺时针，CCW逆时针
//        var rectF5 = RectF(200f, 600f, 700f, 800f)
//        mPath.addRoundRect(rectF5, 20f, 20f, Path.Direction.CCW)



        //画二阶贝塞尔曲线
        mPath.moveTo(300f, 500f)
//        mPath.quadTo(500, 100, 800, 500);
        //参数表示相对位置，等同于上面一行代码
        mPath.rQuadTo(200f, -400f, 500f, 0f)

        //画三阶贝塞尔曲线
        mPath.moveTo(100f, 300f)
//        mPath.cubicTo(500, 100, 600, 1200, 800, 500);
        //参数表示相对位置，等同于上面一行代码
        mPath.rCubicTo(100f, -300f, 300f, 500f, 400f, 0f)

        canvas.drawPath(mPath, mPaint)

    }
}