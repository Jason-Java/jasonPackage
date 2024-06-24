package com.jason.jasonuitools.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年05月22日
 */
public class ShadowDrawable extends Drawable {
    private Paint mShadowPaint;
    private Paint mBgPaint;
    private int mShadowRadius;
    private int mOffsetLeft;
    private int mOffsetRight;
    private int mOffsetTop;
    private int mOffsetBottom;
    private int mBgColor[];
    private RectF mShadowRect;
    private RectF mShapeRect;
    private RectF mStrokeRect;
    private Path mShadowPath;
    private Path mShapePath;
    private int mShadowColor;

    private float mLeftTopRadius;
    private float mLeftBottomRadius;
    private float mRightTopRadius;
    private float mRightBottomRadius;

    // 边框相关
    private float strokeWidth = 0;
    protected int strokeColor;

    private ShadowDrawable() {

    }

    private ShadowDrawable(float leftBottomRadius
            , float leftTopRadius
            , float rightTopRadius
            , float rightBottomRadius
            , int[] bgColor
            , int shadowColor
            , int shadowRadius
            , int offsetLeft
            , int offsetRight, int offsetTop, int offsetBottom) {
        this.mBgColor = bgColor;
        this.mLeftTopRadius = leftTopRadius;
        this.mLeftBottomRadius = leftBottomRadius;
        this.mRightTopRadius = rightTopRadius;
        this.mRightBottomRadius = rightBottomRadius;
        this.mShadowRadius = shadowRadius;
        this.mOffsetLeft = offsetLeft;
        this.mOffsetRight = offsetRight;
        this.mOffsetTop = offsetTop;
        this.mOffsetBottom = offsetBottom;
        this.mShadowColor=shadowColor;


        this.mShadowPath = new Path();
        this.mShapePath = new Path();

        mShadowPaint = new Paint();


        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mShadowRect = new RectF(left + mShadowRadius-mOffsetLeft
                , top + mShadowRadius - mOffsetTop
                , right - mShadowRadius - mOffsetRight
                , bottom - mShadowRadius - mOffsetBottom);

        mShapeRect = new RectF(left + mShadowRadius - mOffsetLeft
                , top + mShadowRadius - mOffsetTop
                , right - mShadowRadius - mOffsetRight
                , bottom - mShadowRadius - mOffsetBottom);

        mStrokeRect= new RectF(left + mShadowRadius - mOffsetLeft + (strokeWidth / 2)
                , top + mShadowRadius - mOffsetTop + (strokeWidth / 2)
                , right - mShadowRadius - mOffsetRight - (strokeWidth / 2),
                bottom - mShadowRadius - mOffsetBottom - (strokeWidth / 2));
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        drawShadow(canvas);
        drawBackground(canvas);
        drawStroke(canvas);
    }


    /**
     * 绘制阴影
     */
    private void drawShadow(Canvas canvas) {
        mShadowPath.reset();
        mShadowPaint.setColor(Color.TRANSPARENT);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setStyle(Paint.Style.FILL);
        mShadowPaint.setShadowLayer(mShadowRadius, mOffsetBottom,mOffsetRight, mShadowColor);
        float[] outRadio = {mLeftTopRadius, mLeftTopRadius, mRightTopRadius, mRightTopRadius, mRightBottomRadius, mRightBottomRadius, mLeftBottomRadius, mLeftBottomRadius};
        mShadowPath.addRoundRect(mShadowRect, outRadio, Path.Direction.CW);
        canvas.drawPath(mShadowPath, mShadowPaint);
    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private void drawBackground(Canvas canvas) {
        if (mBgColor != null) {
            if (mBgColor.length == 1) {
                mBgPaint.setColor(mBgColor[0]);
            } else {
                mBgPaint.setShader(new LinearGradient(mShadowRect.left, mShadowRect.height() / 2, mShadowRect.right,
                        mShadowRect.height() / 2, mBgColor, null, Shader.TileMode.CLAMP));
            }
        }
        mBgPaint.setStyle(Paint.Style.FILL);
        mShapePath.reset();
        float[] outRadio = {mLeftTopRadius, mLeftTopRadius, mRightTopRadius, mRightTopRadius, mRightBottomRadius, mRightBottomRadius, mLeftBottomRadius, mLeftBottomRadius};
        mShapePath.addRoundRect(mShapeRect, outRadio, Path.Direction.CW);
        canvas.drawPath(mShapePath, mBgPaint);
    }

    /**
     * 绘制边框
     *
     * @param canvas
     */
    private void drawStroke(Canvas canvas) {
        if (strokeWidth == 0) {
            return;
        }
        mBgPaint.setColor(strokeColor);
        mBgPaint.setStrokeWidth(strokeWidth);
        mBgPaint.setStyle(Paint.Style.STROKE);
        mBgPaint.setAntiAlias(true);
        mShadowPaint.reset();
        float[] outRadio = {mLeftTopRadius, mLeftTopRadius, mRightTopRadius, mRightTopRadius, mRightBottomRadius, mRightBottomRadius, mLeftBottomRadius, mLeftBottomRadius};
        mShapePath.addRoundRect(mStrokeRect, outRadio, Path.Direction.CW);
        canvas.drawPath(mShapePath, mBgPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mShadowPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mShadowPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public static void setShadowDrawable(View view, Drawable drawable) {
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }

    /**
     * 为指定View添加阴影
     *
     * @param view         目标View
     * @param shapeRadius  View的圆角
     * @param shadowColor  阴影的颜色
     * @param shadowRadius 阴影的宽度
     * @param offsetRight  阴影水平方向的偏移量
     * @param offsetBottom 阴影垂直方向的偏移量
     */
    public static void setShadowDrawable(View view, int shapeRadius, int shadowColor, int shadowRadius, int offsetRight, int offsetBottom) {
        ShadowDrawable drawable = new ShadowDrawable.Builder()
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetBottom(offsetBottom)
                .setOffsetRight(offsetRight)
                .builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }

    /**
     * 为指定View设置带阴影的背景
     *
     * @param view         目标View
     * @param bgColor      View背景色
     * @param shapeRadius  View的圆角
     * @param shadowColor  阴影的颜色
     * @param shadowRadius 阴影的宽度
     * @param offsetRight  阴影水平方向的偏移量
     * @param offsetBottom 阴影垂直方向的偏移量
     */
    public static void setShadowDrawable(View view, int bgColor, int shapeRadius, int shadowColor, int shadowRadius, int offsetRight, int offsetBottom) {
        ShadowDrawable drawable = new ShadowDrawable.Builder()
                .setBgColor(bgColor)
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetBottom(offsetBottom)
                .setOffsetRight(offsetRight)
                .builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }


    /**
     * 为指定View设置带阴影的渐变背景
     *
     * @param view
     * @param bgColor
     * @param shapeRadius
     * @param shadowColor
     * @param shadowRadius
     * @param offsetRight  阴影水平方向的偏移量
     * @param offsetBottom 阴影垂直方向的偏移量
     */
    public static void setShadowDrawable(View view, int[] bgColor, int shapeRadius, int shadowColor, int shadowRadius, int offsetRight, int offsetBottom) {
        ShadowDrawable drawable = new ShadowDrawable.Builder()
                .setBgColor(bgColor)
                .setShapeRadius(shapeRadius)
                .setShadowColor(shadowColor)
                .setShadowRadius(shadowRadius)
                .setOffsetBottom(offsetBottom)
                .setOffsetRight(offsetRight)
                .builder();
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        ViewCompat.setBackground(view, drawable);
    }

    public static class Builder {
        private int mShapeRadius;
        private int mShadowColor;
        private int mShadowRadius;

        private int mOffsetLeft;
        private int mOffsetRight;
        private int mOffsetTop;
        private int mOffsetBottom;

        private int[] mBgColor;
        private float mLeftTopRadius;
        private float mLeftBottomRadius;
        private float mRightTopRadius;
        private float mRightBottomRadius;

        private float strokeWidth;
        protected int strokeColor;

        public Builder() {
            mShapeRadius = 12;
            mShadowColor = Color.parseColor("#4d000000");
            mShadowRadius = 18;
            mOffsetLeft = 0;
            mOffsetRight = 0;
            mOffsetTop = 0;
            mOffsetBottom = 0;
            mBgColor = new int[1];
            mBgColor[0] = Color.TRANSPARENT;
            mLeftBottomRadius = 0;
            mLeftTopRadius = 0;
            mRightBottomRadius = 0;
            mRightTopRadius = 0;
            strokeWidth = 0;
            strokeColor = 0;
        }

        public Builder setLeftTopRadius(float mLeftTopRadius) {
            this.mLeftTopRadius = mLeftTopRadius;
            return this;
        }

        public Builder setLeftBottomRadius(float mLeftBottomRadius) {
            this.mLeftBottomRadius = mLeftBottomRadius;
            return this;
        }

        public Builder setRightTopRadius(float mRightTopRadius) {
            this.mRightTopRadius = mRightTopRadius;
            return this;
        }

        public Builder setRightBottomRadius(float mRightBottomRadius) {
            this.mRightBottomRadius = mRightBottomRadius;
            return this;
        }

        public Builder setRadius(float radius) {
            this.mLeftTopRadius = radius;
            this.mLeftBottomRadius = radius;
            this.mRightTopRadius = radius;
            this.mRightBottomRadius = radius;
            return this;
        }

        public Builder setShapeRadius(int ShapeRadius) {
            this.mShapeRadius = ShapeRadius;
            return this;
        }

        public Builder setShadowColor(int shadowColor) {
            this.mShadowColor = shadowColor;
            return this;
        }

        public Builder setShadowRadius(int shadowRadius) {
            this.mShadowRadius = shadowRadius;
            return this;
        }

        public Builder setOffsetLeft(int offsetLeft) {
            this.mOffsetLeft = offsetLeft;
            return this;
        }

        public Builder setOffsetRight(int offsetRight) {
            this.mOffsetRight = offsetRight;
            return this;
        }

        public Builder setOffsetTop(int offsetTop) {
            this.mOffsetTop = offsetTop;
            return this;
        }

        public Builder setOffsetBottom(int offsetBottom) {
            this.mOffsetBottom = offsetBottom;
            return this;
        }

        public Builder setBgColor(int BgColor) {
            this.mBgColor[0] = BgColor;
            return this;
        }

        public Builder setBgColor(int[] BgColor) {
            this.mBgColor = BgColor;
            return this;
        }

        public Builder setStrokeWidth(float strokeWidth) {
            this.strokeWidth = strokeWidth;
            return this;
        }

        public Builder setStrokeColor(int strokeColor) {
            this.strokeColor = strokeColor;
            return this;
        }

        public ShadowDrawable builder() {
            ShadowDrawable drable = new ShadowDrawable(mLeftBottomRadius, mLeftTopRadius, mRightTopRadius, mRightBottomRadius, mBgColor, mShadowColor, mShadowRadius,mOffsetLeft,mOffsetRight,mOffsetTop,mOffsetBottom);
            drable.strokeWidth = this.strokeWidth;
            drable.strokeColor = this.strokeColor;
            return drable;

        }

    }
}
