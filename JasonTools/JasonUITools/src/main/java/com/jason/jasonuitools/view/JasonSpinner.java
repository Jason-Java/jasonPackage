package com.jason.jasonuitools.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.Spinner;

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
public class JasonSpinner extends androidx.appcompat.widget.AppCompatSpinner {
    private Paint paint;
    private Path path;
    //背景相关
    private int bgDefaultColor = 0xFFFFFFFF;
    //圆角相关
    protected float leftTopRadius = 0;
    protected float leftBottomRadius = 0;
    protected float rightTopRadius = 0;
    protected float rightBottomRadius = 0;
    protected float radius = 0;

    //边框
    protected float strokeWidth = 0;
    protected int strokeColor = 0xFF000000;
    protected int strokeColorPress = 0xFF000000;
    protected int strokeDefaultColor = 0xFF000000;


    // 箭头方向
    private int arrowDirection = 3;
    private static final int ARROW_DIRECTION_TOP = 1;
    private static final int ARROW_DIRECTION_BOTTOM = 2;

    public JasonSpinner(@NonNull Context context) {
        super(context);
    }

    public JasonSpinner(@NonNull Context context, int mode) {
        super(context, mode);
    }

    public JasonSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public JasonSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public JasonSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
        init(attrs);
    }

    public JasonSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, mode, popupTheme);
        init(attrs);
    }


    protected void init(AttributeSet attributeSet) {
        TypedArray attr = getContext().obtainStyledAttributes(attributeSet, R.styleable.JasonSpinner);
        try {
            //圆角相关
            leftTopRadius = attr.getDimension(R.styleable.JasonSpinner_ja_leftBottomRadius, 0);
            leftBottomRadius = attr.getDimension(R.styleable.JasonSpinner_ja_leftBottomRadius, 0);
            rightTopRadius = attr.getDimension(R.styleable.JasonSpinner_ja_rightTopRadius, 0);
            rightBottomRadius = attr.getDimension(R.styleable.JasonSpinner_ja_rightBottomRadius, 0);
            radius = attr.getDimension(R.styleable.JasonSpinner_ja_radius, 0);
            if (radius != 0) {
                leftTopRadius = radius;
                leftBottomRadius = radius;
                rightTopRadius = radius;
                rightBottomRadius = radius;
            }
            //边框
            strokeWidth = attr.getDimension(R.styleable.JasonSpinner_ja_strokeWidth, DensityUtil.px2dp(0));
            if (strokeWidth > DensityUtil.dp2px(7)) {
                strokeWidth = DensityUtil.dp2px(7);
            }
            strokeColor = attr.getColor(R.styleable.JasonSpinner_ja_strokeColor, strokeDefaultColor);
            strokeColorPress = attr.getColor(R.styleable.JasonSpinner_ja_strokeColorPress, strokeColor);
            strokeDefaultColor = strokeColor;

            // 背景颜色相关
            bgDefaultColor = attr.getInt(R.styleable.JasonSpinner_ja_bgColor, bgDefaultColor);
            // 箭头方向
            arrowDirection = attr.getInt(R.styleable.JasonSpinner_ja_arrowDirection, arrowDirection);

           /* //文字颜色相关
            textColor = attr.getInt(R.styleable.JasonButton_ja_textColor, 0xFF3498DB);
            textColorPress = attr.getInt(R.styleable.JasonButton_ja_textColorPress, textColor);
            setTextColor(textColor);*/
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
        drawTriangle(canvas, arrowDirection);

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
     * 绘制箭头
     *
     * @param canvas
     */
    private void drawTriangle(Canvas canvas, int direction) {
        int viewWidth = this.getMeasuredWidth() - 20;
        int viewHeight = this.getMeasuredHeight();


        //等边三角形
        float sideLength = 50;
        // 等边三角形的高
        float height = (float) (sideLength * Math.sqrt(3) * 0.5);
        PointF point1 = null;
        PointF point2 = null;
        PointF point3 = null;
        switch (direction) {
            case ARROW_DIRECTION_TOP:
                point1 = new PointF(viewWidth - sideLength, (viewHeight / 2) + (height / 2));
                point2 = new PointF(viewWidth, (viewHeight / 2) + (height / 2));
                point3 = new PointF((float) (viewWidth - sideLength + (sideLength / 2)), (viewHeight / 2) - (sideLength / 2));
                break;

            case ARROW_DIRECTION_BOTTOM:
                point1 = new PointF(viewWidth - sideLength, (viewHeight / 2) - (height / 2));
                point2 = new PointF(viewWidth, (viewHeight / 2) - (height / 2));
                point3 = new PointF((float) (viewWidth - sideLength + (sideLength / 2)), (viewHeight / 2) + (sideLength / 2));
                break;
        }

        path.reset();
        path.moveTo(point1.x, point1.y);
        path.lineTo(point2.x, point2.y);
        path.lineTo(point3.x, point3.y);
        path.lineTo(point1.x, point1.y);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0XFF000000);
        paint.setAntiAlias(true);
        canvas.drawPath(path, paint);
/*
        // 绘制箭头正方形的大小，边长
        int sideLength = 100;
        float left = viewWidth - sideLength;
        float top = (viewHeight / 2) - (sideLength / 2);
        float right = left + sideLength;
        float bottom = top + sideLength;
        RectF rectF = new RectF(left, top, right, bottom);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.arrow);
        canvas.(bitmap, null, rectF, paint);
*/
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {

        }
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
