package com.jason.jasontools.serialport;

import com.jason.jasontools.commandbus.IProtocol;
import com.jason.jasontools.util.LogUtil;

/**
 * <p>
 * 描述: 结果接受监听接口
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年08月17日
 */
public abstract class IResultListener<T> {
    private String TAG = "DEFAULT_TAG";

    public abstract void onResult(T protocol);

    public void error(String msg) {
        LogUtil.e(TAG, "SerialPort " + getTAG() + " error: " + msg);
    }

    public void setTAG(String tag) {
        TAG = tag;
    }

    public String getTAG() {
        return TAG;
    }
}
