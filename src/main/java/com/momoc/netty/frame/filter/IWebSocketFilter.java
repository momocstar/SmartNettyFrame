package com.momoc.netty.frame.filter;

import io.netty.channel.ChannelHandlerContext;

/**
 *
 * 注解上的order 越小越优先
 * websocket 过滤器
 * @author momoc
 * @version 1.0
 * @className IWebSocketFilter
 * @description
 * @date 2023/7/7 12:48
 */
public interface IWebSocketFilter {


    void doFilter(ChannelHandlerContext context, WebsocketFilterChain chain);

}
