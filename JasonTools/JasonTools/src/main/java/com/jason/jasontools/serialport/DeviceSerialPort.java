package com.jason.jasontools.serialport;

import com.jason.jasontools.commandbus.IProtocol;
import com.jason.jasontools.util.LogUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 描述: 设备串口初始化类
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年08月16日
 */
public abstract class DeviceSerialPort {
    private final static String TAG = "JasonBaseSerialPortTag";
    private SerialPortUtil serialPortUtil = null;
    /**
     * 串口 接口监听<br>
     * 同一个串口注册多个监听，串口接受的结果会发送给每一个监听者
     */
    private IResultListener resultListener;

    private IVerifySerialProtocolData verifySerialProtocolData = null;
    private IParseSerialProtocol parseSerialProtocolData = null;

//    private ArrayList<Byte> cacheBytes = new ArrayList<>();

    /**
     * 串口名称
     */
    private String serialPortName;
    /**
     * 波特率
     */
    private int baudRate;
    /**
     * 停止位
     */
    private int flags;

    /**
     * 设置串口名称
     *
     * @return
     */
    public void setSerialPortName(String serialPortName) {
        this.serialPortName = serialPortName;
    }

    /**
     * 设置波特率
     *
     * @return
     */
    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    /**
     * 设置标志位
     *
     * @param flags
     */
    public void setFlags(int flags) {
        this.flags = flags;
    }


    /**
     * 打开串口接收器
     */
    public void open() {
        this.open(serialPortName, baudRate, flags);
    }

    /**
     * 打开串口接收器
     *
     * @param serialPortName 串口名称
     * @param baudRate       波特率
     * @param flags          标志位
     */
    public void open(String serialPortName, int baudRate, int flags) {
        if (serialPortName == null || serialPortName.length() == 0) {
            throw new RuntimeException("未设置串口号");
        }
        if (baudRate == 0) {
            throw new RuntimeException("未设置波特率");
        }
        try {
            this.serialPortUtil = new SerialPortUtil(serialPortName, baudRate, flags);
            this.serialPortUtil.registerListener(initSerialProtocolListener());
        } catch (Exception e) {
            LogUtil.e(TAG, "open serialPort error " + e.getMessage());
        }
    }


    /**
     * 注册监听器
     * 同一个串口可以有多个监听者，
     *
     * @param listener 监听器
     */
    public void registerListener(IResultListener listener) {
        this.resultListener = listener;
    }

    /**
     * 取消监听器
     */
    public void unregisterListener() {
        this.resultListener = null;
    }

    /**
     * 设置校验数据的接口
     *
     * @param verifySerialProtocolData 校验数据的接口
     */
    public void setVerifySerialProtocolData(IVerifySerialProtocolData verifySerialProtocolData) {
        this.verifySerialProtocolData = verifySerialProtocolData;
    }

    /**
     * 解析数据
     *
     * @param parseSerialProtocolData 解析数据的接口
     */
    public void setParseSerialProtocolData(IParseSerialProtocol parseSerialProtocolData) {
        this.parseSerialProtocolData = parseSerialProtocolData;
    }


    /**
     * 发送数据<br/>
     * 在发送数据之前如果{@link  #verifySerialProtocolData}不为空
     * 则会调用{@link IVerifySerialProtocolData#verifySendData(IProtocol, int)} 的方法进行校验数据
     * 校验数据规则由用户自行决定
     *
     * @param protocol 协议
     */
    public void sendData(IProtocol protocol) {
        try {
            if (this.verifySerialProtocolData != null) {
                protocol = verifySerialProtocolData.verifySendData(protocol, protocol.getProtocolLength());
            }
            serialPortUtil.sendCmd(protocol);
        } catch (VerifyFailedException e) {
            LogUtil.e(TAG, "协议验证失败，详细错误：\"+e.getMessage()+\" 发送的协议：\" + protocol.getProtocolStr()");
            if (resultListener != null) {
                resultListener.error("协议验证失败，详细错误：" + e.getMessage() + " 发送的协议：" + protocol.getProtocolStr());
            }
        } catch (IOException e) {
            LogUtil.e(TAG, "发送数据失败，IO流错误");
            if (resultListener != null)
                resultListener.error("发送数据失败，IO流错误");
        } catch (NullPointerException e) {
            LogUtil.e(TAG, "串口未打开");
            if (resultListener != null)
                resultListener.error("串口未打开");
        }
    }

    /**
     * 销毁
     */
    public void onDestroy() {
        unregisterListener();
        serialPortListener = null;
        if (serialPortUtil != null) {
            serialPortUtil.close();
        }
        serialPortUtil = null;
    }

    /**
     * 串口接收器
     */
    private ISerialPortListener serialPortListener;

    private ISerialPortListener initSerialProtocolListener() {
        return serialPortListener = new ISerialPortListener() {
            @Override
            public void onResponseData(byte[] data, int length) {
                IProtocol protocol = new IProtocol();
                protocol.setProtocol(Arrays.copyOf(data, length));
                try {
                    //验证数据是否合法
                    if (verifySerialProtocolData != null) {
                        protocol = verifySerialProtocolData.verifyReceiveData(protocol, protocol.getProtocolLength());
                    }
                    if (protocol == null) {
                        return;
                    }
                    // 经过协议解析拦截器进行解析
                    ResultData resultData = null;
                    if (parseSerialProtocolData != null) {
                        resultData = parseSerialProtocolData.dispatchParse(protocol, protocol.getProtocolLength());
                    }
                    if (resultData != null && resultListener != null) {
                        resultListener.onResult(resultData);
                    }
                } catch (VerifyFailedException e) {
                    LogUtil.e(TAG, "verifyData error " + e.getMessage());
                    resultListener.error("协议验证失败，详细错误：" + e.getMessage() + "接收到的协议：" + protocol.getProtocolStr());
                } catch (Exception e) {
                    LogUtil.e(TAG, "parseData error " + e.getMessage());
                    resultListener.error("详细错误：" + e.getMessage() + "接收到的协议：" + protocol.getProtocolStr());
                }
            }
        };
    }
}
