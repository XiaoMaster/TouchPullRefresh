package com.xiao.touchpullrefresh;

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
 * 贝塞尔曲线
 * 贝塞尔曲线又称贝兹曲线或贝济埃曲线，是应用于二维图形应用程序的数学曲线
 * Created by xiao on 2018/8/13.
 */

public class BezierCurve extends View {

    public BezierCurve(Context context) {
        super(context);
        init();
    }


    public BezierCurve(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BezierCurve(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BezierCurve(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private Paint mPaint;

    private Path mPath;

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(5);

        mPath = new Path();
        mPath.moveTo(100, 100);
        mPath.lineTo(100, 300);
        mPath.lineTo(300, 300);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawPath(mPath, mPaint);
    }
}
