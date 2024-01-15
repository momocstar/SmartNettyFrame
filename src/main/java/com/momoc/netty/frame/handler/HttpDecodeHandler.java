package com.momoc.netty.frame.handler;

import com.momoc.netty.frame.config.SmartNettyProperties;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpDecodeHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    static Logger log = LoggerFactory.getLogger(HttpDecodeHandler.class);
    SmartNettyProperties smartNettyProperties;

    public HttpDecodeHandler(SmartNettyProperties smartNettyProperties) {
        this.smartNettyProperties = smartNettyProperties;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        // htpp 解析方法：https://blog.csdn.net/IUNIQUE/article/details/121654131
        if (fullHttpRequest.uri().contains(smartNettyProperties.getWebsocketPath())) {
            channelHandlerContext.fireChannelRead(fullHttpRequest);
            return;
        }

        log.info(channelHandlerContext.channel().remoteAddress() + " 客户端请求数据 ... ");

        // HTTP 请求过滤核心代码 -----------------------------------------------------------
        // 判定 HTTP 请求类型, 过滤 HTTP 请求

        // 获取 HTTP 请求
        // 获取网络资源 URI
        URI uri = new URI(fullHttpRequest.uri());
        log.info("本次 HTTP 请求资源 " + uri.getPath());

        // 判定 uri 中请求的资源, 如果请求的是网站图标, 那么直接屏蔽本次请求
        if (uri.getPath() != null && uri.getPath().contains("ico")) {
            log.info("请求图标资源 " + uri.getPath() + ", 屏蔽本次请求 !");
            return;
        }
        // HTTP 请求过滤核心代码 -----------------------------------------------------------

        // 准备给客户端浏览器发送的数据
        ByteBuf byteBuf = Unpooled.copiedBuffer("Hello Client", CharsetUtil.UTF_8);

        // 设置 HTTP 版本, 和 HTTP 的状态码, 返回内容
        DefaultFullHttpResponse defaultFullHttpResponse =
                new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK, byteBuf);

        // 设置 HTTP 请求头
        // 设置内容类型是文本类型
        defaultFullHttpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        // 设置返回内容的长度
        defaultFullHttpResponse.headers().set(
                HttpHeaderNames.CONTENT_LENGTH,
                byteBuf.readableBytes());

        // 写出 HTTP 数据
        channelHandlerContext.writeAndFlush(defaultFullHttpResponse);
    }
}
