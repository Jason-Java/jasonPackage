package com.jason.instrction_pipeline;

import com.jason.instrction_pipeline.client.IClient;
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
public class ClientReceiver implements Runnable {
    private IClient iClient;

    private AbsVerifySerialProtocolData verifySerialProtocolData = null;
    private IParseSerialProtocol parseSerialProtocolData = null;
    private IResultListener resultListener;
    private boolean isRun = false;

    public ClientReceiver() {
        LogUtil.i("接受数据线程已准备就绪，请调用 starReceiverData() 方法启动线程");
    }

    public ClientReceiver setSocketClient(IClient client) {
        this.iClient = client;
        return this;
    }

    /**
     * 设置数据验证抽象类
     *
     * @param verifySerialProtocolData
     */
    public ClientReceiver setVerifySerialProtocolData(AbsVerifySerialProtocolData verifySerialProtocolData) {
        this.verifySerialProtocolData = verifySerialProtocolData;
        return this;
    }

    public ClientReceiver setResultListener(IResultListener listener) {
        this.resultListener = listener;
        return this;
    }

    public ClientReceiver starReceiverData() {
        isRun = true;
        new Thread(this).start();
        LogUtil.i("接受数据线程已启动！");
        return this;
    }

    /**
     * 解析数据
     *
     * @param parseSerialProtocolData 解析数据的接口
     */
    public ClientReceiver setParseSerialProtocolData(IParseSerialProtocol parseSerialProtocolData) {
        this.parseSerialProtocolData = parseSerialProtocolData;
        return this;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[4096];
        while (isRun) {
            try {
                int size = this.iClient.getInputStream().read(buffer);
                // 远程服务器已经关闭socket，本地也同步关闭
                if (size < 0) {
                    LogUtil.i("client 客户端主动断开连接");
                    this.iClient.closeClient();
                    break;
                }
                if (size > 0) {
                    byte[] data = new byte[size];
                    System.arraycopy(buffer, 0, data, 0, size);
                    LogUtil.i("client :" + iClient.getClientTAG() + " 接受成功 [  length:  " + data.length + "  content: " + StrUtil.byteToHexString(data) + "  ]");
                    onResponseData(data, data.length);
                }
            } catch (Exception e) {
                LogUtil.e("客户端 接受数据线程异常结束  " + e.getMessage());
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
            LogUtil.e("verifyData error " + e.getMessage());
            if (resultListener != null)
                resultListener.error("协议验证失败，详细错误：" + e.getMessage() + "接收到的协议：" + protocol.getProtocolStr(), EErrorNumber.AUTHFAILED.getCode());
        } catch (Exception e) {
            LogUtil.e("parseData error " + e.getMessage());
            if (resultListener != null)
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
        iClient = null;

    }

}
