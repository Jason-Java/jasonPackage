package com.jason.jasontools.serialport;

public interface ISerialPortListener {
    /**
     * 串口响应数据
     * @param data 响应数据
     * @param length 数据长度
     */
    void onResponseData(byte[] data,int length);




}
