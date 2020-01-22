/*
 *
 *   Created by Sina Dalvand on 1/21/20 8:23 AM
 *   Copyright (c) 2020 . All rights reserved.
 *
 *
 */
package ir.sinadalvand.videorecoreder.utils.customView;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

public class VideoRecordingButton extends View implements Animatable {
    private Paint buttonPaint, behindButtonPaint, strokeProgressPaint;
    private RectF rectF;

    private float buttonRadius;
    private float behindButtonRadius;
    private int progressStroke;

    private int buttonColor;
    private int progressColor;
    private int behindButtonColor;

    private int startAngle = 270;
    private int sweepAngle;

    private float buttonGap;

    private int currentMilliSecond = 0;
    private int maxMillisecond;

    private boolean isRecording = false;
    private Boolean loading = false;


    public VideoRecordingButton(Context context) {
        super(context);
        init(context);
    }

    public VideoRecordingButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoRecordingButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public VideoRecordingButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        buttonRadius = 100f;
        behindButtonRadius = 120f;
        progressStroke = 5;
        buttonGap = 0f;
        behindButtonColor = Color.parseColor("#e5e5e7");
        buttonColor = Color.parseColor("#FFFFFF");
        progressColor = Color.YELLOW;
        maxMillisecond = 7000;


        buttonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonPaint.setColor(buttonColor);
        buttonPaint.setStyle(Paint.Style.FILL);

        behindButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        behindButtonPaint.setColor(behindButtonColor);
        behindButtonPaint.setStyle(Paint.Style.FILL);
        behindButtonPaint.setAlpha(60);


        strokeProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokeProgressPaint.setColor(progressColor);
        strokeProgressPaint.setStyle(Paint.Style.STROKE);
        strokeProgressPaint.setStrokeWidth(progressStroke);
        strokeProgressPaint.setStrokeCap(Paint.Cap.ROUND);


        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        canvas.drawCircle(cx, cy, behindButtonRadius, behindButtonPaint);
        canvas.drawCircle(cx, cy, buttonRadius, buttonPaint);
        sweepAngle = 360 * currentMilliSecond / maxMillisecond;

        rectF.set(cx - (behindButtonRadius) - buttonGap, cy - (behindButtonRadius) - buttonGap, cx + (behindButtonRadius) + buttonGap, cy + (behindButtonRadius) + buttonGap);
        canvas.drawArc(rectF, startAngle, sweepAngle, false, strokeProgressPaint);

    }




    ObjectAnimator animator;

    @SuppressLint("ObjectAnimatorBinding")
    private ObjectAnimator progressAnimate() {
        animator = ObjectAnimator.ofFloat(this, "progress", currentMilliSecond, maxMillisecond);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = ((Float) (animation.getAnimatedValue())).intValue();

                if (isRecording) {
                    setCurrentMilliSecond(value);

                    if (onRecordListener != null) {
                        onRecordListener.onZoomIn(currentMilliSecond / (maxMillisecond * 1.0f));
                    }

                    if (currentMilliSecond == maxMillisecond)
                        stop();

                }

            }
        });

        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(maxMillisecond);
        return animator;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(loading) return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if (onRecordListener != null) {
                    if (onRecordListener.onStarting()) {
                        start();
                        progressAnimate().start();
                    }

                } else {
                    start();
                    progressAnimate().start();
                }


                break;
            case MotionEvent.ACTION_UP:
                stop();
                break;
            case MotionEvent.ACTION_MOVE:

                break;
        }
        return true;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = ((int) (behindButtonRadius + 10) * 2 + (int) buttonGap * 2 + progressStroke + 30);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(size, widthSize);
        } else {
            width = size;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(size, heightSize);
        } else {
            height = size;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    public void start() {


        isRecording = true;
        scaleAnimation(1.3f, 1.3f);

    }

    @Override
    public void stop() {

        if (isRecording)
            if (currentMilliSecond != maxMillisecond) {
                animator.cancel();
                isRecording = false;
                if (onRecordListener != null)
                    onRecordListener.onCancel();
            } else {
                if (onRecordListener != null)
                    onRecordListener.onFinish();
            }


        isRecording = false;
        currentMilliSecond = 0;
        scaleAnimation(1f, 1f);


    }


    @Override
    public boolean isRunning() {
        return isRecording;
    }

    private void scaleAnimation(float scaleX, float scaleY) {
        this.animate()
                .scaleX(scaleX)
                .scaleY(scaleY)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    public void setCurrentMilliSecond(int currentMilliSecond) {
        this.currentMilliSecond = currentMilliSecond;
        postInvalidate();
    }

    public void setPercent(float percent){
        this.currentMilliSecond = (int) (maxMillisecond*(percent/100));
        postInvalidate();
    }

    public void loadMode(Boolean loading){
        this.loading = loading;

        if(!loading){
            this.currentMilliSecond = 0;
            postInvalidate();
        }
    }

    public void setProgressColor(int color){
        progressColor = color;
        strokeProgressPaint.setColor(progressColor);
        postInvalidate();
    }

    OnRecordListener onRecordListener;

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
    }


    public interface OnRecordListener {
        void onZoomIn(float value);

        Boolean onStarting();

        void onCancel();

        void onFinish();

    }
}