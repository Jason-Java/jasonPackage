package com.jason.jasonuitools.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.jason.jasonuitools.R;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年05月24日
 */
public class JasonBaseView {
    private View view;
    private Context context;
    //圆角相关
    private float leftTopRadius = 0;
    private float leftBottomRadius = 0;
    private float rightTopRadius = 0;
    private float rightBottomRadius = 0;
    private float radius = 0;
    /**
     * 阴影相关
     */
    private int shadowRadius = 0;
    private int shadowOffsetLeft;
    private int shadowOffsetRight;
    private int shadowOffsetTop;
    private int shadowOffsetBottom;
    private int shadowColor;

    /**
     * 背景相关
     */
    private Drawable background;
    private Drawable pressBackground;
    private boolean selected;
    private Drawable selectedBackground;
    private Drawable mBackground;
    // 边框相关
    private float strokeWidth = 0;
    protected int strokeColor;

    public JasonBaseView(View view, Context context, AttributeSet attrs) {
        this.view = view;
        this.context = context;
        init(context, attrs);
        onDraw();
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.JasonBaseView);
        try {
            //圆角相关
            radius = attr.getDimension(R.styleable.JasonBaseView_ja_radius, 0);
            leftTopRadius = attr.getDimension(R.styleable.JasonBaseView_ja_leftTopRadius, radius);
            leftBottomRadius = attr.getDimension(R.styleable.JasonBaseView_ja_leftBottomRadius, radius);
            rightTopRadius = attr.getDimension(R.styleable.JasonBaseView_ja_rightTopRadius, radius);
            rightBottomRadius = attr.getDimension(R.styleable.JasonBaseView_ja_rightBottomRadius, radius);

            background = attr.getDrawable(R.styleable.JasonBaseView_ja_background);
            mBackground = background;
            pressBackground = attr.getDrawable(R.styleable.JasonBaseView_ja_pressBackground);
            selected = attr.getBoolean(R.styleable.JasonBaseView_ja_selected, false);
            selectedBackground = attr.getDrawable(R.styleable.JasonBaseView_ja_selectedBackground);


            // 阴影相关
            shadowRadius = attr.getDimensionPixelOffset(R.styleable.JasonBaseView_ja_shadowRadius, 0);
            shadowOffsetLeft = attr.getDimensionPixelOffset(R.styleable.JasonBaseView_ja_shadowOffsetLeft, 0);
            shadowOffsetRight = attr.getDimensionPixelOffset(R.styleable.JasonBaseView_ja_shadowOffsetRight, 0);
            shadowOffsetTop = attr.getDimensionPixelOffset(R.styleable.JasonBaseView_ja_shadowOffsetTop, 0);
            shadowOffsetBottom = attr.getDimensionPixelOffset(R.styleable.JasonBaseView_ja_shadowOffsetBottom, 0);
            shadowColor = attr.getColor(R.styleable.JasonBaseView_ja_shadowColor, 0);
            // 边框相关
            strokeWidth = attr.getDimensionPixelOffset(R.styleable.JasonBaseView_ja_strokeWidth, 0);
            strokeColor = attr.getColor(R.styleable.JasonBaseView_ja_strokeColor, 0);

        } finally {
            attr.recycle();
        }
    }

    /**
     * 触摸事件
     *
     * @param event
     */
    public void onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (pressBackground != null) {
                    mBackground = pressBackground;
                }
                onDraw();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mBackground = background;
                onDraw();
                break;
        }
    }

    /**
     * 绘制背景
     */
    public void onDraw() {
        if (selected) {
            mBackground = selectedBackground;
        }

        if (mBackground != null) {
            if (mBackground instanceof BitmapDrawable) {
                ShadowDrawable.setShadowDrawable(view, mBackground);
            } else if (mBackground instanceof ColorDrawable) {
                int color = ((ColorDrawable) mBackground).getColor();
                Drawable drawable = new ShadowDrawable.Builder()
                        .setBgColor(color)
                        .setShadowColor(shadowColor)
                        .setShadowRadius(shadowRadius)
                        .setOffsetLeft(shadowOffsetLeft)
                        .setOffsetRight(shadowOffsetRight)
                        .setOffsetTop(shadowOffsetTop)
                        .setOffsetBottom(shadowOffsetBottom)
                        .setLeftBottomRadius(leftBottomRadius)
                        .setLeftTopRadius(leftTopRadius)
                        .setRightBottomRadius(rightBottomRadius)
                        .setRightTopRadius(rightTopRadius)
                        .setStrokeWidth(strokeWidth)
                        .setStrokeColor(strokeColor)
                        .builder();
                view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                view.setBackground(drawable);
            }
        }
    }
}
