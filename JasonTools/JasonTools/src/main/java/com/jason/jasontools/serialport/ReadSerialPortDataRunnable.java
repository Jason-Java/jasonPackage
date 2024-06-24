package com.jason.jasontools.serialport;

import com.jason.jasontools.util.LogUtil;
import com.jason.jasontools.util.StrUtil;

import android_serialport_api.JasonSerialPort;

/**
 * <p>
 * 描述: 读取串口返回的数据
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年08月16日
 */
public class ReadSerialPortDataRunnable implements Runnable {
    private final static String TAG = "JasonBaseSerialPortTag";
    private JasonSerialPort serialPort = null;
    private ISerialPortListener listener = null;
    private boolean isRun = true;
    private String serialPortName;


    public ReadSerialPortDataRunnable(JasonSerialPort serialPort, String serialPortName) {
        this.serialPort = serialPort;
        this.serialPortName = serialPortName;
    }

    public void setSerialPortListener(ISerialPortListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        while (isRun) {
            try {
                if (this.serialPort == null) {
                    LogUtil.e(TAG, "SerialPort " + serialPortName + " readData error: serialPort is null");
                    break;
                }
                byte[] buffer = new byte[1024];
                int size = this.serialPort.getInputStream().read(buffer);
                if (size > 0) {
                    byte[] data = new byte[size];
                    System.arraycopy(buffer, 0, data, 0, size);
                    LogUtil.i(TAG, "SerialPort " + serialPortName + " 接受成功 [  length:  " + data.length + "  content: " + StrUtil.byteToHexString(data) + "  ]");
                    if (this.listener != null)
                        listener.onResponseData(data, data.length);
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "SerialPort " + serialPortName + " readData error: " + e.getMessage());
                e.printStackTrace();
            }
        }
        LogUtil.e(TAG, "串口读取线程异常结束");
    }

    /**
     * 停止读取串口数据
     */
    public void stop() {
        isRun = false;
        LogUtil.i(TAG, "SerialPort " + serialPortName + " stop success " + serialPortName);
    }
}
