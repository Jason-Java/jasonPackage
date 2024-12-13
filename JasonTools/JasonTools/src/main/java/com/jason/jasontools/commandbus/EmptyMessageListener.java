package com.jason.jasontools.commandbus;

import com.jason.jasontools.serialport.ResultData;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年07月17日
 */
public class EmptyMessageListener implements IMessageListener<ResultData> {
    @Override
    public void start() {

    }

    @Override
    public void success(ResultData data) {

    }

    @Override
    public void error(String message, int type) {

    }
}
