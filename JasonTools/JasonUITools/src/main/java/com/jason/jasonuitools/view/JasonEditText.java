package com.jason.jasonuitools.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.annotation.ColorInt;

import com.jason.jasonuitools.util.DensityUtil;
import com.jason.jasonuitools.R;

import java.lang.reflect.Field;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年11月30日
 */
public class JasonEditText extends EditText {

    private Paint paint;
    private Path path;

    //背景相关
    protected int bgColorPress;
    protected int bgColor;
    private int bgDefaultColor = 0xFFFFFFFF;

    //圆角相关
    protected float leftTopRadius = 0;
    protected float leftBottomRadius = 0;
    protected float rightTopRadius = 0;
    protected float rightBottomRadius = 0;
    protected float radius = 0;

    //边框
    protected float strokeWidth = 0;
    protected float strokeLeftWidth = 0;
    protected float strokeRightWidth = 0;
    protected float strokeTopWidth = 0;
    protected float strokeBottomWidth = 0;
    protected int strokeColor = 0xFFFFFFFF;
    protected int strokeColorPress = 0xFFFFFFFF;
    protected int strokeDefaultColor = 0xFFFFFFFF;
    //文字相关
    protected int textColorPress = 0xFF000000;
    protected int textColor = 0xFF000000;

    public JasonEditText(Context context) {
        super(context);
    }

    public JasonEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public JasonEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public JasonEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }


    protected void init(AttributeSet attributeSet) {
        TypedArray attr = getContext().obtainStyledAttributes(attributeSet, R.styleable.JasonEditText);
        try {
            //圆角相关
            leftTopRadius = attr.getDimension(R.styleable.JasonEditText_ja_leftBottomRadius, 0);
            leftBottomRadius = attr.getDimension(R.styleable.JasonEditText_ja_leftBottomRadius, 0);
            rightTopRadius = attr.getDimension(R.styleable.JasonEditText_ja_rightTopRadius, 0);
            rightBottomRadius = attr.getDimension(R.styleable.JasonEditText_ja_rightBottomRadius, 0);
            radius = attr.getDimension(R.styleable.JasonEditText_ja_radius, 0);
            if (radius != 0) {
                leftTopRadius = radius;
                leftBottomRadius = radius;
                rightTopRadius = radius;
                rightBottomRadius = radius;
            }
            //边框
            strokeRightWidth = attr.getDimension(R.styleable.JasonEditText_ja_strokeRightWidth, 0);
            strokeLeftWidth = attr.getDimension(R.styleable.JasonEditText_ja_strokeLeftWidth, 0);
            strokeTopWidth = attr.getDimension(R.styleable.JasonEditText_ja_strokeTopWidth, 0);
            strokeBottomWidth = attr.getDimension(R.styleable.JasonEditText_ja_strokeBottomWidth, 0);
            strokeWidth = attr.getDimension(R.styleable.JasonEditText_ja_strokeWidth, DensityUtil.px2dp(0));

            strokeWidth = Math.min(strokeWidth, DensityUtil.dp2px(7));
            strokeLeftWidth = Math.min(strokeLeftWidth, DensityUtil.dp2px(7));
            strokeRightWidth = Math.min(strokeRightWidth, DensityUtil.dp2px(7));
            strokeTopWidth = Math.min(strokeTopWidth, DensityUtil.dp2px(7));
            strokeBottomWidth = Math.min(strokeBottomWidth, DensityUtil.dp2px(7));

            if (strokeWidth > 0) {
                strokeLeftWidth = strokeRightWidth = strokeTopWidth = strokeBottomWidth = strokeWidth;
            }
            strokeColor = attr.getColor(R.styleable.JasonEditText_ja_strokeColor, strokeDefaultColor);
            strokeColorPress = attr.getColor(R.styleable.JasonEditText_ja_strokeColorPress, strokeColor);
            strokeDefaultColor = strokeColor;


            // 背景颜色相关
            bgColor = attr.getInt(R.styleable.JasonEditText_ja_bgColor, bgDefaultColor);
            bgColorPress = attr.getInt(R.styleable.JasonEditText_ja_bgColorPress, bgColor);
            bgDefaultColor = bgColor;

            //文字颜色相关
            textColor = attr.getInt(R.styleable.JasonEditText_ja_textColor, 0xFF000000);
            textColorPress = attr.getInt(R.styleable.JasonEditText_ja_textColorPress, 0xFF000000);
            setTextColor(textColor);
        } finally {
            attr.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Field mBackground = getSpecialField(this.getClass(), "mBackground");
        mBackground.setAccessible(true);
        try {
            mBackground.set(this, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        if (paint == null) {
            paint = new Paint();
        }
        if (path == null) {
            path = new Path();
        }
        drawStroke(canvas);
        drawBackGround(canvas);

        super.onDraw(canvas);
    }

    /**
     * 绘制边框
     *
     * @param canvas
     */
    private void drawStroke(Canvas canvas) {
        path.reset();
        RectF rectF = new RectF(0, 0, this.getMeasuredWidth(), this.getMeasuredHeight());
        float[] outRadio = {leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius, rightBottomRadius, rightBottomRadius, leftBottomRadius, leftBottomRadius};
        // 绘制边框
        path.addRoundRect(rectF, outRadio, Path.Direction.CW);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(strokeDefaultColor);
        canvas.drawPath(path, paint);
    }

    /**
     * 绘制背景色
     *
     * @param canvas
     */
    private void drawBackGround(Canvas canvas) {
        path.reset();
        // 绘制背景色
        RectF rectF = new RectF(strokeLeftWidth, strokeTopWidth, this.getMeasuredWidth() - strokeRightWidth, this.getMeasuredHeight() - strokeBottomWidth);
        float[] outRadio = {leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius, rightBottomRadius, rightBottomRadius, leftBottomRadius, leftBottomRadius};
        path.addRoundRect(rectF, outRadio, Path.Direction.CW);
        //绘制背景
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(bgDefaultColor);
        canvas.drawPath(path, paint);
    }

    /**
     * 设置默认背景色和按压背景色
     *
     * @param color
     */
    public void setJaBgColor(@ColorInt int color) {
        this.bgColor = color;
        this.bgColorPress = color;
        this.bgDefaultColor = color;
        this.invalidate();
    }

    /**
     * 设置按压效果背景色
     *
     * @param color
     */
    public void setJaBgColorPress(int color) {
        this.bgColorPress = color;
        this.bgDefaultColor = color;
        this.invalidate();
    }

    /**
     * 设置默认背景色
     *
     * @param color
     */
    public void setJaBgDefaultColor(int color) {
        this.bgColor = color;
        this.bgDefaultColor = color;
        this.invalidate();
    }

    public void setJaStrokeWidth(float width) {
        this.strokeWidth=width;
        this.strokeLeftWidth = width;
        this.strokeRightWidth = width;
        this.strokeTopWidth = width;
        this.strokeBottomWidth = width;
        this.invalidate();
    }

    /**
     * 设置左边边框宽度
     *
     * @param width
     */
    public void setJaStrokeLeftWidth(float width) {
        this.strokeLeftWidth = width;
        this.invalidate();
    }

    /**
     * 设置右边边框宽度
     *
     * @param width
     */
    public void setJaStrokeRightWidth(float width) {
        this.strokeRightWidth = width;
        this.invalidate();
    }

    /**
     * 设置上边边框宽度
     *
     * @param width
     */
    public void setJaStrokeTopWidth(float width) {
        this.strokeTopWidth = width;
        this.invalidate();
    }

    /**
     * 设置底边边框宽度
     *
     * @param width
     */
    public void setJaStrokeBottomWidth(float width) {
        this.strokeBottomWidth = width;
        this.invalidate();
    }

    /**
     * 利用反射功能，获取指定的字段，如果此类中没有找到则从父类中进行获取
     *
     * @param clazz
     * @param fieldName
     * @return
     */
    public Field getSpecialField(Class clazz, String fieldName) {

        while (clazz != null && clazz != Object.class) {
            Field declaredField = null;
            try {
                declaredField = clazz.getDeclaredField(fieldName);
            } catch (Exception exception) {
            }
            if (declaredField == null) {
                clazz = clazz.getSuperclass();
            } else {
                return declaredField;
            }
        }
        return null;
    }

}
