package com.jason.jasonuitools.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.security.cert.PolicyNode;

public class DensityUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param dpValue
     * @return
     */
    public static int dp2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param pxValue
     * @return
     */
    public static int px2dp(float pxValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 根据手机分辨率从px单位 转化为 sp
     *
     * @param pxValue
     * @return
     */
    public static float px2sp(float pxValue) {
        return pxValue / Resources.getSystem().getDisplayMetrics().scaledDensity;
    }

    /**
     * 根据手机分辨率从sp转化为px
     *
     * @param spValue
     * @return
     */
    public static float sp2px(float spValue) {
        return spValue * Resources.getSystem().getDisplayMetrics().scaledDensity;
    }

    /**
     * 获取屏幕的宽和高
     *
     * @param activity
     * @return Point.X表示宽度Point.Y表示高度
     */
    public static Point getScreenWidthAndHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        // 方法一(推荐使用)使用Point来保存屏幕宽、高两个数据
        Point outSize = new Point();
        // 通过Display对象获取屏幕宽、高数据并保存到Point对象中
        display.getSize(outSize);
        return outSize;
    }

    /**
     * 获取屏幕的宽和高
     *
     * @param context
     * @return point x 屏幕的宽度，y屏幕的高度
     */
    public static Point getScreenWidthAndHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= 17) {
            windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        } else {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;
        Point point = new Point();
        point.x = widthPixels;
        point.y = heightPixels;
        return point;
    }


}
