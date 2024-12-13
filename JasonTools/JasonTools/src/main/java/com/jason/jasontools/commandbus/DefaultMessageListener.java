package com.jason.jasontools.commandbus;

import com.jason.jasontools.serialport.ResultData;

/**
 * <p>
 * 描述: 默认消息监听类，默认不关心start函数，仅关心success和error
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年06月28日
 */
public abstract class DefaultMessageListener implements IMessageListener<ResultData> {
    @Override
    public void start() {

    }
}
