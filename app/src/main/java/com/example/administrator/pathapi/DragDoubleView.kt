package com.example.administrator.pathapi

import android.content.Context
import android.graphics.*
import android.view.View
import android.util.AttributeSet
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Bitmap
import android.view.MotionEvent
import android.graphics.PointF
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.R.attr.y
import android.R.attr.x
import android.animation.*
import android.annotation.TargetApi
import android.os.Build
import android.util.Log


class DragDoubleView : View {
    /**
     * 气泡半径
     */
    private var mBubbleRadius: Float = 0.toFloat()
    /**
     * 气泡颜色
     */
    private var mBubbleColor: Int = 0

    /**
     * 气泡消息文字
     */
    private var mTextStr: String? = null

    /**
     * 气泡消息文字颜色
     */
    private var mTextColor: Int = 0
    /**
     * 气泡消息文字大小
     */
    private var mTextSize: Float = 0.toFloat()
    /**
     * 不动气泡的半径
     */
    private var mBubFixedRadius: Float = 0.toFloat()
    /**
     * 可动气泡的半径
     */
    private var mBubMovableRadius: Float = 0.toFloat()

    /**
     * 不动气泡的圆心
     */
    private var mBubFixedCenter: PointF? = null
    /**
     * 气泡相连状态最大圆心距离
     */
    private var mMaxDist: Float = 0.toFloat()

    /**
     * 手指触摸偏移量
     */
    private var MOVE_OFFSET: Float = 0.toFloat()
    /**
     * 气泡的画笔
     */
    private var mBubblePaint: Paint? = null
    /**
     * 贝塞尔曲线path
     */
    private var mBezierPath: Path? = null
    /**
     * 气泡文字画笔
     */
    private var mTextPaint: Paint? = null
    // 文本绘制区域
    private var mTextRect: Rect? = null
    /**
     * 爆炸画笔
     */
    private var mBurstPaint: Paint? = null

    // 爆炸 文本绘制区域
    private var mBurstRect: Rect? = null
    /**
     * 气泡爆炸的图片id数组
     */
    private var mBurstDrawablesArray = intArrayOf(R.mipmap.burst_1, R.mipmap.burst_2, R.mipmap.burst_3, R.mipmap.burst_4, R.mipmap.burst_5)

    /**
     * 气泡爆炸的bitmap数组
     */
    var mBurstBitmapsArray: ArrayList<Bitmap>? = null
    /**
     * 气泡默认状态--静止
     */
    private val BUBBLE_STATE_DEFAULT = 0
    /**
     * 气泡相连
     */
    private val BUBBLE_STATE_CONNECT = 1
    /**
     * 气泡分离
     */
    private val BUBBLE_STATE_APART = 2

    /**
     * 气泡消失
     */
    private val BUBBLE_STATE_DISMISS = 3

    /**
     * 气泡状态标志
     */
    private var mBubbleState = BUBBLE_STATE_DEFAULT
    /**
     * 两气泡圆心距离
     */
    private var mDist: Float = 0.toFloat()


    /**
     * 可动气泡的圆心
     */
    private var mBubMovableCenter: PointF? = null
    /**
     * 当前气泡爆炸图片index
     */
    private var mCurDrawableIndex: Int = 0


    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {
        Log.e("tag","1111111111111")

    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.DragBubbleView, defStyleAttr, 0)
        mBubbleRadius = array.getDimension(R.styleable.DragBubbleView_bubble_radius, mBubbleRadius)
        mBubbleColor = array.getColor(R.styleable.DragBubbleView_bubble_color, Color.RED)
        mTextStr = array.getString(R.styleable.DragBubbleView_bubble_text)
        mTextSize = array.getDimension(R.styleable.DragBubbleView_bubble_textSize, mTextSize)
        mTextColor = array.getColor(R.styleable.DragBubbleView_bubble_textColor, Color.WHITE)
        array.recycle()
        //两个圆半径大小一致
        mBubFixedRadius = mBubbleRadius
        mBubMovableRadius = mBubFixedRadius
        mMaxDist = 8 * mBubbleRadius

        MOVE_OFFSET = mMaxDist / 4

        //抗锯齿
        mBubblePaint = Paint(ANTI_ALIAS_FLAG)
        mBubblePaint?.color = mBubbleColor
        mBubblePaint?.style = Paint.Style.FILL
        mBezierPath = Path()

        //文本画笔
        mTextPaint = Paint(ANTI_ALIAS_FLAG)
        mTextPaint?.color = mTextColor
        mTextPaint?.textSize = mTextSize
        mTextRect = Rect()

        //爆炸画笔
        mBurstPaint = Paint(ANTI_ALIAS_FLAG)
        mBurstPaint?.isFilterBitmap = true
        mBurstRect = Rect()
        mBurstBitmapsArray = arrayListOf<Bitmap>()
        for (i in 0 until mBurstDrawablesArray.size) {
            //将气泡爆炸的drawable转为bitmap
            val bitmap = BitmapFactory.decodeResource(resources, mBurstDrawablesArray[i])
//            mBurstBitmapsArray!![i]=bitmap
            mBurstBitmapsArray?.add(bitmap)
        }


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //不动气泡圆心
        if (mBubFixedCenter == null) {
            mBubFixedCenter = PointF((w / 2).toFloat(), (h / 2).toFloat())
        } else {
            mBubFixedCenter!!.set((w / 2).toFloat(), (h / 2).toFloat())
        }

        //可动气泡圆心
        if (mBubMovableCenter == null) {
            mBubMovableCenter = PointF((w / 2).toFloat(), (h / 2).toFloat())
        } else {
            mBubMovableCenter!!.set((w / 2).toFloat(), (h / 2).toFloat())
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        if (mBubbleState == BUBBLE_STATE_CONNECT) {
            mBubFixedCenter?.y?.let { canvas.drawCircle(mBubFixedCenter?.x!!.toFloat(), it, mBubFixedRadius, mBubblePaint) }

            //绘制贝塞尔曲线
            //控制点坐标
            val iAnchorX = ((mBubFixedCenter?.x?.plus(mBubMovableCenter!!.x))?.div(2))?.toInt()
            val iAnchorY = ((mBubFixedCenter!!.y + mBubMovableCenter!!.y) / 2).toInt()

            val sinTheta = (mBubMovableCenter?.y?.minus(mBubFixedCenter!!.y))?.div(mDist)
            val cosTheta = (mBubMovableCenter?.x?.minus(mBubFixedCenter!!.x))?.div(mDist)

            //B
            val iBubMovableStartX = mBubMovableCenter!!.x + sinTheta!! * mBubMovableRadius
            val iBubMovableStartY = mBubMovableCenter!!.y - cosTheta!! * mBubMovableRadius

            //A
            val iBubFixedEndX = mBubFixedCenter!!.x + mBubFixedRadius * sinTheta
            val iBubFixedEndY = mBubFixedCenter!!.y - mBubFixedRadius * cosTheta

            //D
            val iBubFixedStartX = mBubFixedCenter!!.x - mBubFixedRadius * sinTheta
            val iBubFixedStartY = mBubFixedCenter!!.y + mBubFixedRadius * cosTheta
            //C
            val iBubMovableEndX = mBubMovableCenter!!.x - mBubMovableRadius * sinTheta
            val iBubMovableEndY = mBubMovableCenter!!.y + mBubMovableRadius * cosTheta

            mBezierPath?.reset()
            mBezierPath?.moveTo(iBubFixedStartX, iBubFixedStartY)
            mBezierPath?.quadTo(iAnchorX!!.toFloat(), iAnchorY.toFloat(), iBubMovableEndX, iBubMovableEndY)
            //移动到B点
            mBezierPath?.lineTo(iBubMovableStartX, iBubMovableStartY)
            mBezierPath?.quadTo(iAnchorX!!.toFloat(), iAnchorY.toFloat(), iBubFixedEndX, iBubFixedEndY)
            mBezierPath?.close()
            canvas.drawPath(mBezierPath, mBubblePaint)

        }




        if (mBubbleState != BUBBLE_STATE_DISMISS) {
            //绘制一个气泡加消息数据
            canvas.drawCircle(mBubMovableCenter!!.x, mBubMovableCenter!!.y, mBubMovableRadius, mBubblePaint)
            mTextPaint?.getTextBounds(mTextStr, 0, mTextStr?.length!!.toInt(), mTextRect)
            canvas.drawText(mTextStr, mBubMovableCenter!!.x - mTextRect?.width()!! / 2, mBubMovableCenter!!.y + mTextRect!!.height() / 2, mTextPaint)
        }

        if (mBubbleState == BUBBLE_STATE_DISMISS && mCurDrawableIndex < mBurstBitmapsArray!!.size) {
            mBurstRect?.set(
                    (mBubMovableCenter?.x?.minus(mBubMovableRadius))!!.toInt(),
                    (mBubMovableCenter?.y!! - mBubMovableRadius).toInt(),
                    (mBubMovableCenter!!.x + mBubMovableRadius).toInt(),
                    (mBubMovableCenter!!.y + mBubMovableRadius).toInt()
            )
            canvas.drawBitmap(mBurstBitmapsArray!![mCurDrawableIndex], null, mBurstRect, mBubblePaint)
        }

        //1.静止装态，一个小球加消息数据

        //2.连接状态，一个小球加消息数据 ，贝塞尔曲线，本身位置上小球，大小可变换


        //3. 分离状态，一个小球加消息数据

        //4.消失状态，爆炸效果
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (mBubbleState !== BUBBLE_STATE_DISMISS) {
                    mDist = Math.hypot((event.x - mBubFixedCenter!!.x).toDouble(), (event.y - mBubFixedCenter!!.y).toDouble()).toFloat()
                    mBubbleState = if (mDist < mBubbleRadius + MOVE_OFFSET) { //加上MOVE_OFFSET是为了方便拖拽
                        BUBBLE_STATE_CONNECT
                    } else {
                        BUBBLE_STATE_DEFAULT
                    }
                }

            }

            MotionEvent.ACTION_MOVE -> {
                if (mBubbleState !== BUBBLE_STATE_DEFAULT) {
                    mDist = Math.hypot((event.x - mBubFixedCenter!!.x).toDouble(), (event.y - mBubFixedCenter!!.y).toDouble()).toFloat()
                    mBubMovableCenter?.x = event.x
                    mBubMovableCenter?.y = event.y
                    if (mBubbleState == BUBBLE_STATE_CONNECT) {
                        if (mDist < mMaxDist - MOVE_OFFSET) {//当拖拽的距离在指定范围内，那么调整不动气泡的半径
                            mBubFixedRadius = mBubbleRadius - mDist / 8
                        } else {
                            mBubbleState = BUBBLE_STATE_APART//当拖拽的距离超过指定范围，那么改成分离状态
                        }
                    }
                    invalidate()
                }

            }

            MotionEvent.ACTION_UP -> {
                if (mBubbleState == BUBBLE_STATE_CONNECT) {
                    //橡皮筋动画效果
                    startBubbleRestAnim()
                } else if (mBubbleState == BUBBLE_STATE_APART) {
                    if (mDist < 2 * mBubbleRadius) {
                        startBubbleRestAnim()
                    } else {
                        //爆炸效果
                        startBubbleBurstAnim()
                    }

                }

            }

        }

        return true
    }

    /**
     * 爆炸效果
     */
    private fun startBubbleBurstAnim() {

        mBubbleState = BUBBLE_STATE_DISMISS
        val anim = ValueAnimator.ofInt(0, mBurstBitmapsArray!!.size)
        anim.duration = 500
        anim.interpolator = LinearInterpolator() as TimeInterpolator?
        anim.addUpdateListener { animation ->
            mCurDrawableIndex = animation.animatedValue as Int
            invalidate()
        }
        anim.start()


    }

    /**
     * 橡皮筋动画效果
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startBubbleRestAnim() {
        val anim = ValueAnimator.ofObject(PointFEvaluator(),
                PointF(mBubMovableCenter!!.x, mBubMovableCenter!!.y),
                PointF(mBubFixedCenter!!.x, mBubFixedCenter!!.y))
        anim.duration = 200
        anim.interpolator = OvershootInterpolator(5f)
        anim.addUpdateListener { animation ->
            mBubMovableCenter = animation.animatedValue as PointF
            invalidate()
        }
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                mBubbleState = BUBBLE_STATE_DEFAULT
            }
        })
        anim.start()

    }
}