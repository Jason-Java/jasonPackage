package com.jason.jasontools.socket;

import com.jason.jasontools.serialport.IResultListener;
import com.jason.jasontools.util.LogUtil;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * <p>
 * 描述:
 * </P>
 *
 * @author 阿振
 * @version 1.0
 * @email fjz19971129@163.com
 * @createTime 2024年01月29日
 */
public class ReadTimeoutHandler extends ChannelDuplexHandler {
    private IResultListener listener;

    public ReadTimeoutHandler(IResultListener listener) {
        this.listener = listener;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                LogUtil.e("接受数据超时");
                ctx.channel().close();
                if (this.listener != null) {
                    BytesToIProtocol handler = (BytesToIProtocol) ctx.pipeline().get(BytesToIProtocol.TAG);
                    listener.error("读取超时");
                }
            }
        }
        super.userEventTriggered(ctx, evt);
    }

}
