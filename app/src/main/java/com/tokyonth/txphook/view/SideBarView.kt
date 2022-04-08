package com.tokyonth.txphook.view

import android.animation.ValueAnimator
import android.view.MotionEvent
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

import com.tokyonth.txphook.R

/**
 * SideBarView
 * By Tokyonth 2022/3/22
 */
class SideBarView : View {

    companion object {
        // 计算波浪贝塞尔曲线的角弧长值
        private const val ANGLE = Math.PI * 45 / 180
        private const val ANGLE_R = Math.PI * 90 / 180
    }

    private var listener: OnTouchLetterChangeListener? = null

    // 渲染字母表
    private var mLetters: MutableList<String> = ArrayList()

    // 当前选中的位置
    private var mTextColor = -0x666667
    private var mTextSize = 50F
    private var mPadding = 60
    private var mChoose = -1
    private var oldChoose = 0
    private var newChoose = 0
    private var mWidth = 0
    private var mHeight = 0
    private var mItemHeight = 0

    // 字母列表画笔
    private val mLettersPaint = Paint()

    // 提示字母画笔
    private val mTextPaint = Paint()

    // 波浪画笔
    private var mWavePaint = Paint()

    // 波浪路径
    private val mWavePath = Path()

    // 圆形路径
    private val mBallPath = Path()

    // 手指滑动的Y点作为中心点, 中心点Y
    private var mCenterY = 0

    // 贝塞尔曲线的分布半径
    private var mRadius = 0

    // 圆形半径
    private var mBallRadius = 0

    // 用于过渡效果计算
    private var mRatioAnimator: ValueAnimator? = null

    // 用于绘制贝塞尔曲线的比率
    private var mRatio = 0F

    // 选中字体的坐标
    private var mPosX = 0F
    private var mPosY = 0F

    // 圆形中心点X
    private var mBallCentreX = 0F
    private var fixBallYAxis = 0F
    private var baseLine = 0F
    private var isDrawWave = false

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        var mWaveColor = Color.BLACK
        var mTextColorChoose = Color.WHITE
        var mLargeTextSize = mTextSize * 1.2F
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SideBarView)
            val entries = a.getTextArray(R.styleable.SideBarView_sidebarEntries)
            if (entries != null) {
                optEntries(entries)
            }

            mTextColor = a.getColor(R.styleable.SideBarView_sidebarTextColor, mTextColor)
            mTextColorChoose =
                a.getColor(R.styleable.SideBarView_sidebarChooseTextColor, mTextColorChoose)
            mTextSize = a.getFloat(R.styleable.SideBarView_sidebarTextSize, mTextSize)
            mLargeTextSize =
                a.getFloat(R.styleable.SideBarView_sidebarLargeTextSize, mTextSize * 1.2F)
            mWaveColor = a.getColor(R.styleable.SideBarView_sidebarBackgroundColor, mWaveColor)
            mRadius = a.getInt(R.styleable.SideBarView_sidebarRadius, 60)
            mBallRadius = a.getInt(R.styleable.SideBarView_sidebarBallRadius, 64)
            a.recycle()
        }
        mWavePaint.apply {
            isAntiAlias = true
            color = mWaveColor
            style = Paint.Style.FILL
        }
        mTextPaint.apply {
            isAntiAlias = true
            color = mTextColorChoose
            textSize = mLargeTextSize
            style = Paint.Style.FILL
            textAlign = Paint.Align.CENTER
        }
    }

    private fun optEntries(entries: Array<CharSequence>) {
        val ts = entries.map {
            it.toString()
        }
        mLetters.addAll(ts)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val y = event.y
        val x = event.x
        oldChoose = mChoose
        newChoose = (y / mHeight * mLetters.size).toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (x < mWidth - 2 * mRadius) {
                    return false
                }
                mCenterY = y.toInt()
                startAnimator(mRatio, 1.0F)
            }
            MotionEvent.ACTION_MOVE -> {
                mCenterY = y.toInt()
                if (oldChoose != newChoose) {
                    if (newChoose >= 0 && newChoose < mLetters.size) {
                        mChoose = newChoose
                        if (listener != null) {
                            listener!!.onLetterChange(mLetters[newChoose])
                        }
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                startAnimator(mRatio, 0F)
                mChoose = -1
            }
            else -> {}
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            heightSpecSize = (mLetters.size * mTextSize).toInt() + mPadding + mBallRadius
        } else if (heightSpecMode == MeasureSpec.EXACTLY) {
            heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        }
        if (mLetters.isEmpty()) {
            return
        }
        setMeasuredDimension(measuredWidth, heightSpecSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (mLetters.isEmpty()) {
            return
        }
        mHeight = h
        mWidth = w
        mItemHeight = (mHeight - mPadding) / mLetters.size
        mPosX = mWidth - 1.6F * mTextSize
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mLetters.isEmpty()) {
            return
        }
        //绘制字母列表
        drawLetters(canvas)
        //绘制波浪
        drawWavePath(canvas)
        //绘制圆
        drawBallPath(canvas)
        //绘制选中的字体
        drawChooseText(canvas)
    }

    private fun drawLetters(canvas: Canvas) {
        mLettersPaint.reset()
        mLettersPaint.apply {
            color = mTextColor
            isAntiAlias = true
            textSize = mTextSize
            textAlign = Paint.Align.CENTER
        }
        val fontMetrics = mLettersPaint.fontMetrics
        baseLine = abs(-fontMetrics.bottom - fontMetrics.top)

        for (i in mLetters.indices) {
            val posY = mItemHeight * (i + 1) + baseLine / 2
            if (i == mChoose) {
                mPosY = posY
            } else {
                canvas.drawText(mLetters[i], mPosX, posY, mLettersPaint)
            }
        }
    }

    private fun drawChooseText(canvas: Canvas) {
        if (mChoose != -1) {
            // 绘制右侧选中字符
            mLettersPaint.reset()
            mLettersPaint.apply {
                color = Color.BLACK
                textSize = mTextSize
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(mLetters[mChoose], mPosX, mPosY, mLettersPaint)

            // 绘制提示字符
            if (mRatio >= 0.9F) {
                val target = mLetters[mChoose]
                canvas.drawText(target, mBallCentreX, fixBallYAxis + baseLine / 2, mTextPaint)
            }
        }
    }

    private fun drawWavePath(canvas: Canvas) {
        if (!isDrawWave) {
            return
        }
        mWavePath.reset()
        // 移动到起始点
        mWavePath.moveTo(mWidth.toFloat(), (mCenterY - 3 * mRadius).toFloat())
        //计算上部控制点的Y轴位置
        val controlTopY = mCenterY - 2 * mRadius

        //计算上部结束点的坐标
        val endTopX = (mWidth - mRadius * cos(ANGLE) * mRatio).toInt()
        val endTopY = (controlTopY + mRadius * sin(ANGLE)).toInt()
        mWavePath.quadTo(
            mWidth.toFloat(),
            controlTopY.toFloat(),
            endTopX.toFloat(),
            endTopY.toFloat()
        )

        //计算中心控制点的坐标
        val controlCenterX = (mWidth - 1.8F * mRadius * sin(ANGLE_R) * mRatio).toInt()
        val controlCenterY = mCenterY
        //计算下部结束点的坐标
        val controlBottomY = mCenterY + 2 * mRadius
        val endBottomY = (controlBottomY - mRadius * cos(ANGLE)).toInt()
        mWavePath.quadTo(
            controlCenterX.toFloat(),
            controlCenterY.toFloat(),
            endTopX.toFloat(),
            endBottomY.toFloat()
        )
        mWavePath.quadTo(
            mWidth.toFloat(),
            controlBottomY.toFloat(),
            mWidth.toFloat(),
            (controlBottomY + mRadius).toFloat()
        )
        mWavePath.close()
        canvas.drawPath(mWavePath, mWavePaint)
    }

    private fun drawBallPath(canvas: Canvas) {
        //x轴的移动路径
        mBallCentreX = mWidth + mBallRadius - (2.0F * mRadius + 2.0F * mBallRadius) * mRatio
        mBallPath.reset()
        val firstY = (mItemHeight shr 1) + baseLine
        fixBallYAxis = if (mCenterY <= firstY) {
            firstY
        } else {
            min(mCenterY, mItemHeight * mLetters.size).toFloat()
        }
        mBallPath.addCircle(mBallCentreX, fixBallYAxis, mBallRadius.toFloat(), Path.Direction.CW)
        mBallPath.op(mWavePath, Path.Op.DIFFERENCE)
        mBallPath.close()
        canvas.drawPath(mBallPath, mWavePaint)
    }

    private fun startAnimator(vararg values: Float) {
        if (mRatioAnimator == null) {
            mRatioAnimator = ValueAnimator()
        }
        mRatioAnimator?.cancel()
        mRatioAnimator?.setFloatValues(*values)
        mRatioAnimator?.addUpdateListener { value: ValueAnimator ->
            mRatio = value.animatedValue as Float
            //球弹到位的时候，并且点击的位置变了，即点击的时候显示当前选择位置
            if (mRatio == 1F && oldChoose != newChoose) {
                if (newChoose >= 0 && newChoose < mLetters.size) {
                    mChoose = newChoose
                    if (listener != null) {
                        listener!!.onLetterChange(mLetters[newChoose])
                    }
                }
            }
            invalidate()
        }
        mRatioAnimator?.start()
    }

    fun getSideLetters(): MutableList<String> {
        return mLetters
    }

    fun setSideLetters(list: List<String>) {
        mLetters.clear()
        mLetters.addAll(list)
        invalidate()
    }

    fun setEnableDrawWave(isEnable: Boolean) {
        isDrawWave = isEnable
    }

    fun interface OnTouchLetterChangeListener {
        fun onLetterChange(letter: String)
    }

    fun setOnTouchLetterChangeListener(listener: OnTouchLetterChangeListener?) {
        this.listener = listener
    }

}
