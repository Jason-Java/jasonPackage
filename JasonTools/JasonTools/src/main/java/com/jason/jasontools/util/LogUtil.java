package com.jason.jasontools.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年08月17日
 */
public class LogUtil {
    private static String className;//类名
    private static String methodName;//方法名
    private static int lineNumber;//行数
    private static boolean isPrint =true;//是否打印
    private static String TAG = "Jason";
    //过滤标签
    public static List<String> filterTag=new ArrayList<>();

    /**
     * 判断是否可以调试
     *
     * @return
     */
    private void setIsPrint(boolean isPrint) {
        this.isPrint = isPrint;
    }

    //打印格式
    private static String createLog(String log) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("-->");
        buffer.append(methodName);
        buffer.append("(").append(className).append(":").append(lineNumber).append(") :");
        buffer.append(log);
        return buffer.toString();
    }

    /**
     * 获取文件名、方法名、所在行数
     *
     * @param sElements
     */
    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    /**
     * 添加过滤标签
     * @param tag 标签
     */
    public static void addFilterTag(String... tag) {
        filterTag.addAll(Arrays.asList(tag));
    }

    /**
     * 过滤不打印的标签
     * 如果标签在过滤列表中，则不打印
     * @param tag 标签
     * @return
     */
    public static boolean filterNotPrintTag(String tag) {
        for (String tg : filterTag){
           if(tg.equals(tag)){
               return true;
           }
        }
        return false;
    }

    public static void e(String message) {
        e(TAG, message);
    }

    public static void e(String tag, String message) {
        if (!isPrint || filterNotPrintTag(tag))
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.e(tag , createLog(message));
    }

    public static void i(String message) {
        i(TAG, message);
    }

    public static void i(String tag, String message) {
        if (!isPrint || filterNotPrintTag(tag))
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.i(tag, createLog(message));
    }

    public static void d(String message) {
        d(TAG, message);
    }

    public static void d(String tag, String message) {
        if (!isPrint || filterNotPrintTag(tag))
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.d(tag , createLog(message));
    }

    public static void v(String message) {
        v(TAG, message);
    }

    public static void v(String tag, String message) {
        if (!isPrint || filterNotPrintTag(tag))
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.v(tag , createLog(message));
    }

    public static void w(String message) {
        w(TAG, message);
    }

    public static void w(String tag, String message) {
        if (isPrint || filterNotPrintTag(tag))
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.w(tag , createLog(message));
    }

}

