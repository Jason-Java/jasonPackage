package com.jason.jasontools.commandbus;


public interface IMessageListener<T> {
    void start();
    void success(T data);

    void error(String message, int type);
}
