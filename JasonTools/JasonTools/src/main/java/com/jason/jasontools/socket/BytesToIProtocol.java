package com.jason.jasontools.socket;

import com.jason.jasontools.commandbus.IProtocol;
import com.jason.jasontools.serialport.IParseSerialProtocol;
import com.jason.jasontools.serialport.IVerifySerialProtocolData;
import com.jason.jasontools.serialport.ResultData;
import com.jason.jasontools.serialport.VerifyFailedException;
import com.jason.jasontools.util.LogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年01月29日
 */
public class BytesToIProtocol extends ByteToMessageCodec<IProtocol> {
    public final static String TAG = "JasonBytesToIProtocolTag";
    /**
     * 验证协议是否有效
     */
    private IVerifySerialProtocolData verifySerialProtocolData;
    /**
     * 处理返回协议
     */
    private IParseSerialProtocol parseSerialProtocol;

    public BytesToIProtocol(IVerifySerialProtocolData verifySerialProtocolData, IParseSerialProtocol parseSerialProtocol) {
        this.verifySerialProtocolData = verifySerialProtocolData;
        this.parseSerialProtocol = parseSerialProtocol;
    }

    // 发送消息进行编码
    @Override
    protected void encode(ChannelHandlerContext ctx, IProtocol protocol, ByteBuf out) throws Exception {
        try {
            if (this.verifySerialProtocolData != null) {
                protocol = verifySerialProtocolData.verifySendData(protocol, protocol.getProtocolLength());
            }
            out.writeBytes(protocol.getProtocol());
        } catch (VerifyFailedException e) {
            LogUtil.e(TAG, "协议验证失败，详细错误：\"+e.getMessage()+\" 发送的协议：\" + protocol.getProtocolStr()");
            throw new VerifyFailedException("协议验证失败，详细错误：\" + e.getMessage() + \" 发送的协议：\" + protocol.getProtocolStr()", e.getCause());
        }
    }

    // 接受消息进行解码
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int i = in.readableBytes();
        byte[] bytes = new byte[i];
        in.readBytes(bytes);
/*
        while (in.isReadable()) {
            cacheBytes.add(in.readByte());
        }
*/
        IProtocol protocol = new IProtocol();
        protocol.setProtocol(bytes);
        try {
            if (verifySerialProtocolData != null)
                //验证数据是否合法，合法
                protocol = verifySerialProtocolData.verifyReceiveData(protocol, protocol.getProtocolLength());
            if (protocol == null) return;
            ResultData resultData = null;
            if (this.parseSerialProtocol != null) {
                resultData = this.parseSerialProtocol.dispatchParse(protocol, protocol.getProtocolLength());
            } else {
                resultData = new ResultData();
                resultData.setProtocol(protocol);
            }
            out.add(resultData);

        } catch (VerifyFailedException e) {
            LogUtil.e(TAG, "verifyData error " + e.getMessage());
            throw new VerifyFailedException("协议验证失败，详细错误：" + e.getMessage() + "接收到的协议：" + protocol.getProtocolStr(), e.getCause());
        } catch (Exception e) {
            LogUtil.e(TAG, "parseData error " + e.getMessage());
            throw new Exception("详细错误：" + e.getMessage() + "接收到的协议：" + protocol.getProtocolStr(), e.getCause());
        }
    }

    public void setVerifySerialProtocolData(IVerifySerialProtocolData verifySerialProtocolData) {
        this.verifySerialProtocolData = verifySerialProtocolData;
    }

    public void setParseSerialProtocol(IParseSerialProtocol parseSerialProtocol) {
        this.parseSerialProtocol = parseSerialProtocol;
    }
}
