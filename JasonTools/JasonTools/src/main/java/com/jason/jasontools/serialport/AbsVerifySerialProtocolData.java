package com.jason.jasontools.serialport;

import com.jason.jasontools.commandbus.IProtocol;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 描述: 校验串口数据的抽象类。
 * 在验证数据的时候兼容缓存数据的任务。
 * 如果要使用缓存功能需要注意一下两点。
 * 1. 在发送数据函数{@link AbsVerifySerialProtocolData#verifySendData(IProtocol, int)}中清空上次的缓冲数据，以便接收新的数据。
 * 2. 在接收数据函数中{@linkplain AbsVerifySerialProtocolData#verifyReceiveData(IProtocol, int)}需要对数据进行缓存处理
 * </P>
 */
public abstract class AbsVerifySerialProtocolData {
    /**
     * 字节缓冲区
     */
    private List<Byte> cache;

    public AbsVerifySerialProtocolData() {
        cache = new LinkedList<>();
    }

    /**
     * 校验串口发送出去的数据
     *
     * @param protocol 协议
     * @param len      协议数据长度
     * @return 如果校验成功则返回协议, 如果校验失败则抛出异常
     * @throws VerifyFailedException 校验失败异常
     */
    public abstract IProtocol verifySendData(IProtocol protocol, int len) throws VerifyFailedException;

    /**
     * 校验串口返回过来的数据
     *
     * @param protocol 协议
     * @param len      协议数据长度
     * @return 如果校验成功则返回协议，如果返回null 代表当前收到数据不完整，需要进行缓存
     * @throws VerifyFailedException 校验失败异常
     */
    public abstract IProtocol verifyReceiveData(IProtocol protocol, int len) throws VerifyFailedException;

    /**
     * 添加缓存
     *
     * @param protocol
     */
    protected final IProtocol addCache(IProtocol protocol) {
        if (cache.size() > 1024) {
            cache.clear();
        }
        for (int j = 0; j < protocol.getProtocolLength(); j++) {
            cache.add(protocol.protocol[j]);
        }
        protocol.setProtocol(cache);
        return protocol;
    }

    /**
     * 清空缓存
     */
    protected final void clearCache() {
        cache.clear();
    }
}
