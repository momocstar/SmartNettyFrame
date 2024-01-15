package com.momoc.netty.frame.dispatch;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.core.NamedThreadLocal;

/**
 * @author momoc
 * @version 1.0
 * @className NettyRequestContextHolder
 * @description
 * @date 2023/7/7 16:12
 */

public class WebsocketRequestContextHolder {

    private static final ThreadLocal<ChannelHandlerContext> requestAttributesHolder = new NamedThreadLocal("Netty protobuf Request attributes");

    public static ChannelHandlerContext getRequestChannel(){
        return requestAttributesHolder.get();
    }

    public static void setRequestChannel(ChannelHandlerContext channel){
         requestAttributesHolder.set(channel);
    }

    public static void removeChannel(){
        requestAttributesHolder.remove();
    }
}
