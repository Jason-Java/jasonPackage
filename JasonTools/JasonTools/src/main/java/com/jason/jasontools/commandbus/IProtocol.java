package com.jason.jasontools.commandbus;


import com.jason.jasontools.util.StrUtil;

import java.util.List;

public class IProtocol {

    public byte[] protocol;

    /**
     * 设置byte数组格式协议
     *
     * @param protocol 协议
     */
    public void setProtocol(byte[] protocol) {
        this.protocol = protocol;
    }

    /**
     * 设置List<Byte>格式协议
     *
     * @param list
     */
    public void setProtocol(List<Byte> list) {
        protocol = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            protocol[i] = list.get(i);
        }
    }

    /**
     * 获取协议
     *
     * @return
     */
    public byte[] getProtocol() {
        return protocol;
    }

    /**
     * 获取字符串格式协议
     *
     * @return
     */
    public String getProtocolStr() {
        String str = "";
        try {
            str = StrUtil.byteToHexString(protocol);
            return str;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 追加协议
     *
     * @param protocol
     */
    public void addProtocol(byte[] protocol) {
        byte[] newProtocol = new byte[this.protocol.length + protocol.length];
        System.arraycopy(this.protocol, 0, newProtocol, 0, this.protocol.length);
        System.arraycopy(protocol, 0, newProtocol, this.protocol.length, protocol.length);
        this.protocol = newProtocol;
    }

    /**
     * 头部添加协议
     *
     * @param protocol
     */
    public void addHeadProtocol(byte[] protocol) {
        byte[] newProtocol = new byte[this.protocol.length + protocol.length];
        System.arraycopy(protocol, 0, newProtocol, 0, protocol.length);
        System.arraycopy(this.protocol, 0, newProtocol, protocol.length, this.protocol.length);
        this.protocol = newProtocol;
    }

    /**
     * 获取协议长度
     *
     * @return
     */
    public int getProtocolLength() {
        return protocol == null ? 0 : protocol.length;
    }

}
