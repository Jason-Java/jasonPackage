package com.jason.jasontools.serialport;

/**
 * <p>
 * 描述: 串口数据校验失败异常类
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2023年08月21日
 */
public class VerifyFailedException extends Exception {
    public VerifyFailedException() {
        super();
    }

    public VerifyFailedException(String message) {
        super(message);
    }

    public VerifyFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public VerifyFailedException(Throwable cause) {
        super(cause);
    }

    protected VerifyFailedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
