package com.jason.jasontools.serialport;

import com.jason.jasontools.commandbus.IProtocol;

import java.util.ArrayList;

/**
 * <p>
 * 描述: 校验串口数据的抽象类
 * </P>
 */
public interface IVerifySerialProtocolData {
    /**
     * 校验串口发送出去的数据
      * @param protocol 协议
     * @param len 协议数据长度
     * @return 如果校验成功则返回协议,如果校验失败则抛出异常
     * @throws VerifyFailedException 校验失败异常
     */
    IProtocol verifySendData(IProtocol protocol,int len) throws VerifyFailedException;

    /**
     * 校验串口返回过来的数据
     *
     * @param protocol 协议
     * @param len      协议数据长度
     * @return 如果校验成功则返回协议，如果返回null 代表数据不合法，但是不清除缓存
     * @throws VerifyFailedException 校验失败异常
     */
    IProtocol verifyReceiveData(IProtocol protocol, int len) throws VerifyFailedException;
}
