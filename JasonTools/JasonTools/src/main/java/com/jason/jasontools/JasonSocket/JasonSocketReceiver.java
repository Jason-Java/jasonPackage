package com.jason.jasontools.JasonSocket;

import com.jason.jasontools.commandbus.IProtocol;
import com.jason.jasontools.serialport.AbsVerifySerialProtocolData;
import com.jason.jasontools.serialport.IParseSerialProtocol;
import com.jason.jasontools.serialport.IResultListener;
import com.jason.jasontools.serialport.ResultData;
import com.jason.jasontools.serialport.VerifyFailedException;
import com.jason.jasontools.util.EErrorNumber;
import com.jason.jasontools.util.LogUtil;
import com.jason.jasontools.util.StrUtil;

import java.util.Arrays;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年12月10日
 */
public class JasonSocketReceiver implements Runnable {
    private final static String TAG = JasonSocketReceiver.class.getSimpleName();
    private JasonSocketClient jasonSocketClient;

    private AbsVerifySerialProtocolData verifySerialProtocolData = null;
    private IParseSerialProtocol parseSerialProtocolData = null;
    private IResultListener resultListener;
    private boolean isRun = false;

    public JasonSocketReceiver() {
        LogUtil.i(TAG + "接受数据线程已准备就绪，请调用 starReceiverData() 方法启动线程");
    }

    public JasonSocketReceiver setSocketClient(JasonSocketClient jasonSocketClient) {
        this.jasonSocketClient = jasonSocketClient;
        return this;
    }

    /**
     * 设置数据验证抽象类
     *
     * @param verifySerialProtocolData
     */
    public JasonSocketReceiver setVerifySerialProtocolData(AbsVerifySerialProtocolData verifySerialProtocolData) {
        this.verifySerialProtocolData = verifySerialProtocolData;
        return this;
    }

    public JasonSocketReceiver setResultListener(IResultListener listener) {
        this.resultListener = listener;
        return this;
    }

    public JasonSocketReceiver starReceiverData() {
        isRun = true;
        new Thread(this).start();
        LogUtil.i(TAG + "接受数据线程已启动！");
        return this;
    }

    /**
     * 解析数据
     *
     * @param parseSerialProtocolData 解析数据的接口
     */
    public JasonSocketReceiver setParseSerialProtocolData(IParseSerialProtocol parseSerialProtocolData) {
        this.parseSerialProtocolData = parseSerialProtocolData;
        return this;
    }

    @Override
    public void run() {
        if (jasonSocketClient == null) {
            LogUtil.e("Jason", "JasonSocketClient  is null");
            return;
        }
        if (this.jasonSocketClient.getSocket() == null) {
            LogUtil.e("Jason", "Socket  is null");
            return;
        }
        if (this.jasonSocketClient.getSocket().isClosed()) {
            LogUtil.e("Jason", "Socket 已经关闭，无法开启数据接受线程");
            return;
        }
        if (!this.jasonSocketClient.getSocket().isConnected()) {
            LogUtil.e("Jason", "socket 为正确建立连接，无法开启数据接受线程");
            return;
        }
        byte[] buffer = new byte[4096];
        while (isRun) {
            if (this.jasonSocketClient.getSocket() == null) {
                break;
            }
            if (this.jasonSocketClient.getSocket().isClosed()) {
                LogUtil.e(TAG, "Socket 已经关闭，数据接受线程已经自动关闭");
                break;
            }
            if (!this.jasonSocketClient.getSocket().isConnected()) {
                LogUtil.e(TAG, "socket 为正确建立连接，数据接受线程已自动关闭");
                break;
            }
            try {
                int size = this.jasonSocketClient.getSocket().getInputStream().read(buffer);
                // 远程服务器已经关闭socket，本地也同步关闭
                if (size < 0) {
                    LogUtil.i(TAG, "远程Socket服务已主动断开连接");
                    this.jasonSocketClient.close();
                    break;
                }
                if (size > 0) {
                    byte[] data = new byte[size];
                    System.arraycopy(buffer, 0, data, 0, size);
                    LogUtil.i(TAG, "SerialPort " + jasonSocketClient + " 接受成功 [  length:  " + data.length + "  content: " + StrUtil.byteToHexString(data) + "  ]");
                    onResponseData(data, data.length);
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "socket 接受数据线程异常结束  " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void onResponseData(byte[] data, int length) {
        IProtocol protocol = new IProtocol();
        protocol.setProtocol(Arrays.copyOf(data, length));
        try {
            //验证数据是否合法
            if (verifySerialProtocolData != null) {
                protocol = verifySerialProtocolData.verifyReceiveData(protocol, protocol.getProtocolLength());
            } else {
                protocol = new IProtocol();
                protocol.setProtocol(data);
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
            resultListener.error("协议验证失败，详细错误：" + e.getMessage() + "接收到的协议：" + protocol.getProtocolStr(), EErrorNumber.AUTHFAILED.getCode());
        } catch (Exception e) {
            LogUtil.e(TAG, "parseData error " + e.getMessage());
            resultListener.error("详细错误：" + e.getMessage() + "接收到的协议：" + protocol.getProtocolStr(),EErrorNumber.AUTHFAILED.getCode());
        }
    }



    /**
     * 销毁实例，释放资源
     */
    public void onDestroy() {
        isRun = false;
        resultListener = null;
        parseSerialProtocolData = null;
        verifySerialProtocolData = null;
        jasonSocketClient = null;

    }

}
