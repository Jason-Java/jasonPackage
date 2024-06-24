package com.jason.jasontools.commandbus;

import com.jason.jasontools.serialport.ResultData;

public interface IMessageListener {
    void success(ResultData data);

    void error(String message, int type);
}
