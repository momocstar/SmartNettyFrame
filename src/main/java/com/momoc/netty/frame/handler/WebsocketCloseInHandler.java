package com.momoc.netty.frame.handler;

import com.momoc.netty.frame.event.WebsocketEventContext;
import com.momoc.netty.frame.event.annotation.WebsocketCloseEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author momoc
 * @version 1.0
 * @className WebsocketCloseOutHandler
 * @description
 * @date 2023/7/5 10:47
 */
public class WebsocketCloseInHandler extends SimpleChannelInboundHandler<CloseWebSocketFrame> {
    static Logger log = LoggerFactory.getLogger(DispatchWebSocketHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloseWebSocketFrame msg) throws Exception {
        //传递给下个处理器进行关闭
//        ctx.fireChannelRead(msg);
        //关闭后执行的代码
        WebsocketEventContext.getEventMethodsAndInvoke(ctx.channel(), WebsocketCloseEvent.class);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught", cause);
    }
}
