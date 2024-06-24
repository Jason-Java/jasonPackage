package com.jason.jasonuitools.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jason.jasonuitools.R;
import com.jason.jasonuitools.util.DensityUtil;

import java.lang.reflect.Field;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年12月11日
 */
public class JasonTextView extends androidx.appcompat.widget.AppCompatTextView {
    private Paint paint;
    private Path path;

    //背景相关
    protected int bgColorPress;
    protected int bgColor;
    private int bgDefaultColor = 0xFF3498DB;

    //圆角相关
    protected float leftTopRadius = 0;
    protected float leftBottomRadius = 0;
    protected float rightTopRadius = 0;
    protected float rightBottomRadius = 0;
    protected float radius = 0;

    //边框
    protected float strokeWidth = 0;
    protected int strokeColor = 0xFF3498DB;
    protected int strokeColorPress = 0xFF3498DB;
    protected int strokeDefaultColor = 0xFF3498DB;
    //文字相关
    protected int textColorPress = 0xFFFFFFFF;
    protected int textColor = 0xFFFFFFFF;

    public JasonTextView(@NonNull Context context) {
        super(context);
    }

    public JasonTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public JasonTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    protected void init(AttributeSet attributeSet) {
        TypedArray attr = getContext().obtainStyledAttributes(attributeSet, R.styleable.JasonTextView);
        try {
            //圆角相关
            leftTopRadius = attr.getDimension(R.styleable.JasonTextView_ja_leftBottomRadius, 0);
            leftBottomRadius = attr.getDimension(R.styleable.JasonTextView_ja_leftBottomRadius, 0);
            rightTopRadius = attr.getDimension(R.styleable.JasonTextView_ja_rightTopRadius, 0);
            rightBottomRadius = attr.getDimension(R.styleable.JasonTextView_ja_rightBottomRadius, 0);
            radius = attr.getDimension(R.styleable.JasonTextView_ja_radius, 0);
            if (radius != 0) {
                leftTopRadius = radius;
                leftBottomRadius = radius;
                rightTopRadius = radius;
                rightBottomRadius = radius;
            }
            //边框
            strokeWidth = attr.getDimension(R.styleable.JasonTextView_ja_strokeWidth, DensityUtil.px2dp(0));
            if (strokeWidth > DensityUtil.dp2px(7)) {
                strokeWidth = DensityUtil.dp2px(7);
            }
            strokeColor = attr.getColor(R.styleable.JasonTextView_ja_strokeColor, strokeDefaultColor);
            strokeColorPress = attr.getColor(R.styleable.JasonTextView_ja_strokeColorPress, strokeColor);
            strokeDefaultColor = strokeColor;

            // 背景颜色相关
            bgColor = attr.getInt(R.styleable.JasonTextView_ja_bgColor, bgDefaultColor);
            bgColorPress = attr.getInt(R.styleable.JasonTextView_ja_bgColorPress, bgColor);
            bgDefaultColor = bgColor;

            //文字颜色相关
            textColor = attr.getInt(R.styleable.JasonTextView_ja_textColor, 0xFF3498DB);
            textColorPress = attr.getInt(R.styleable.JasonTextView_ja_textColorPress, textColor);
            setTextColor(textColor);
        } finally {
            attr.recycle();
        }
        Field mBackground = getSpecialField(this.getClass(), "mBackground");
        mBackground.setAccessible(true);
        try {
            mBackground.set(this, null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return !isEnabled();
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            bgDefaultColor = bgColorPress;
            strokeDefaultColor = strokeColorPress;
            setTextColor(textColorPress);
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
            bgDefaultColor = bgColor;
            strokeDefaultColor = strokeColor;
            setTextColor(textColor);
        }
        this.invalidate();
       /* this.invalidate();
        invalidateOutline();*/
        return super.onTouchEvent(event);

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
        if (strokeWidth > 0) {
            drawStroke(canvas);
        }
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
        canvas.save();
    }

    /**
     * 绘制背景色
     *
     * @param canvas
     */
    private void drawBackGround(Canvas canvas) {
        path.reset();
        // 绘制背景色
        RectF rectF = new RectF(strokeWidth, strokeWidth, this.getMeasuredWidth() - strokeWidth, this.getMeasuredHeight() - strokeWidth);
        float[] outRadio = {leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius, rightBottomRadius, rightBottomRadius, leftBottomRadius, leftBottomRadius};
        path.addRoundRect(rectF, outRadio, Path.Direction.CW);
        //绘制背景
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(bgDefaultColor);
        canvas.drawPath(path, paint);
        canvas.save();
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

    /**
     * 设置边框宽度
     *
     * @param strokeWidth
     */
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        this.invalidate();
    }

    /**
     * 设置边框默认颜色和按压颜色
     *
     * @param color
     */
    public void setStrokeColor(int color) {
        this.strokeColor = color;
        this.strokeColorPress = color;
        this.strokeDefaultColor = color;
        this.invalidate();
    }

    /**
     * 设置边框按压颜色
     *
     * @param color
     */
    public void setStrokeColorPress(int color) {
        this.strokeColorPress = color;
        this.strokeDefaultColor = color;
        this.invalidate();
    }

    /**
     * 设置边框默认颜色
     *
     * @param color
     */
    public void setStrokeDefaultColor(int color) {
        this.strokeColor = color;
        this.strokeDefaultColor = color;
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
