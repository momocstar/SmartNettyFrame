package com.momoc.netty.frame.handler;

import com.momoc.netty.frame.config.SmartNettyProperties;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;


/**
 * 用于服务的初始化，向netty加入一些处理器
 */
public class WebSocketInitHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (SmartNettyProperties.getNettyProperties() == null){
            new SmartNettyProperties();
        }
        //开启debug模式
        if (SmartNettyProperties.getNettyProperties().getDebug()) {
            pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        }
        //10 秒客户端没有向服务器发送心跳则关闭连接
        pipeline.addLast(new IdleStateHandler(10, 0, 0));
        // HTTP请求的解码和编码
        pipeline.addLast(new HttpServerCodec());
        // 把多个消息转换为一个单一的FullHttpRequest或是FullHttpResponse，
        // 原因是HTTP解码器会在每个HTTP消息中生成多个消息对象HttpRequest/HttpResponse,HttpContent,LastHttpContent
        pipeline.addLast(new HttpObjectAggregator(65536));
        //心跳
        pipeline.addLast(new HearPingHandler());
        // 主要用于处理大数据流，比如一个1G大小的文件如果你直接传输肯定会撑暴jvm内存的; 增加之后就不用考虑这个问题了
        pipeline.addLast(new ChunkedWriteHandler());
        // WebSocket数据压缩
        pipeline.addLast(new WebSocketServerCompressionHandler());
        //用于websocket关闭
        pipeline.addLast(new WebsocketCloseInHandler());

        // 协议包长度限制
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws", null, true));
        //握手链接
        pipeline.addLast(new WebSocketHandshakeHandler());
        //websocket 客户端手动关闭
//        pipeline.addLast(new WebSocketCloseHandler());
        pipeline.addLast(new HttpDecodeHandler(SmartNettyProperties.getNettyProperties()));
        pipeline.addLast(new ProtobufEnCoderHandler());
        pipeline.addLast(new DispatchWebSocketHandler(SmartNettyProperties.getNettyProperties()));
    }

}