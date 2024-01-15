package com.momoc.netty.frame.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 心跳检测处理器
 * @author momoc
 * @version 1.0
 * @className HearPingHandler
 * @description
 * @date 2023/6/29 11:04
 */
public class HearPingHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //判断evt是否是IdleStateEvent(用于触发用户事件，包含读空闲/写空闲/读写空闲)
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                //创建一个websocket关闭，发送给前端
                CloseWebSocketFrame closeWebSocketFrame = new CloseWebSocketFrame();
                ctx.write(closeWebSocketFrame);
                ctx.flush();
            }
        }else if (evt instanceof ChannelInputShutdownEvent) {
            ctx.channel().close();//远程主机强制关闭连接
        }
        //向下传递事件
        ctx.fireUserEventTriggered(evt);
    }
}
