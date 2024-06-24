package com.jason.jasontools.serialport;

import com.jason.jasontools.commandbus.IProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 描述: 解析串口返回过来的数据抽象类
 * </P>
 */
public abstract class IParseSerialProtocol {

    private Map<String, IParseSerialProtocol> map;

    /**
     * 解析串口返回过来的数据
     *
     * @param protocol 协议
     * @param len      协议数据长度
     * @return
     */
    public abstract ResultData parseData(IProtocol protocol, int len);


    protected IParseSerialProtocol getParseProtocol(String tag) {
        return getMap().get(tag);
    }

    protected Map<String, IParseSerialProtocol> getMap() {
        if (map == null) {
            map = new HashMap<>();
        }
        return map;
    }

}
