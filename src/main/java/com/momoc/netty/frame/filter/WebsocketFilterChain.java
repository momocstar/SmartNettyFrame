package com.momoc.netty.frame.filter;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author momoc
 * @version 1.0
 * @className WebsocketFilterChain
 * @description
 * @date 2023/7/7 17:22
 */
public class WebsocketFilterChain {

    private IWebSocketFilter filter;

    private WebsocketFilterChain next;

    WebsocketFilterChain( IWebSocketFilter filter){
        this.filter = filter;
    }

    WebsocketFilterChain(WebsocketFilterChain next , IWebSocketFilter filter){
        this.next = next;
        this.filter = filter;

    }

    public  void doNextFilter(ChannelHandlerContext context, WebsocketFilterChain chain){
        if (chain == null || chain.filter == null ){
            return;
        }
        chain.getFilter().doFilter(context, chain.getNext());
    };

    public IWebSocketFilter getFilter() {
        return filter;
    }

    public WebsocketFilterChain getNext() {
        return next;
    }

}
