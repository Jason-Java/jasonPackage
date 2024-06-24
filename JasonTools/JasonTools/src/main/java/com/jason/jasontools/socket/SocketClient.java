package com.jason.jasontools.socket;

import com.jason.jasontools.commandbus.IProtocol;
import com.jason.jasontools.serialport.IParseSerialProtocol;
import com.jason.jasontools.serialport.IResultListener;
import com.jason.jasontools.serialport.IVerifySerialProtocolData;
import com.jason.jasontools.util.LogUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.EventExecutor;

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
public abstract class SocketClient {
    private final static String TAG = "JasonBaseSerialPortTag";

    /**
     * ip地址
     */
    private String ipAddress;


    /**
     * IO线程池
     */
    private EventLoopGroup eventLoopGroup = null;
    /**
     * 处理数据线程池
     */
    private EventLoopGroup workEventLoopGroup;
    private Bootstrap bootstrap;
    private NioSocketChannel nioSocketChannel;

    /**
     * 串口 接口监听<br>
     * 同一个串口注册多个监听，串口接受的结果会发送给每一个监听者
     */
    private IResultListener listener;

    private IVerifySerialProtocolData verifySerialProtocolData = null;
    private IParseSerialProtocol parseSerialProtocolData = null;


    /**
     * 获取Ip地址
     *
     * @return
     */
    protected String getIpAddress() {
        return this.ipAddress;
    }


    public SocketClient() {
        try {
            eventLoopGroup = new NioEventLoopGroup();
            workEventLoopGroup = new DefaultEventLoop();
            bootstrap = new Bootstrap()
                    .group(eventLoopGroup)
                    // 连接超时500毫秒
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 添加netty日志
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                            //添加读空闲5秒超时处理器
                            ch.pipeline().addLast(new IdleStateHandler(3, 0, 0, TimeUnit.SECONDS));
                            // 添加读超时异常处理器
                            ch.pipeline().addLast(new ReadTimeoutHandler(listener));
                            // 添加协议处理转换处理类
                            ch.pipeline().addLast(workEventLoopGroup, BytesToIProtocol.TAG, new BytesToIProtocol(SocketClient.this.verifySerialProtocolData, SocketClient.this.parseSerialProtocolData));
                            ch.pipeline().addLast(workEventLoopGroup, "处理接受到的消息", new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    if (SocketClient.this.listener != null) {
                                        listener.onResult(msg);
                                    }
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    super.exceptionCaught(ctx, cause);
                                    if (SocketClient.this.listener != null)
                                        listener.error(cause.getMessage());
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            if (SocketClient.this.listener != null)
                listener.error("连接超时");
            LogUtil.e(TAG, "open socket error " + e.getMessage());
            eventLoopGroup.shutdownGracefully();
            workEventLoopGroup.shutdownGracefully();
        }
    }

    /**
     * 打开串口接收器
     */
    public void connect() {
        if (nioSocketChannel == null || !nioSocketChannel.isActive())
            this.connect(this.getIpAddress());
    }

    /**
     * 设置Ip地址
     *
     * @param ipAddress
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }


    /**
     * 发起连接
     *
     * @param ipAddress ip地址
     */
    public void connect(String ipAddress) {
        this.ipAddress = ipAddress;
        String ipAndProtocol[] = this.ipAddress.split(":");
        try {
            nioSocketChannel = (NioSocketChannel) bootstrap
                    .connect(new InetSocketAddress(ipAndProtocol[0], Integer.parseInt(ipAndProtocol[1])))
                    .sync().channel();
        } catch (Exception e) {
            if (this.listener != null) {
                this.listener.error("连接超时");
            }
            e.printStackTrace();
            LogUtil.e(TAG, "open socket error " + e.getMessage());
            eventLoopGroup.shutdownGracefully();
            workEventLoopGroup.shutdownGracefully();
        }
    }


    /**
     * 注册监听器
     * 同一个串口可以有多个监听者，
     *
     * @param listener 监听器
     */
    public void registerListener(IResultListener listener) {
        this.listener = listener;
    }

    /**
     * 取消监听器
     */
    public void unregisterListener() {
        listener = null;
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
        if (nioSocketChannel != null) {
            //清除缓存
            nioSocketChannel.writeAndFlush(protocol);
        }

    }

    /**
     * 断开连接
     */
    public void disConnect() {
        if (nioSocketChannel == null) {
            return;
        }
        nioSocketChannel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("我是结束事件");
            }
        });
        nioSocketChannel.close();
        listener = null;
    }

    public void destroy() {
        // 停止 线程
        EventLoopGroup group = bootstrap.config().group();
        Iterator<EventExecutor> iterator = group.iterator();
        while (iterator.hasNext()) {
            EventExecutor next = iterator.next();
            next.shutdownGracefully();
            iterator.remove();
        }
    }
}
