package com.xiao.touchpullrefresh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.xiao.touchpullrefresh.widget.PullBallView;

public class MainActivity extends AppCompatActivity {

    private static final int TOUCH_MOVE_MAX_Y = 600;
    private float mTouchMoveStartY;

    PullBallView mPullBallView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPullBallView = findViewById(R.id.pull_ball);

        findViewById(R.id.activity_container).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int actionMasked = event.getActionMasked();

                switch (actionMasked) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchMoveStartY = event.getY();
                        Log.i("TAG", "mTouchMoveStartY = " + mTouchMoveStartY);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float moveY = event.getY();
                        Log.i("TAG", "mTouchMoveStartY = " + mTouchMoveStartY + " moveY=" + moveY);
                        if (moveY >= mTouchMoveStartY){
                            float moveSize =moveY - mTouchMoveStartY;
                            float progress = moveSize >= TOUCH_MOVE_MAX_Y ? 1 : moveSize / TOUCH_MOVE_MAX_Y;
                            mPullBallView.setProgress(progress);
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mPullBallView.releaseView();
                        break;
                }
                return false;
            }
        });
    }
}
