package com.example.administrator.pathapi

import android.content.Context
import android.graphics.*
import android.view.View
import android.util.AttributeSet
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Bitmap


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
    var mBurstBitmapsArray: Array<Bitmap>? = null
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

    constructor(context: Context) : super(context) {

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
        mBurstBitmapsArray = arrayOf()
        for (i in 0 until mBurstDrawablesArray.size) {
            //将气泡爆炸的drawable转为bitmap
            val bitmap = BitmapFactory.decodeResource(resources, mBurstDrawablesArray[i])
            mBurstBitmapsArray!![i] = bitmap
        }


    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //1.静止装态，一个小球加消息数据

        if(mBubbleState==BUBBLE_STATE_DEFAULT){
            mBubFixedCenter?.y?.let { canvas.drawCircle(mBubFixedCenter?.x!!.toFloat(), it,mBubFixedRadius,mBubblePaint) }
        }

        //2.连接状态，一个小球加消息数据 ，贝塞尔曲线，本身位置上小球，大小可变换


        //3. 分离状态，一个小球加消息数据

        //4.消失状态，爆炸效果
    }
}