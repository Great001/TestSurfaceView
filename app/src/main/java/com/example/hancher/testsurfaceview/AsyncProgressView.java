package com.example.hancher.testsurfaceview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by liaohaicong on 2018/5/14.
 * 异步的进度显示
 */

public class AsyncProgressView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;

    private Paint mProgressPaint;
    private Paint mTextPaint;

    private int mWidth;
    private int mHeight;
    private RectF mProgressRect;
    private int mTextWidth;
    private int mTextHeight;

    private int mStartAngel;

    private int mProgressRadius;
    private int mProgressColor;
    private int mProgressBarSize;
    private int mProgressTextSize;

    private String mProgressText;

    private DrawThread mDrawThread;
    private boolean mStop;


    public AsyncProgressView(Context context) {
        this(context, null);
    }

    public AsyncProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AsyncProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        mProgressRadius = DensityUtil.dip2px(context, 20);
        mProgressColor = getResources().getColor(R.color.white);
        mProgressBarSize = DensityUtil.dip2px(context, 3);
        mProgressTextSize = DensityUtil.sp2px(context, 14);
        mProgressText = "Preparing Materials…";
        initPaint();
    }

    private void initPaint() {
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
        mProgressRect = new RectF(mWidth / 2 - mProgressRadius, mHeight / 2 - mProgressRadius * 2, mWidth / 2 + mProgressRadius, mHeight / 2);
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
        mDrawThread.forceStop();
        mDrawThread = null;
    }


    //绘制显示内容
    private void drawProgressBarAndText(Canvas canvas) {
        Log.d("hancher", "startAngel: " + mStartAngel);
        canvas.drawArc(mProgressRect, mStartAngel, 300, false, mProgressPaint);
        canvas.drawText(mProgressText, mWidth / 2 - mTextWidth / 2, mHeight / 2 + mTextHeight + DensityUtil.dip2px(getContext(), 10), mTextPaint);
        mStartAngel = (mStartAngel + 30) % 360;
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

    public int getProgressRadius() {
        return mProgressRadius;
    }

    public void setProgressRadius(int progressRadius) {
        mProgressRadius = progressRadius;
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
    }

    private DrawThread getDrawThread() {
        if (mDrawThread == null) {
            mDrawThread = new DrawThread();
        }
        return mDrawThread;
    }

    public void start() {

    }

    /**
     * 停止刷新
     */
    public void stop() {
        mStop = true;
        setVisibility(GONE);
    }

    /**
     * 绘制线程
     */
    class DrawThread extends Thread {

        private static final int MSG_UPDATE = 1111;
        private Handler mUpdateHandler;


        private void updateDraw() {
            Canvas canvas = null;
            canvas = mSurfaceHolder.lockCanvas();
            if (canvas != null) {
                //清除画面
                canvas.drawColor(getResources().getColor(R.color.trans), PorterDuff.Mode.CLEAR);
                drawProgressBarAndText(canvas);
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }


        @Override
        public void run() {
            Looper.prepare();
            mUpdateHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MSG_UPDATE:
                            Log.d("hancher", "update");
                            updateDraw();
                            if (!mStop) {
                                sendEmptyMessageDelayed(MSG_UPDATE, 50);
                            }
                            break;
                        default:
                            break;

                    }
                }
            };
            mUpdateHandler.sendEmptyMessage(MSG_UPDATE);
            Looper.loop();
        }

        public void forceStop() {
            if (mUpdateHandler != null) {
                mUpdateHandler.removeCallbacksAndMessages(null);
            }
        }
    }
}
