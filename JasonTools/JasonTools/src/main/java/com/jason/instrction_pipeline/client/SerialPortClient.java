package com.jason.instrction_pipeline.client;

import com.jason.jasontools.commandbus.IProtocol;
import com.jason.jasontools.serialport.AbsVerifySerialProtocolData;
import com.jason.jasontools.serialport.IParseSerialProtocol;
import com.jason.jasontools.serialport.IResultListener;
import com.jason.jasontools.serialport.ISerialPortListener;
import com.jason.jasontools.serialport.ResultData;
import com.jason.jasontools.serialport.SerialPortUtil;
import com.jason.jasontools.serialport.VerifyFailedException;
import com.jason.jasontools.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import android_serialport_api.JasonSerialPort;

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
public class SerialPortClient implements IClient {
    private final static String TAG = "JasonBaseSerialPortTag";
    private JasonSerialPort serialPort;
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
     *
     * @param serialPortName 串口名
     * @param baudRate 波特率
     * @param flags 停止为
     */
    public SerialPortClient(String serialPortName, int baudRate, int flags) {
        this.serialPortName = serialPortName;
        this.baudRate = baudRate;
        this.flags = flags;
    }


    @Override
    public IClient openClient() throws SecurityException, IOException {
        this.serialPort = new JasonSerialPort(new File(serialPortName), baudRate, flags);
        LogUtil.i(TAG, "SerialPort " + this.serialPortName + " open success ");
        return this;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return serialPort.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return serialPort.getOutputStream();
    }

    @Override
    public void closeClient() {
        if (serialPort != null) {
             try {
                serialPort.onDestory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public String getClientTAG() {
        return "SerialPort " + serialPortName + "  ";
    }
}
