package com.jason.jasontools.serialport;

import com.jason.jasontools.commandbus.IProtocol;
import com.jason.jasontools.util.LogUtil;

import java.io.IOException;
import java.util.ArrayList;
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
    private Map<String, IResultListener> listenerMap = new HashMap<>();

    private IVerifySerialProtocolData verifySerialProtocolData = null;
    private IParseSerialProtocol parseSerialProtocolData = null;

    private ArrayList<Byte> cacheBytes = new ArrayList<>();

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
     * 获取串口名称
     *
     * @return
     */
    public void setSerialPortName(String serialPortName) {
        this.serialPortName = serialPortName;
    }

    /**
     * 获取波特率
     *
     * @return
     */
    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

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
     * @param baudrate       波特率
     * @param flags          标志位
     */
    public void open(String serialPortName, int baudrate, int flags) {
        this.serialPortName = serialPortName;
        this.baudRate = baudrate;
        this.flags = flags;
        if (serialPortName == null || serialPortName.length() == 0) {
            throw new RuntimeException("为设置串口号");
        }
        if (baudRate == 0) {
            throw new RuntimeException("未设置波特率");
        }
        try {
            this.serialPortUtil = new SerialPortUtil(serialPortName, baudrate, flags);
            this.serialPortUtil.registerListener(initSerialProtocolListener());
        } catch (Exception e) {
            LogUtil.e(TAG, "open serialPort error " + e.getMessage());
        }
    }

    /**
     * 注册监听器
     * 同一个串口可以有多个监听者
     *
     * @param tag      监听器标识唯一标识,如果已经存在则会覆盖
     * @param listener
     */
    public void registerListener(String tag, IResultListener listener) {
        listenerMap.put(tag, listener);
    }

    /**
     * 注册监听器
     * 同一个串口可以有多个监听者，
     *
     * @param listener 监听器
     */
    public void registerListener(IResultListener listener) {
        registerListener(listener.getTAG(), listener);
    }

    /**
     * 取消监听器
     *
     * @param tag 监听器标识唯一标识
     */
    public void unregisterListener(String tag) {
        listenerMap.remove(tag);
    }

    /**
     * 取消监听器
     *
     * @param listener 监听器
     */
    public void unregisterListener(IResultListener listener) {
        unregisterListener(listener.getTAG());
    }

    public void unregisterAllListener() {
        listenerMap.clear();
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
     * 发送数据
     *
     * @param protocol 协议
     */
    public void sendData(IProtocol protocol) {
        sendData((IProtocol) protocol.clone(), null);
    }


    /**
     * 发送数据<br/>
     * 在发送数据之前如果{@link  #verifySerialProtocolData}不为空
     * 则会调用{@link IVerifySerialProtocolData#verifySendData(IProtocol, int)} 的方法进行校验数据
     * 校验数据规则由用户自行决定
     *
     * @param protocol    协议
     * @param listenerTag 监听器标识唯一标识
     */
    public void sendData(IProtocol protocol, String listenerTag) {
        //清除缓存
        cacheBytes.clear();
        IResultListener iResultListener = listenerMap.get(listenerTag);
        try {
            if (this.verifySerialProtocolData != null) {
                protocol = verifySerialProtocolData.verifySendData(protocol, protocol.getProtocolLength());
            }
            serialPortUtil.sendCmd(protocol);
        } catch (VerifyFailedException e) {
            LogUtil.e(TAG, "协议验证失败，详细错误：\"+e.getMessage()+\" 发送的协议：\" + protocol.getProtocolStr()");
            if (iResultListener != null) {
                iResultListener.error("协议验证失败，详细错误：" + e.getMessage() + " 发送的协议：" + protocol.getProtocolStr());
            }
        } catch (IOException e) {
            LogUtil.e(TAG, "发送数据失败，IO流错误");
            if (iResultListener != null)
                iResultListener.error("发送数据失败，IO流错误");
        } catch (NullPointerException e) {
            LogUtil.e(TAG, "串口未打开");
            if (iResultListener != null)
                iResultListener.error("串口未打开");
        }
    }

    /**
     * 销毁
     */
    public void onDestroy() {
        unregisterAllListener();
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
                // 存储缓存
                for (int i = 0; i < length; i++) {
                    cacheBytes.add(data[i]);
                }
                IProtocol protocol = new IProtocol();
                protocol.setProtocol(cacheBytes);
                try {
                    if (verifySerialProtocolData != null)
                        //验证数据是否合法，合法
                        protocol = verifySerialProtocolData.verifyReceiveData(protocol, protocol.getProtocolLength());
                    if (protocol == null) return;
                    ResultData resultData = null;
                    if (parseSerialProtocolData != null) {
                        resultData = parseSerialProtocolData.parseData(protocol, protocol.getProtocolLength());
                    }
                    for (IResultListener listener : listenerMap.values())
                        if (resultData != null) {
                            listener.onResult(resultData);
                        }
                    //清除缓存
                    cacheBytes.clear();
                } catch (VerifyFailedException e) {
                    LogUtil.e(TAG, "verifyData error " + e.getMessage());
                    for (IResultListener li :
                            listenerMap.values()) {
                        li.error("协议验证失败，详细错误：" + e.getMessage() + "接收到的协议：" + protocol.getProtocolStr());
                    }
                    //清除缓存
                    cacheBytes.clear();
                } catch (Exception e) {
                    LogUtil.e(TAG, "parseData error " + e.getMessage());
                    for (IResultListener li :
                            listenerMap.values()) {
                        li.error("详细错误：" + e.getMessage() + "接收到的协议：" + protocol.getProtocolStr());
                    }
                }
            }
        };
    }
}
