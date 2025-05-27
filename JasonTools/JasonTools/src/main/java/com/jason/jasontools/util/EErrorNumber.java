package com.jason.jasontools.util;

public enum EErrorNumber {

    /**
     * 未定义
     */
    UNDEFINED(1000, "未定义错误"),
    /**
     * 连接超时
     */
    LINKTIMEOUT(1001, "连接超时"),
    /**
     * 读取超时
     */
    READTIMEOUT(1002, "读取超时"),
    /**
     * 写数据超时
     */
    WRITETIMEOUT(1004, "写数据超时"),
    /**
     * 空指针异常
     */
    NULLPOINTER(1005, "空指针异常"),

    /**
     * 协议认证失败
     */
    AUTHFAILED(1003, "协议认证失败"),
    ;


    private  int code;
    private  String message;
    EErrorNumber(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
