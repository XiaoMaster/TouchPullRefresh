package com.xiao.touchpullrefresh.widget;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.xiao.touchpullrefresh.BezierCurve;
import com.xiao.touchpullrefresh.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 粘性下拉小球
 * Created by xiao on 2018/8/13.
 */

public class PullBallView extends View {

    private Paint mCirclePaint;

    /**
     * 圆的半径
     */
    private float mCircleRadius = 50;

    private float mCirclePointX;

    private float mCirclePointY;

    /**
     * 可拖动的最大高度
     */
    private int mDraggableHeight = 300;

    private float mProgress;

    /**
     * 角度变化
     */
    private int mTargetAngle = 105;

    /**
     * 目标的宽度
     */
    private int mTargetWidth = 400;

    private Path mPath = new Path();

    private Paint mPathPaint;

    private Paint mDotPaint;
    /**
     * 进度差值器
     */
    private Interpolator mProgressInterpolator = new DecelerateInterpolator();
    /**
     * 角度差值器
     */
    private Interpolator mTangentAnleInterpolator;

    /**
     * 重心点最终的高度，决定控制点的Y坐标
     */
    private int mTargetGravityHeight;

    private Drawable mDrawable = null;

    private int mDrawableMargin = 0;

    public PullBallView(Context context) {
        super(context);
        init(null);
    }

    public PullBallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PullBallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PullBallView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        final Context context = getContext();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PullBallView);
        int color = array.getColor(R.styleable.PullBallView_pColor, 0x20000000);
        mCircleRadius = array.getDimension(R.styleable.PullBallView_pRadius, mCircleRadius);
        mDraggableHeight = array.getDimensionPixelOffset(R.styleable.PullBallView_pDragHeight, mDraggableHeight);
        mTargetAngle = array.getInteger(R.styleable.PullBallView_pTangentAngle, mTargetAngle);
        mTargetWidth = array.getDimensionPixelOffset(R.styleable.PullBallView_pTargetWidth, mTargetWidth);
        mTargetGravityHeight = array.getDimensionPixelOffset(R.styleable.PullBallView_pTargetGravityHeight, mTargetGravityHeight);
        mDrawable = array.getDrawable(R.styleable.PullBallView_pContentDrawable);
        mDrawableMargin = array.getDimensionPixelOffset(R.styleable.PullBallView_pContentDrawableMargin, mDrawableMargin);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        mCirclePaint = paint;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        mPathPaint = paint;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        mDotPaint = paint;

        //切角路径差值器
        mTangentAnleInterpolator = PathInterpolatorCompat.create(
                mCircleRadius * 2.0f / mDraggableHeight, 90.0f / mTargetAngle
        );
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int count = canvas.save();
        float tranX = (getWidth() - getCurrentValue(getWidth(), mTargetWidth, mProgress)) / 2;
        canvas.translate(tranX, 0);

        canvas.drawPath(mPath, mPathPaint);
        canvas.drawCircle(mCirclePointX, mCirclePointY, mCircleRadius, mCirclePaint);

        for (Point point : points) {
            canvas.drawPoint(point.x, point.y, mDotPaint);
        }
        Drawable drawable = mDrawable;
        if (drawable != null) {
            canvas.save();
            canvas.clipRect(drawable.getBounds());

            drawable.draw(canvas);
            canvas.restore();
        }

        canvas.restoreToCount(count);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int requireMinWidth = (int) (2 * mCircleRadius + getPaddingRight() + getPaddingLeft());
        int requireMinHeight = (int) ((mDraggableHeight * mProgress + 0.5f)) + getPaddingBottom() + getPaddingTop();

        int measureWidth = 0;
        if (widthMode == MeasureSpec.EXACTLY) {
            //确定的值
            measureWidth = width;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //父布局允许的最多宽度值
            measureWidth = Math.min(requireMinWidth, width);
        } else if (widthMode == MeasureSpec.UNSPECIFIED) {
            measureWidth = requireMinWidth;
        }

        int measureHeight = 0;

        if (heightMode == MeasureSpec.AT_MOST) {
            measureHeight = Math.min(requireMinHeight, height);
        } else if (heightMode == MeasureSpec.EXACTLY) {
            measureHeight = height;
        } else if (widthMode == MeasureSpec.UNSPECIFIED) {
            measureHeight = requireMinHeight;
        }

        setMeasuredDimension(measureWidth, measureHeight);
    }


    public void setProgress(float progress) {
        mProgress = progress;
        //请求进行重新测量
        requestLayout();
    }

    /**
     * 当改变大小是触发
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updatePathLayout();
    }


    private List<Point> points = new ArrayList<>();

    /**
     * 更新路径相关操作
     */
    private void updatePathLayout() {
        points.clear();
        //获取进度
        final float progress = mProgressInterpolator.getInterpolation(mProgress);

        float w = getCurrentValue(getWidth(), mTargetWidth, mProgress);
        float h = getCurrentValue(0, mDraggableHeight, mProgress);

        final float cPointX = w / 2.0f;

        final float cRadius = mCircleRadius;
        final float cPointY = h - cRadius;

        //控制点结束y的坐标
        final float endControlY = mTargetGravityHeight;

        //更新圆的坐标
        mCirclePointX = cPointX;
        mCirclePointY = cPointY;

        points.add(new Point((int) mCirclePointX, (int) mCirclePointY));

        final Path path = mPath;
        path.reset();
        //复位
        path.moveTo(0, 0);

        points.add(new Point(0, 0));

        //左边部分的结束点和控制点
        float lEndPointX, lEndPointY;
        float lControlPointX, lControlPointY;

        //获取当前切线的弧度
        float angle = mTargetAngle * mTangentAnleInterpolator.getInterpolation(mProgress);
        double radian = Math.toRadians(getCurrentValue(0, angle, progress));
        float x = (float) (Math.sin(radian) * cRadius);
        float y = (float) (Math.cos(radian) * cRadius);

        lEndPointX = cPointX - x;
        lEndPointY = cPointY + y;

        lControlPointY = getCurrentValue(0, endControlY, progress);
        //控制点与结束点之间的高度
        float tHeight = lEndPointY - lControlPointY;
        float tWidth = (float) (tHeight / Math.tan(radian));

        lControlPointX = lEndPointX - tWidth;
        points.add(new Point((int) lControlPointX, (int) lControlPointY));
        points.add(new Point((int) lEndPointX, (int) lEndPointY));
        path.quadTo(lControlPointX, lControlPointY, lEndPointX, lEndPointY);
        path.lineTo(cPointX + (cPointX - lEndPointX), lEndPointY);

        points.add(new Point((int) (cPointX + (cPointX - lEndPointX)), (int) lEndPointY));

        path.quadTo(cPointX + (cPointX - lControlPointX), lControlPointY, w, 0);
        points.add(new Point((int) (cPointX + (cPointX - lControlPointX)), (int) lControlPointY));
        points.add(new Point((int) w, 0));

        //更新内容部分drawable
        updateContentLayout(cPointX, cPointY, cRadius);


    }

    /**
     * 对内容部分进行测量和绘制
     */
    private void updateContentLayout(float cPointX, float cPointY, float cRadius) {

        Drawable drawable = mDrawable;
        if (drawable != null) {

            int margin = mDrawableMargin;
            int l = (int) (cPointX - cRadius + margin);
            int r = (int) (cPointX + cRadius - margin);
            int t = (int) (cPointY - cRadius + margin);
            int b = (int) (cPointY + cRadius - margin);
            drawable.setBounds(l, t, r, b);
        }
    }

    private float getCurrentValue(float start, float end, float progress) {
        return start + (end - start) * progress;
    }

    private ValueAnimator valueAnimator;

    /**
     * 添加释放操作
     */
    private void release() {

        if (valueAnimator == null) {
            final ValueAnimator animator = ValueAnimator.ofFloat(mProgress, 0f);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(400);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Object val = animator.getAnimatedValue();
                    if (val instanceof Float) {
                        setProgress((Float) val);
                    }
                }
            });

            valueAnimator = animator;
        } else {
            valueAnimator.cancel();
            valueAnimator.setFloatValues(mProgress, 0f);
        }
        valueAnimator.start();
    }

    public void releaseView() {
        release();
    }
}
