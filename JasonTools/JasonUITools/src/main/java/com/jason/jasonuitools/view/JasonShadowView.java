package com.jason.jasonuitools.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
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
 * @createTime 2024年05月22日
 */
public class JasonShadowView extends View{
    private Paint mShadowPaint;
    private Paint mBgPaint;
    private int mShadowRadius;
    private int mShape;
    private int mShapeRadius;
    private int mOffsetX;
    private int mOffsetY;
    private int mBgColor[];
    private RectF mRect;
    private Path mPath;

    public final static int SHAPE_ROUND = 1;
    public final static int SHAPE_CIRCLE = 2;


    public JasonShadowView(Context context) {
        super(context);
        init();
    }

    public JasonShadowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JasonShadowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public JasonShadowView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        this.mBgColor = new int[]{0x123456};
        this.mShapeRadius = 10;
        this.mShadowRadius = 10;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mPath = new Path();

        mShadowPaint = new Paint();
        mShadowPaint.setColor(Color.TRANSPARENT);
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setShadowLayer(mShadowRadius, mOffsetX, mOffsetY, 0x992979FF);
        mShadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
    }

    

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mRect = new RectF(0 + mShadowRadius - mOffsetX, 0 + mShadowRadius - mOffsetY, getMeasuredWidth() - mShadowRadius - mOffsetX,
                getMeasuredHeight() - mShadowRadius - mOffsetY);
        if (mBgColor != null) {
            if (mBgColor.length == 1) {
                mBgPaint.setColor(mBgColor[0]);
            } else {
                mBgPaint.setShader(new LinearGradient(mRect.left, mRect.height() / 2, mRect.right,
                        mRect.height() / 2, mBgColor, null, Shader.TileMode.CLAMP));
            }
        }
        mPath = new Path();
        mPath.reset();
        // 绘制背景色
        float[] outRadio = {0, 0, 10, 10, 0, 0, 0, 0};
        mPath.addRoundRect(mRect, outRadio, Path.Direction.CW);
        canvas.drawPath(mPath, mShadowPaint);
        canvas.drawPath(mPath, mBgPaint);
    }
}
