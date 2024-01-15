package com.momoc.netty.frame.handler;


import com.momoc.netty.frame.event.WebsocketEventContext;
import com.momoc.netty.frame.event.annotation.WebsocketConnectEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

public class WebSocketHandshakeHandler extends ChannelInboundHandlerAdapter {
    /**
     * WebSocketServerProtocolHandler 读了一下源码，才找出来，握手事件
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt == WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE) {
            WebsocketEventContext.getEventMethodsAndInvoke(ctx.channel(), WebsocketConnectEvent.class);
        }
        super.userEventTriggered(ctx, evt);
    }

}
