package com.momoc.netty.frame.filter;

import io.netty.channel.ChannelHandlerContext;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.springframework.core.annotation.Order;

/**
 * @author momoc
 * @version 1.0
 * @className WebsocketFilterHandler
 * @description
 * @date 2023/7/7 16:56
 */
public class WebsocketFilterHandler {

    private static WebsocketFilterHandler websocketFilterHandler;



    public static WebsocketFilterHandler getWebsocketFilterHandler() {
        return websocketFilterHandler;
    }

    public static void setWebsocketFilterHandler(WebsocketFilterHandler websocketFilterHandler) {
        WebsocketFilterHandler.websocketFilterHandler = websocketFilterHandler;
    }

    WebsocketFilterChain firstChain;

    public void doFilterChain(ChannelHandlerContext context){
        if (firstChain == null){
            return;
        }
        firstChain.getFilter().doFilter(context,firstChain.getNext());
    };


    private WebsocketFilterHandler(List<IWebSocketFilter> iWebSocketFilter) {
        websocketFilterHandler = this;
        if (iWebSocketFilter == null || iWebSocketFilter.isEmpty()) {
            return;
        }
        iWebSocketFilter.sort(new Comparator<IWebSocketFilter>() {
            @Override
            public int compare(IWebSocketFilter o1, IWebSocketFilter o2) {

                int numOne = 0;
                int numTwo = 0;
                Order annotation = o1.getClass().getAnnotation(Order.class);
                if (annotation != null) {
                    numOne = annotation.value();
                }

                if (o2 != null) {
                    Order annotation2 = o2.getClass().getAnnotation(Order.class);
                    if (annotation2 != null) {
                        numTwo = annotation2.value();
                    }
                }
                if (numOne == numTwo) {
                    return 0;
                } else if (numOne > numTwo) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        WebsocketFilterChain next = null;
        for (IWebSocketFilter webSocketFilter : iWebSocketFilter) {
            if (firstChain == null){
                firstChain = new WebsocketFilterChain(new WebsocketFilterChain(null), webSocketFilter);
                next = firstChain;
            }else{
                next = new WebsocketFilterChain(next, webSocketFilter);
            }
        }

        firstChain = next;
    }





}
