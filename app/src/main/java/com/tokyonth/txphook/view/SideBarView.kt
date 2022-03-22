package com.tokyonth.txphook.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tokyonth.txphook.R;

import java.util.Arrays;
import java.util.List;

public class SideBarView extends View {

    // 计算波浪贝塞尔曲线的角弧长值
    private static final double ANGLE = Math.PI * 45 / 180;
    private static final double ANGLE_R = Math.PI * 90 / 180;
    private OnTouchLetterChangeListener listener;

    // 渲染字母表
    private List<String> mLetters;

    // 当前选中的位置
    private int mChoose = -1;

    private int oldChoose;

    private int newChoose;

    // 字母列表画笔
    private final Paint mLettersPaint = new Paint();

    // 提示字母画笔
    private final Paint mTextPaint = new Paint();
    // 波浪画笔
    private Paint mWavePaint = new Paint();

    private float mTextSize;
    private int mTextColor;
    private int mWidth;
    private int mHeight;
    private int mItemHeight;
    private int mPadding;

    // 波浪路径
    private final Path mWavePath = new Path();

    // 圆形路径
    private final Path mBallPath = new Path();

    // 手指滑动的Y点作为中心点
    private int mCenterY; //中心点Y

    // 贝塞尔曲线的分布半径
    private int mRadius;

    // 圆形半径
    private int mBallRadius;
    // 用于过渡效果计算
    ValueAnimator mRatioAnimator;

    // 用于绘制贝塞尔曲线的比率
    private float mRatio;

    // 选中字体的坐标
    private float mPosX, mPosY;

    // 圆形中心点X
    private float mBallCentreX;

    private int fixBallYAxis;

    public SideBarView(Context context) {
        this(context, null);
    }

    public SideBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mLetters = Arrays.asList(context.getResources().getStringArray(R.array.SideBarLetters));
        mTextColor = Color.parseColor("#999999");
        int mWaveColor = Color.BLACK;
        int mTextColorChoose = Color.WHITE;
        mTextSize = 50;
        float mLargeTextSize = 72;
        mPadding = 60;

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SideBarView);
            mTextColor = a.getColor(R.styleable.SideBarView_sidebarTextColor, mTextColor);
            mTextColorChoose = a.getColor(R.styleable.SideBarView_sidebarChooseTextColor, mTextColorChoose);
            mTextSize = a.getFloat(R.styleable.SideBarView_sidebarTextSize, mTextSize);
            mLargeTextSize = a.getFloat(R.styleable.SideBarView_sidebarLargeTextSize, mLargeTextSize);
            mWaveColor = a.getColor(R.styleable.SideBarView_sidebarBackgroundColor, mWaveColor);
            mRadius = a.getInt(R.styleable.SideBarView_sidebarRadius, 60);
            mBallRadius = a.getInt(R.styleable.SideBarView_sidebarBallRadius, 64);
            a.recycle();
        }

        mWavePaint = new Paint();
        mWavePaint.setAntiAlias(true);
        mWavePaint.setStyle(Paint.Style.FILL);
        mWavePaint.setColor(mWaveColor);

        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColorChoose);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(mLargeTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final float y = event.getY();
        final float x = event.getX();

        oldChoose = mChoose;
        newChoose = (int) (y / mHeight * mLetters.size());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (x < mWidth - 2 * mRadius) {
                    return false;
                }
                mCenterY = (int) y;
                startAnimator(mRatio, 1.0f);
                break;
            case MotionEvent.ACTION_MOVE:
                mCenterY = (int) y;
                if (oldChoose != newChoose) {
                    if (newChoose >= 0 && newChoose < mLetters.size()) {
                        mChoose = newChoose;
                        if (listener != null) {
                            listener.onLetterChange(mLetters.get(newChoose));
                        }
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                startAnimator(mRatio, 0f);
                mChoose = -1;
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mItemHeight = (mHeight - mPadding) / mLetters.size();
        mPosX = mWidth - 1.6f * mTextSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制字母列表
        drawLetters(canvas);
        //绘制波浪
        //drawWavePath(canvas);
        //绘制圆
        drawBallPath(canvas);
        //绘制选中的字体
        drawChooseText(canvas);
    }

    private void drawLetters(Canvas canvas) {
/*        RectF rectF = new RectF();
        rectF.left = mPosX - mTextSize;
        rectF.right = mPosX + mTextSize;
        rectF.top = mTextSize / 2;
        rectF.bottom = mHeight - mTextSize / 2;*/

/*        mLettersPaint.reset();
        mLettersPaint.setStyle(Paint.Style.FILL);
        mLettersPaint.setColor(Color.parseColor("#F9F9F9"));
        mLettersPaint.setAntiAlias(true);
      //  canvas.drawRoundRect(rectF, mTextSize, mTextSize, mLettersPaint);

        mLettersPaint.reset();
        mLettersPaint.setStyle(Paint.Style.STROKE);
        mLettersPaint.setColor(mTextColor);
        mLettersPaint.setAntiAlias(true);
       // canvas.drawRoundRect(rectF, mTextSize, mTextSize, mLettersPaint);*/

        for (int i = 0; i < mLetters.size(); i++) {
            mLettersPaint.reset();
            mLettersPaint.setColor(mTextColor);
            mLettersPaint.setAntiAlias(true);
            mLettersPaint.setTextSize(mTextSize);
            mLettersPaint.setTextAlign(Paint.Align.CENTER);

            Paint.FontMetrics fontMetrics = mLettersPaint.getFontMetrics();
            float baseline = Math.abs(-fontMetrics.bottom - fontMetrics.top);

            float posY = mItemHeight * i + baseline / 2 + mPadding;

            if (i == mChoose) {
                mPosY = posY;
            } else {
                canvas.drawText(mLetters.get(i), mPosX, posY, mLettersPaint);
            }
        }
    }

    private void drawChooseText(Canvas canvas) {
        if (mChoose != -1) {
            // 绘制右侧选中字符
            mLettersPaint.reset();
            mLettersPaint.setColor(Color.BLACK);
            mLettersPaint.setTextSize(mTextSize);
            mLettersPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(mLetters.get(mChoose), mPosX, mPosY, mLettersPaint);

            // 绘制提示字符
            if (mRatio >= 0.9f) {
                /**
                 * 修复提示圆球能跟随手指滑动移出到View外
                 * Side非全充满屏幕情况
                 * By Tokyonth
                 */

                String target = mLetters.get(mChoose);
                Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
                float baseline = Math.abs(-fontMetrics.bottom - fontMetrics.top);
                float x = mBallCentreX;
                //float y = mCenterY + baseline / 2;
                //canvas.drawText(target, x, y, mTextPaint);
                canvas.drawText(target, x, fixBallYAxis + baseline / 2, mTextPaint);
            }
        }
    }

    /**
     * 绘制波浪
     */
    private void drawWavePath(Canvas canvas) {
        mWavePath.reset();
        // 移动到起始点
        mWavePath.moveTo(mWidth, mCenterY - 3 * mRadius);
        //计算上部控制点的Y轴位置
        int controlTopY = mCenterY - 2 * mRadius;

        //计算上部结束点的坐标
        int endTopX = (int) (mWidth - mRadius * Math.cos(ANGLE) * mRatio);
        int endTopY = (int) (controlTopY + mRadius * Math.sin(ANGLE));
        mWavePath.quadTo(mWidth, controlTopY, endTopX, endTopY);

        //计算中心控制点的坐标
        int controlCenterX = (int) (mWidth - 1.8f * mRadius * Math.sin(ANGLE_R) * mRatio);
        int controlCenterY = mCenterY;
        //计算下部结束点的坐标
        int controlBottomY = mCenterY + 2 * mRadius;
        int endBottomY = (int) (controlBottomY - mRadius * Math.cos(ANGLE));
        mWavePath.quadTo(controlCenterX, controlCenterY, endTopX, endBottomY);
        mWavePath.quadTo(mWidth, controlBottomY, mWidth, controlBottomY + mRadius);
        mWavePath.close();
        canvas.drawPath(mWavePath, mWavePaint);
    }

    private void drawBallPath(Canvas canvas) {
        //x轴的移动路径
        mBallCentreX = (mWidth + mBallRadius) - (2.0f * mRadius + 2.0f * mBallRadius) * mRatio;
        mBallPath.reset();

        /**
         * 修复提示圆球能跟随手指滑动移出到View外
         * Side非全充满屏幕情况
         * By Tokyonth
         */
        if (mCenterY < mItemHeight + mPadding / 2) {
            fixBallYAxis = mItemHeight + mPadding / 2;
        } else fixBallYAxis = Math.min(mCenterY, mItemHeight * mLetters.size());

        mBallPath.addCircle(mBallCentreX, fixBallYAxis, mBallRadius, Path.Direction.CW);

        mBallPath.op(mWavePath, Path.Op.DIFFERENCE);
        mBallPath.close();
        canvas.drawPath(mBallPath, mWavePaint);
    }

    private void startAnimator(float... value) {
        if (mRatioAnimator == null) {
            mRatioAnimator = new ValueAnimator();
        }
        mRatioAnimator.cancel();
        mRatioAnimator.setFloatValues(value);
        mRatioAnimator.addUpdateListener(value1 -> {
            mRatio = (float) value1.getAnimatedValue();
            //球弹到位的时候，并且点击的位置变了，即点击的时候显示当前选择位置
            if (mRatio == 1f && oldChoose != newChoose) {
                if (newChoose >= 0 && newChoose < mLetters.size()) {
                    mChoose = newChoose;
                    if (listener != null) {
                        listener.onLetterChange(mLetters.get(newChoose));
                    }
                }
            }
            invalidate();
        });
        mRatioAnimator.start();
    }

    public void setOnTouchLetterChangeListener(OnTouchLetterChangeListener listener) {
        this.listener = listener;
    }

    public List<String> getLetters() {
        return mLetters;
    }

    public void setLetters(List<String> letters) {
        this.mLetters = letters;
        invalidate();
    }

    public interface OnTouchLetterChangeListener {

        void onLetterChange(String letter);

    }

}
