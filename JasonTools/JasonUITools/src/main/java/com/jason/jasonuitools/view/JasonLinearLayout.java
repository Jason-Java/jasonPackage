package com.jason.jasonuitools.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年05月23日
 */
public class JasonLinearLayout extends LinearLayout {
    private JasonBaseView baseView;


    public JasonLinearLayout(Context context) {
        super(context);
    }

    public JasonLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        baseView = new JasonBaseView(this, context, attrs);
    }

    public JasonLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        baseView = new JasonBaseView(this, context, attrs);
    }

    public JasonLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        baseView = new JasonBaseView(this, context, attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        baseView.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }


}
