package com.jason.jasontools.commandbus;

import com.jason.jasontools.serialport.ResultData;

public interface IMessageListener {
    void start();
    void success(ResultData data);

    void error(String message, int type);
}
