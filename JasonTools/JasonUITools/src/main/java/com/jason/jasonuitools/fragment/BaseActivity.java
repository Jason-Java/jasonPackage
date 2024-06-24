package com.jason.jasonuitools.fragment;

import android.os.CountDownTimer;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年03月27日
 */
public class BaseActivity extends AppCompatActivity {
    private CountDownTimer timer;
    /**
     * 倒计时时长
     */
    private long countDownDuration = 60 * 1000;
    private boolean isRunTimer = true;


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            startTimer();
        } else {
            cancelTimer();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        startTimer();
        return super.dispatchTouchEvent(ev);
    }

    public final void startTimer(long timer) {
        cancelTimer();
        if (this.timer == null) {
            countDownDuration = timer;
            this.timer = new CountDownTimer(timer, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    downTimer(millisUntilFinished / 1000);
                }

                @Override
                public void onFinish() {
                    finishTimer();
                }
            };
        }
        if (isRunTimer)
            this.timer.start();
    }

    //开始倒计时
    public void startTimer() {
        startTimer(countDownDuration);
    }


    //取消倒计时
    public void cancelTimer() {
        if (timer != null)
            timer.cancel();
    }

    //结束倒计时
    public void finishTimer() {
        finish();
    }


    public void downTimer(long downTimer) {

    }

    /**
     * 设置倒计时是否生效
     *
     * @param isRunTimer false 倒计时不生效
     */
    public void setRunTimer(boolean isRunTimer) {
        this.isRunTimer = isRunTimer;
    }

    @Override
    protected void onDestroy() {
        cancelTimer();
        super.onDestroy();
    }
}
