package com.example.hancher.testsurfaceview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by liaohaicong on 2018/5/14.
 * SurfaceView异步刷新进度
 * 通过循环和Thread.sleep实现不断刷新
 */

public class AsyncProgress extends SurfaceView implements SurfaceHolder.Callback {

    private static final int DEFAULT_PROGRESS_COLOR = Color.WHITE;  //颜色
    private static final int DEFAULT_PROGRESS_RADIUS = 20;  //dp  半径
    private static final int DEFAULT_PROGRESS_BAR_SIZE = 3;  //dp
    private static final int DEFAULT_PROGRESS_TEXT_SIZE = 14;  //sp

    private SurfaceHolder mSurfaceHolder;

    private Paint mProgressPaint;
    private Paint mTextPaint;

    private int mWidth;
    private int mHeight;
    private RectF mProgressRect;
    private int mTextWidth;
    private int mTextHeight;

    private int mStartAngel;

    private int mProgressColor;
    private int mProgressBarRadius;
    private int mProgressBarSize;
    private int mProgressTextSize;
    private String mProgressText;

    private DrawThread mDrawThread;
    private boolean mStop;

    private boolean mHasInitPaint;


    public AsyncProgress(Context context) {
        this(context, null);
    }

    public AsyncProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AsyncProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setZOrderOnTop(true);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        initAttribute(context, attrs);
        initPaint();
    }


    private void initAttribute(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AsyncProgress);
        if (typedArray != null) {
            mProgressColor = typedArray.getColor(R.styleable.AsyncProgress_progressbar_color, DEFAULT_PROGRESS_COLOR);

            mProgressBarRadius = typedArray.getDimensionPixelSize(R.styleable.AsyncProgress_progressbar_radius, DensityUtil.dip2px(context, DEFAULT_PROGRESS_RADIUS));
            mProgressBarSize = typedArray.getDimensionPixelSize(R.styleable.AsyncProgress_progressbar_size, DensityUtil.dip2px(context, DEFAULT_PROGRESS_BAR_SIZE));

            mProgressTextSize = typedArray.getDimensionPixelSize(R.styleable.AsyncProgress_progress_textsize, DensityUtil.dip2px(context, DEFAULT_PROGRESS_TEXT_SIZE));
            mProgressText = typedArray.getString(R.styleable.AsyncProgress_progress_text);

            typedArray.recycle();
        }
    }


    private void initPaint() {
        mHasInitPaint = true;
        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressBarSize);
        mProgressPaint.setColor(mProgressColor);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mProgressColor);
        mTextPaint.setTextSize(mProgressTextSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        mHeight = getHeight();
        mProgressRect = new RectF(mWidth / 2 - mProgressBarRadius, mHeight / 2 - mProgressBarRadius * 2, mWidth / 2 + mProgressBarRadius, mHeight / 2);
        mTextWidth = (int) mTextPaint.measureText(mProgressText);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        if (fontMetrics != null) {
            mTextHeight = (int) (fontMetrics.bottom - fontMetrics.top);
        }
        Log.d("hancher", "Progress Rect: " + mProgressRect.toString());
        Log.d("hancher", "Font Metric: " + fontMetrics.top + " " + fontMetrics.bottom + " " + fontMetrics.ascent + " " + fontMetrics.descent);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("hancher", "Surface Created");
        mStop = false;
        getDrawThread().start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("hancher", "Surface Changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("hancher", "Surface Destroyed");
        mStop = true;
        mStartAngel = 0;
        mDrawThread = null;
    }


    private DrawThread getDrawThread() {
        if (mDrawThread == null) {
            mDrawThread = new DrawThread();
        }
        return mDrawThread;
    }

    /**
     * 显示
     */
    public void show() {
        setVisibility(VISIBLE);
        getDrawThread().start();
    }

    /**
     * 隐藏
     */
    public void hide() {
        mStop = true;
        mStartAngel = 0;
        setVisibility(GONE);
    }

    //绘制显示内容
    private void drawProgressBarAndText(Canvas canvas) {
        canvas.drawArc(mProgressRect, mStartAngel, 270, false, mProgressPaint);
        canvas.drawText(mProgressText, mWidth / 2 - mTextWidth / 2, mHeight / 2 + mTextHeight + DensityUtil.dip2px(getContext(), 10), mTextPaint);
        mStartAngel = (mStartAngel + 30) % 360;
    }


    /**
     * 绘制线程
     */
    class DrawThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (!mStop) {
                Log.d("hancher", "run...");
                Canvas canvas = null;
                try {
                    canvas = mSurfaceHolder.lockCanvas();
                    //清除画面
                    canvas.drawColor(getResources().getColor(R.color.trans), PorterDuff.Mode.CLEAR);
                    drawProgressBarAndText(canvas);
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        //控制帧率
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public Paint getProgressPaint() {
        return mProgressPaint;
    }

    public void setProgressPaint(Paint progressPaint) {
        mProgressPaint = progressPaint;
    }

    public Paint getTextPaint() {
        return mTextPaint;
    }

    public void setTextPaint(Paint textPaint) {
        mTextPaint = textPaint;
    }

    public RectF getProgressRect() {
        return mProgressRect;
    }

    public void setProgressRect(RectF progressRect) {
        mProgressRect = progressRect;
    }

    public int getProgressBarRadius() {
        return mProgressBarRadius;
    }

    public void setProgressBarRadius(int progressBarRadius) {
        mProgressBarRadius = progressBarRadius;
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
    }

    public int getProgressBarSize() {
        return mProgressBarSize;
    }

    public void setProgressBarSize(int progressBarSize) {
        mProgressBarSize = progressBarSize;
    }

    public int getProgressTextSize() {
        return mProgressTextSize;
    }

    public void setProgressTextSize(int progressTextSize) {
        mProgressTextSize = progressTextSize;
    }

    public String getProgressText() {
        return mProgressText;
    }

    public void setProgressText(String progressText) {
        mProgressText = progressText;
        //重新初始化一次
        if (mHasInitPaint) {
            initPaint();
        }
    }
}

