package com.jason.instrction_pipeline.client;

import com.jason.jasontools.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * <p>
 * 描述: socket客户端，负责管理socket的打开和关闭以及资源的销毁
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年12月09日
 */
public class SocketClient implements IClient {
    private Socket socket = null;
    private String ip;
    private int protocol;
    private int connectTimeout;


    /**
     * @param ip       ip地址
     * @param protocol 端口号
     */
    public SocketClient(String ip, int protocol) {
        socket = new Socket();
        this.ip = ip;
        this.protocol = protocol;
    }

    /**
     * @param ip             ip地址
     * @param protocol       端口号
     * @param connectTimeout 连接超时时间
     */
    public SocketClient(String ip, int protocol, int connectTimeout) {
        socket = new Socket();
        this.ip = ip;
        this.protocol = protocol;
        this.connectTimeout = connectTimeout;
    }


    @Override
    public SocketClient openClient() {
        try {
            socket.connect(new InetSocketAddress(ip, protocol), connectTimeout);
        } catch (IOException e) {
            this.closeClient();
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    /**
     * 关闭socket
     *
     * @throws IOException
     */
    @Override
    public void closeClient() {
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


    @Override
    public String getClientTAG() {
        return "SocketClient " + ip + ":" + protocol+"  ";
    }
}