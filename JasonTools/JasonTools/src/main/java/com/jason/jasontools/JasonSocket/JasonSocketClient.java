package com.jason.jasontools.JasonSocket;

import com.jason.jasontools.commandbus.IProtocol;
import com.jason.jasontools.serialport.AbsVerifySerialProtocolData;
import com.jason.jasontools.serialport.IParseSerialProtocol;
import com.jason.jasontools.serialport.VerifyFailedException;
import com.jason.jasontools.util.JasonThreadPool;
import com.jason.jasontools.util.LogUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年12月09日
 */
public class JasonSocketClient {
    private static final String TAG = JasonSocketClient.class.getSimpleName();
    private Socket socket = null;
    private String ip;
    private int protocol;


    public JasonSocketClient(String ip, int protocol) {
        socket = new Socket();
        this.ip = ip;
        this.protocol = protocol;
    }


    /**
     * 创建socket连接
     *
     * @param timeOut 连接超时时间
     * @throws IOException
     */
    public JasonSocketClient connect(int timeOut) {
        try {
            socket.connect(new InetSocketAddress(ip, protocol), timeOut);
        } catch (IOException e) {
            this.close();
            throw new RuntimeException(e);
        }
        return this;
    }

    public Socket getSocket() {
        return socket;
    }

    /**
     * 关闭socket
     *
     * @throws IOException
     */
    public void close() {
        try {
            if (socket != null) {
                socket.getInputStream().close();
                socket.getOutputStream().close();
            }
        } catch (IOException e) {
            LogUtil.e("数据流关闭异常 " + e.getMessage());
        } finally {
            if (socket != null && socket.isConnected() && !socket.isClosed()) {
                try {
                    socket.close();
                    socket = null;
                } catch (IOException e) {
                    LogUtil.i("socket 关闭异常  " + e.getMessage());
                }
            } else {
                socket = null;
            }
        }
        // 定时发送心跳包
    }
}