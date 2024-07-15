package com.jason.jasonuitools.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.jason.jasonuitools.R;

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

    public JasonLinearLayout(Context context) {
        super(context);
    }

    public JasonLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public JasonLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public JasonLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet set) {
        TypedArray attr = getContext().obtainStyledAttributes(set, R.styleable.JasonLinearLayout);
        try {
            //圆角相关
            radius = attr.getDimension(R.styleable.JasonLinearLayout_ja_radius, 0);
            leftTopRadius = attr.getDimension(R.styleable.JasonLinearLayout_ja_leftTopRadius, radius);
            leftBottomRadius = attr.getDimension(R.styleable.JasonLinearLayout_ja_leftBottomRadius, radius);
            rightTopRadius = attr.getDimension(R.styleable.JasonLinearLayout_ja_rightTopRadius, radius);
            rightBottomRadius = attr.getDimension(R.styleable.JasonLinearLayout_ja_rightBottomRadius, radius);

            background = attr.getDrawable(R.styleable.JasonLinearLayout_ja_background);
            mBackground = background == null ? new ColorDrawable(0xffffffff) : background;
            pressBackground = attr.getDrawable(R.styleable.JasonLinearLayout_ja_pressBackground);
            selected = attr.getBoolean(R.styleable.JasonLinearLayout_ja_selected, false);
            selectedBackground = attr.getDrawable(R.styleable.JasonLinearLayout_ja_selectedBackground);


            // 阴影相关
            shadowRadius = attr.getDimensionPixelOffset(R.styleable.JasonLinearLayout_ja_shadowRadius, 0);
            shadowOffsetLeft = attr.getDimensionPixelOffset(R.styleable.JasonLinearLayout_ja_shadowOffsetLeft, 0);
            shadowOffsetRight = attr.getDimensionPixelOffset(R.styleable.JasonLinearLayout_ja_shadowOffsetRight, 0);
            shadowOffsetTop = attr.getDimensionPixelOffset(R.styleable.JasonLinearLayout_ja_shadowOffsetTop, 0);
            shadowOffsetBottom = attr.getDimensionPixelOffset(R.styleable.JasonLinearLayout_ja_shadowOffsetBottom, 0);
            shadowColor = attr.getColor(R.styleable.JasonLinearLayout_ja_shadowColor, 0);
            // 边框相关
            strokeWidth = attr.getDimensionPixelOffset(R.styleable.JasonLinearLayout_ja_strokeWidth, 0);
            strokeColor = attr.getColor(R.styleable.JasonLinearLayout_ja_strokeColor, 0);

        } finally {
            attr.recycle();
        }
        onDrawShadow();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (pressBackground != null) {
                    mBackground = pressBackground;
                }
                onDrawShadow();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mBackground = background;
                onDrawShadow();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    /**
     * 绘制背景
     */
    public void onDrawShadow() {
        if (selected) {
            mBackground = selectedBackground;
        }

        if (mBackground != null) {
            if (mBackground instanceof BitmapDrawable) {
                ShadowDrawable.setShadowDrawable(this, mBackground);
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
                this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                this.setBackground(drawable);
            }
        }
    }
}
