package com.jason.jasontools.serialport;

import com.jason.jasontools.commandbus.IProtocol;
import com.jason.jasontools.util.JasonThreadPool;
import com.jason.jasontools.util.LogUtil;
import com.jason.jasontools.util.StrUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android_serialport_api.JasonSerialPort;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年08月16日
 */
public final class SerialPortUtil {
    private final static String TAG = "JasonBaseSerialPortTag";

    private JasonSerialPort serialPort = null;
    private ISerialPortListener listener = null;
    private String serialPortName;
    private ReadSerialPortDataRunnable readSerialPortDataRunnable = null;
    private Lock lock = new ReentrantLock(true);
    private Condition condition = lock.newCondition();


    protected SerialPortUtil(String serialPortName, int baudrate) throws SecurityException, IOException {
        this(serialPortName, baudrate, 0);
    }

    /**
     * 初始化串口
     *
     * @param serialPortName
     * @param baudrate
     */
    protected SerialPortUtil(String serialPortName, int baudrate, int flags) throws SecurityException, IOException {
        this.serialPortName = serialPortName;
        this.serialPort = new JasonSerialPort(new File(serialPortName), baudrate, flags);
        if (readSerialPortDataRunnable == null) {
            JasonThreadPool.getInstance().execute(readSerialPortDataRunnable = new ReadSerialPortDataRunnable(serialPort, serialPortName));
        }
        LogUtil.i(TAG, "SerialPort " + this.serialPortName + " open success ");
    }


    protected void registerListener(ISerialPortListener listener) {
        this.listener = listener;
        if (readSerialPortDataRunnable != null)
            readSerialPortDataRunnable.setSerialPortListener(listener);
    }

    /**
     * 发送串口指令
     *
     * @param protocol
     */
    protected void sendCmd(IProtocol protocol) throws NullPointerException, IOException {
        if (this.serialPort == null) {
            LogUtil.e(TAG, "SerialPort " + serialPortName + " sendCmd error: serialPort is null");
            throw new NullPointerException("serialPort is null");
        }
        lock.lock();
        try {
            this.serialPort.getOutputStream().write(protocol.getProtocol());
            this.serialPort.getOutputStream().flush();
            LogUtil.i(TAG, "SerialPort " + serialPortName + " 发送成功 [  length:  " + protocol.getProtocolLength() + "  content: " + protocol.getProtocolStr() + "  ]");
        } finally {
            lock.unlock();
        }
    }


    /**
     * 关闭串口
     */
    protected void close() {
        if (serialPort != null) {
            try {
                serialPort.onDestory();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (readSerialPortDataRunnable != null) {
                readSerialPortDataRunnable.stop();
            }
            readSerialPortDataRunnable = null;
            listener = null;
            serialPort = null;
        }
    }
}
