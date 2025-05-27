package com.jason.instrction_pipeline;

import com.jason.instrction_pipeline.client.IClient;
import com.jason.jasontools.commandbus.IProtocol;
import com.jason.jasontools.serialport.AbsVerifySerialProtocolData;
import com.jason.jasontools.serialport.IResultListener;
import com.jason.jasontools.serialport.VerifyFailedException;
import com.jason.jasontools.util.EErrorNumber;
import com.jason.jasontools.util.LogUtil;

import java.io.IOException;

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
public class ClientSender {
    private IClient client;
    private AbsVerifySerialProtocolData verifySerialProtocolData = null;
    private IResultListener resultListener;

    public ClientSender setSocketClient(IClient client) {
        this.client = client;
        return this;
    }

    /**
     * 设置数据验证抽象类
     *
     * @param verifySerialProtocolData
     */
    public ClientSender setVerifySerialProtocolData(AbsVerifySerialProtocolData verifySerialProtocolData) {
        this.verifySerialProtocolData = verifySerialProtocolData;
        return this;
    }

    public ClientSender setResultListener(IResultListener listener) {
        this.resultListener = listener;
        return this;
    }


    /**
     * 发送数据
     *
     * @param protocol
     */
    public synchronized void sendData(IProtocol protocol) {
        try {
            if (this.verifySerialProtocolData != null) {
                protocol = verifySerialProtocolData.verifySendData(protocol, protocol.getProtocolLength());
            }
            sendCmd(protocol);
        } catch (VerifyFailedException e) {
            LogUtil.e("协议验证失败，详细错误：\"+e.getMessage()+\" 发送的协议：\" + protocol.getProtocolStr()");
            if (resultListener != null) {
                resultListener.error("协议验证失败，详细错误：" + e.getMessage() + " 发送的协议：" + protocol.getProtocolStr(), EErrorNumber.AUTHFAILED.getCode());
            }
        } catch (IOException e) {
            LogUtil.e("发送数据失败，IO流错误");
            if (resultListener != null)
                resultListener.error("发送数据失败，IO流错误",EErrorNumber.WRITETIMEOUT.getCode());
        } catch (NullPointerException e) {
            LogUtil.e("客户端未打开");
            if (resultListener != null)
                resultListener.error("客户端未打开",EErrorNumber.NULLPOINTER.getCode());
        }
    }

    /**
     * 发送串口指令
     *
     * @param protocol
     */
    private synchronized void sendCmd(IProtocol protocol) throws NullPointerException, IOException {
        if (this.client == null) {
            throw new NullPointerException("serialPort is null");
        }
        this.client.getOutputStream().write(protocol.getProtocol());
        this.client.getOutputStream().flush();
        LogUtil.i("client " + client.getClientTAG() + " 发送成功 [  length:  " + protocol.getProtocolLength() + "  content: " + protocol.getProtocolStr() + "  ]");
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
