package com.jason.jasontools.JasonSocket;

import com.jason.jasontools.commandbus.IProtocol;
import com.jason.jasontools.serialport.AbsVerifySerialProtocolData;
import com.jason.jasontools.serialport.IResultListener;
import com.jason.jasontools.serialport.VerifyFailedException;
import com.jason.jasontools.util.EErrorNumber;
import com.jason.jasontools.util.LogUtil;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>
 * 描述: socket的数据发送中心
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年12月10日
 */
public class JasonSocketSender {
    private final static String TAG = JasonSocketClient.class.getSimpleName();
    private JasonSocketClient client;
    private AbsVerifySerialProtocolData verifySerialProtocolData = null;
    private IResultListener resultListener;

    public JasonSocketSender setSocketClient(JasonSocketClient client) {
        this.client = client;
        return this;
    }

    /**
     * 设置数据验证抽象类
     *
     * @param verifySerialProtocolData
     */
    public JasonSocketSender setVerifySerialProtocolData(AbsVerifySerialProtocolData verifySerialProtocolData) {
        this.verifySerialProtocolData = verifySerialProtocolData;
        return this;
    }

    public JasonSocketSender setResultListener(IResultListener listener) {
        this.resultListener = listener;
        return this;
    }


    /**
     * 发送数据
     *
     * @param protocol
     */
    public synchronized void sendData(IProtocol protocol) {
        if (client == null) {
            if (resultListener != null) {
                resultListener.error("socket client 为空！", EErrorNumber.NULLPOINTER.getCode());
            }
            LogUtil.e(TAG, "socket client 为空！");
            return;
        }
        if (client.getSocket() == null) {
            if (resultListener != null) {
                resultListener.error("socket 已关闭连接，请检查", EErrorNumber.WRITETIMEOUT.getCode());
            }
            LogUtil.e(TAG, "socket 已关闭连接，请检查");
            return;
        }
        if (client.getSocket().isClosed() && !client.getSocket().isConnected()) {
            if (resultListener != null) {
                resultListener.error("socket 未建立连接，请检查", EErrorNumber.WRITETIMEOUT.getCode());
            }
            LogUtil.e(TAG, "socket 未建立连接，请检查");
            return;
        }
        try {
            if (this.verifySerialProtocolData != null) {
                protocol = verifySerialProtocolData.verifySendData(protocol, protocol.getProtocolLength());
            }
            OutputStream outputStream = client.getSocket().getOutputStream();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            outputStream.write(protocol.getProtocol());
            outputStream.flush();
            LogUtil.i("JasonSocketSender" + "发送数据成功，协议：" + protocol.getProtocolStr());
        } catch (VerifyFailedException e) {
            LogUtil.e(TAG, "协议验证失败，详细错误：" + e.getMessage() + " 发送的协议：" + protocol.getProtocolStr());
            if (resultListener != null) {
                resultListener.error("协议验证失败，详细错误：" + e.getMessage() + " 发送的协议：" + protocol.getProtocolStr(), EErrorNumber.AUTHFAILED.getCode());
            }
        } catch (IOException e) {
            LogUtil.e(TAG, "发送数据失败，IO流错误");
            if (resultListener != null)
                resultListener.error("发送数据失败，IO流错误", EErrorNumber.WRITETIMEOUT.getCode());
        }
    }


    /**
     * 销毁实例，释放资源
     */
    public void onDestroy() {
        resultListener = null;
        verifySerialProtocolData = null;
        client = null;
    }
}
