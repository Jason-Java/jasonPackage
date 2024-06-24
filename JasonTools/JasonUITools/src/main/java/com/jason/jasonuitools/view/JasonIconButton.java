package com.jason.jasonuitools.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.jason.jasonuitools.R;

import java.util.regex.Matcher;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年05月09日
 */
public class JasonIconButton extends View {

    private Paint paint;
    private Paint mShadowPaint;
    /**
     * 圆角路径
     */
    private Path radiusPath;
    //圆角相关
    protected float leftTopRadius = 0;
    protected float leftBottomRadius = 0;
    protected float rightTopRadius = 0;
    protected float rightBottomRadius = 0;
    protected float radius = 0;
    // icon 相关
    protected Drawable iconDrawable;
    private float iconTop = 0;
    private float iconLeft = 0;

    // 文字相关
    private CharSequence text;
    private int textColor;
    private float textSize;
    private float textTop;
    private float textLeft;

    private int orientation;
    private int gravity;
    private int textOrientation;

    private Drawable background;
    private Drawable pressBackground;
    private boolean selected;
    private Drawable selectedBackground;

    private Drawable mBackground;

    /**
     * 阴影相关
     */
    private int shadowRadius = 0;
    private int shadowOffsetLeft;
    private int shadowOffsetRight;
    private int shadowOffsetTop;
    private int shadowOffsetBottom;
    private int shadowColor;


    public JasonIconButton(Context context) {
        super(context);
    }

    public JasonIconButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public JasonIconButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public JasonIconButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void init(AttributeSet attributeSet) {
        TypedArray attr = getContext().obtainStyledAttributes(attributeSet, R.styleable.JasonIconButton);
        try {
            //圆角相关
            radius = attr.getDimension(R.styleable.JasonIconButton_ja_radius, 0);
            leftTopRadius = attr.getDimension(R.styleable.JasonIconButton_ja_leftTopRadius, radius);
            leftBottomRadius = attr.getDimension(R.styleable.JasonIconButton_ja_leftBottomRadius, radius);
            rightTopRadius = attr.getDimension(R.styleable.JasonIconButton_ja_rightTopRadius, radius);
            rightBottomRadius = attr.getDimension(R.styleable.JasonIconButton_ja_rightBottomRadius, radius);

            iconDrawable = attr.getDrawable(R.styleable.JasonIconButton_ja_icon);
            iconTop = attr.getDimension(R.styleable.JasonIconButton_ja_iconTop, 0);
            iconLeft = attr.getDimension(R.styleable.JasonIconButton_ja_iconLeft, 0);

            // 文字相关
            text = attr.getText(R.styleable.JasonIconButton_ja_text);
            textColor = attr.getColor(R.styleable.JasonIconButton_ja_textColor, 0);
            textSize = attr.getDimensionPixelSize(R.styleable.JasonIconButton_ja_textSize, -1);
            textTop = attr.getDimension(R.styleable.JasonIconButton_ja_textTop, 0);
            textLeft = attr.getDimension(R.styleable.JasonIconButton_ja_textLeft, 0);
            // 页面布局方向
            orientation = attr.getInt(R.styleable.JasonIconButton_ja_orientation, -1);
            gravity = attr.getInt(R.styleable.JasonIconButton_ja_gravity, -1);
            // 文字方向

            textOrientation = attr.getInt(R.styleable.JasonIconButton_ja_textOrientation, 0);

            background = attr.getDrawable(R.styleable.JasonIconButton_ja_background);
            mBackground = background;
            pressBackground = attr.getDrawable(R.styleable.JasonIconButton_ja_pressBackground);
            selected = attr.getBoolean(R.styleable.JasonIconButton_ja_selected, false);
            selectedBackground = attr.getDrawable(R.styleable.JasonIconButton_ja_selectedBackground);


            // 阴影相关
            shadowRadius = attr.getDimensionPixelOffset(R.styleable.JasonIconButton_ja_shadowRadius, 0);
            shadowOffsetLeft = attr.getDimensionPixelOffset(R.styleable.JasonIconButton_ja_shadowOffsetLeft, 0);
            shadowOffsetRight = attr.getDimensionPixelOffset(R.styleable.JasonIconButton_ja_shadowOffsetRight, 0);
            shadowOffsetTop = attr.getDimensionPixelOffset(R.styleable.JasonIconButton_ja_shadowOffsetTop, 0);
            shadowOffsetBottom = attr.getDimensionPixelOffset(R.styleable.JasonIconButton_ja_shadowOffsetBottom, 0);
            shadowColor = attr.getColor(R.styleable.JasonIconButton_ja_shadowColor, 0);

        } finally {
            attr.recycle();
        }
        if (paint == null) {
            paint = new Paint();
        }
        if (radiusPath == null) {
            radiusPath = new Path();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (pressBackground != null) {
                    mBackground = pressBackground;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mBackground = background;
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawShadow(canvas);
        drawRadius(canvas);
        drawBackGround(canvas);
        // 测量文字
        if (text != null && text.length() != 0) {
            paint.setTextSize(textSize);
        }
        drawIcon(canvas);
        drawText(canvas);
    }


    /**
     * 绘制阴影
     *
     * @param canvas
     */
    private void drawShadow(Canvas canvas) {
        if (shadowRadius > 0) {
            mShadowPaint = new Paint();
            mShadowPaint.setColor(Color.TRANSPARENT);
            mShadowPaint.setAntiAlias(true);
            mShadowPaint.setShadowLayer(shadowRadius, shadowOffsetRight, shadowOffsetBottom, shadowColor);
            mShadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
            setLayerType(LAYER_TYPE_SOFTWARE, null);
            // 绘制背景色
            radiusPath.reset();
            RectF rectF = new RectF( shadowRadius-shadowOffsetLeft
                    , shadowRadius- shadowOffsetTop
                    , this.getMeasuredWidth() - (shadowRadius+ shadowOffsetRight)
                    , this.getMeasuredHeight() - ( shadowRadius+shadowOffsetBottom));
            float[] outRadio = {leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius, rightBottomRadius, rightBottomRadius, leftBottomRadius, leftBottomRadius};
            radiusPath.addRoundRect(rectF, outRadio, Path.Direction.CW);
            canvas.drawPath(radiusPath, mShadowPaint);
        }
    }

    /**
     * 绘制圆角
     *
     * @param canvas
     */
    private void drawRadius(Canvas canvas) {
        radiusPath.reset();
        RectF rectF = new RectF(shadowRadius-shadowOffsetLeft
                , shadowRadius-shadowOffsetTop
                , this.getMeasuredWidth()-(shadowRadius+shadowOffsetRight)
                , this.getMeasuredHeight() - (shadowRadius+ shadowOffsetBottom));
        float[] outRadio = {leftTopRadius, leftTopRadius, rightTopRadius, rightTopRadius, rightBottomRadius, rightBottomRadius, leftBottomRadius, leftBottomRadius};
        radiusPath.addRoundRect(rectF, outRadio, Path.Direction.CW);
    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private void drawBackGround(Canvas canvas) {
        if (selected) {
            mBackground = selectedBackground;
        }
        if (mBackground != null) {
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            Bitmap bitmap = null;
            RectF desRect = new RectF(0, 0, width, height);
            if (mBackground instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) mBackground).getBitmap();
                canvas.drawBitmap(bitmap, null, desRect, paint);
                canvas.drawBitmap(bitmap, null, desRect, paint);
            } else if (mBackground instanceof ColorDrawable) {
                int color = ((ColorDrawable) mBackground).getColor();
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(color);
                canvas.drawPath(radiusPath, paint);
            }
        }
    }

    /**
     * 绘制icon
     *
     * @param canvas
     */
    private void drawIcon(Canvas canvas) {
        if (iconDrawable == null) {
            return;
        }
        if (gravity == -1) {
            Bitmap bitmap = ((BitmapDrawable) iconDrawable).getBitmap();
            int width = iconDrawable.getIntrinsicWidth();
            int height = iconDrawable.getIntrinsicHeight();
            float rectLeft = iconLeft;
            float rectTop = iconTop;
            float rectRight = rectLeft + width;
            float rectBottom = rectTop + height;
            RectF desRect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
            canvas.drawBitmap(bitmap, null, desRect, paint);
        }
        // 水平居中
        else if (gravity == 0) {
            Bitmap bitmap = ((BitmapDrawable) iconDrawable).getBitmap();
            int width = iconDrawable.getIntrinsicWidth();
            int height = iconDrawable.getIntrinsicHeight();
            float rectLeft = (getMeasuredWidth() - width) / 2;
            float rectTop = iconTop;
            float rectRight = rectLeft + width;
            float rectBottom = rectTop + height;
            RectF desRect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
            canvas.drawBitmap(bitmap, null, desRect, paint);
        } // 垂直居中
        else if (gravity == 1) {
            Bitmap bitmap = ((BitmapDrawable) iconDrawable).getBitmap();
            int width = iconDrawable.getIntrinsicWidth();
            int height = iconDrawable.getIntrinsicHeight();
            float rectLeft = iconLeft;
            float rectTop = (getMeasuredHeight() - height) / 2;
            float rectRight = rectLeft + width;
            float rectBottom = rectTop + height;
            RectF desRect = new RectF(rectLeft, rectTop, rectRight, rectBottom);
            canvas.drawBitmap(bitmap, null, desRect, paint);
        }
    }

    /**
     * 绘制文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        if (text == null) {
            return;
        }
        paint.setTextSize(textSize);
        // 不处理
        if (gravity == -1) {
            float textHeight = (paint.getFontMetrics().top - paint.getFontMetrics().bottom);
            float left = textLeft;
            float top = textTop;
            top = top - textHeight;
            drawText(canvas, left, top);
        }
        // 水平居中
        else if (gravity == 0) {
            float textHeight = (paint.getFontMetrics().top - paint.getFontMetrics().bottom);
            float textWidth = paint.measureText((String) text);
            float left = (getMeasuredWidth() - textWidth) / 2;
            float top = textTop - textHeight;
            drawText(canvas, left, top);
        }
        // 垂直居中
        else if (gravity == 1) {
            if (textOrientation == 0) {
                float textHeight = Math.abs(paint.getFontMetrics().top + paint.getFontMetrics().bottom);
                float left = textLeft;
                float top = (getMeasuredHeight() + textHeight) / 2;
                drawText(canvas, left, top);
            } else if (textOrientation == 1) {
                float textHeight = Math.abs(paint.getFontMetrics().top + paint.getFontMetrics().bottom) * (text.length() - 1);
                float left = textLeft;
                float top = (getMeasuredHeight() - textHeight) / 2;
                drawText(canvas, left, top);
            }
        }

    }

    private void drawText(Canvas canvas, float left, float top) {
        // 水平绘制文字
        if (textOrientation == 0) {
            paint.setColor(textColor);
            canvas.drawText((String) text, left, top, paint);
        }
        // 竖排绘制文字
        else if (textOrientation == 1) {
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            float textHeight = paint.ascent() + paint.descent();
            float fontTop = paint.getFontMetrics().top;
            float fontBottom = paint.getFontMetrics().bottom;
            top = top;
            // 获取字体的高度
            for (int i = 0; i < text.length(); i++) {
                canvas.drawText(String.valueOf(text.charAt(i)), left, top, paint);
                top = (top - textHeight) - (fontTop + fontBottom) / 2;
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    /**
     * 设置圆角
     *
     * @param radius
     */
    public void setRadius(float radius) {
        this.leftBottomRadius = radius;
        this.leftTopRadius = radius;
        this.rightBottomRadius = radius;
        this.rightTopRadius = radius;
    }

    /**
     * 设置圆角
     *
     * @param leftTopRadius     左上角
     * @param rightTopRadius    右上角
     * @param rightBottomRadius 右下角
     * @param leftBottomRadius  左下角
     */
    public void setRadius(float leftTopRadius, float rightTopRadius, float rightBottomRadius, float leftBottomRadius) {
        this.leftBottomRadius = leftBottomRadius;
        this.leftTopRadius = leftTopRadius;
        this.rightBottomRadius = rightBottomRadius;
        this.rightTopRadius = rightTopRadius;
        invalidate();
    }

    public void setLeftTopRadius(float leftTopRadius) {
        this.leftTopRadius = leftTopRadius;
        invalidate();
    }

    public void setLeftBottomRadius(float leftBottomRadius) {
        this.leftBottomRadius = leftBottomRadius;
        invalidate();
    }

    public void setRightTopRadius(float rightTopRadius) {
        this.rightTopRadius = rightTopRadius;
        invalidate();
    }

    public void setRightBottomRadius(float rightBottomRadius) {
        this.rightBottomRadius = rightBottomRadius;
        invalidate();
    }

    public float getLeftTopRadius() {
        return leftTopRadius;
    }

    public float getLeftBottomRadius() {
        return leftBottomRadius;
    }

    public float getRightTopRadius() {
        return rightTopRadius;
    }

    public float getRightBottomRadius() {
        return rightBottomRadius;
    }

    public String getJaText() {
        return (String) text;
    }

    public void setJaText(String text) {
        this.text = text;
        invalidate();
    }

    public int getJaTextColor() {
        return textColor;
    }

    public void setJaTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public float getJaTextSize() {
        return textSize;
    }

    public void setJaTextSize(float textSize) {
        this.textSize = textSize;
        invalidate();
    }

    public float getTextTop() {
        return textTop;
    }

    public void setJaTextTop(float textTop) {
        this.textTop = textTop;
        invalidate();
    }

    public float getJaTextLeft() {
        return textLeft;
    }

    public void setJaTextLeft(float textLeft) {
        this.textLeft = textLeft;
        invalidate();
    }

    public Drawable getIcon() {
        return iconDrawable;
    }

    public void setIcon(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
        invalidate();
    }

    public float getIconTop() {
        return iconTop;
    }

    public void setIconTop(float iconTop) {
        this.iconTop = iconTop;
        invalidate();
    }

    public float getIconLeft() {
        return iconLeft;
    }

    public void setIconLeft(float iconLeft) {
        this.iconLeft = iconLeft;
        invalidate();
    }

    public int getJaOrientation() {
        return orientation;
    }

    public void setJaOrientation(int orientation) {
        this.orientation = orientation;
        invalidate();
    }

    public int getJaGravity() {
        return gravity;
    }

    public void setJaGravity(int gravity) {
        this.gravity = gravity;
        invalidate();
    }

    public int getTextOrientation() {
        return textOrientation;
    }

    public void setTextOrientation(int textOrientation) {
        this.textOrientation = textOrientation;
        invalidate();
    }

    public Drawable getJaBackground() {
        return background;
    }

    public void setJaBackground(Drawable background) {
        this.background = background;
        invalidate();
    }

    public Drawable getPressBackground() {
        return pressBackground;
    }

    public void setPressBackground(Drawable pressBackground) {
        this.pressBackground = pressBackground;
        invalidate();
    }

    public boolean isJaSelected() {
        return selected;
    }

    public void setJaSelected(boolean selected) {
        this.selected = selected;
        invalidate();
    }

    public Drawable getSelectedBackground() {
        return selectedBackground;
    }

    public void setSelectedBackground(Drawable selectedBackground) {
        this.selectedBackground = selectedBackground;
        invalidate();
    }
}

