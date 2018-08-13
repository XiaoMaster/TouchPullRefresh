package com.xiao.touchpullrefresh.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * 贝塞尔
 * Created by xiao on 2018/8/13.
 */

public class BezierView extends View {


    public BezierView(Context context) {
        super(context);
        init();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();

    }

    private void initBezier() {

        float[] xPoints = new float[]{158, 170, 249, 274, 386, 387};
        float[] yPoints = new float[]{144, 305, 207, 271, 62, 389};

        Path path = mPath;
        int fps = 300;
        for (int i = 0; i < fps; i++) {
            float progress = i / (float) fps;
            float x = calculateBezier(progress, xPoints);
            float y = calculateBezier(progress, yPoints);

            path.lineTo(x * 2, y * 2 );
        }
    }

    private float calculateBezier(float t, float... values) {

        final int length = values.length;

        for (int i = length - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                values[j] = values[j] + (values[j + 1] - values[j]) * t;
            }
        }

        return values[0];
    }

    private Paint mPaint;

    private Path mPath;

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);

        mPath = new Path();
//        mPath.moveTo(100, 100);
//        mPath.lineTo(100, 300);
//        mPath.lineTo(300, 300);

        initBezier();

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawPath(mPath, mPaint);
    }
}
