package com.jason.jasontools.serialport;

import com.jason.jasontools.commandbus.IProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 描述: 解析串口返回过来的数据抽象类
 * 此解析类是一个链表，使用责任链模式进行处理当前处理器仅处理自己业务
 * 如果到最后没人处理则报错处理。
 * </P>
 */
public abstract class IParseSerialProtocol {

    private IParseSerialProtocol note;

    public ResultData dispatchParse(IProtocol protocol, int len) {
        if (isParseData(protocol, len)) {
            return parseData(protocol, len);
        } else if (this.note != null) {
            return this.note.dispatchParse(protocol, len);
        }
        throw new RuntimeException("无处理器处理协议");
    }

    /**
     * 解析串口返回过来的数据
     *
     * @param protocol 协议
     * @param len      协议数据长度
     * @return
     */
    protected abstract ResultData parseData(IProtocol protocol, int len);

    /**
     * 此节点是否需要处理此协议
     *
     * @param protocol
     * @param len
     * @return
     */
    protected abstract boolean isParseData(IProtocol protocol, int len);

    /**
     * 在当前节点添加拦截器
     *
     * @param protocol
     */
    public void addNote(IParseSerialProtocol protocol) {
        protocol.note = this.note;
        this.note = protocol;
    }
}
