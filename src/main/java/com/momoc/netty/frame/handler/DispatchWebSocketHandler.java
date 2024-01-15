package com.momoc.netty.frame.handler;

import com.google.protobuf.MessageLite;
import com.momoc.netty.frame.config.SmartNettyProperties;
import com.momoc.netty.frame.context.ProtobufHandlerMappingContext;
import com.momoc.netty.frame.dispatch.WebsocketRequestContextHolder;
import com.momoc.netty.frame.dispatch.model.PacketMassageInfo;
import com.momoc.netty.frame.filter.WebsocketFilterHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import java.lang.reflect.Method;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息转发器，通过消息id转发到，被NettyDispatchController注解标记的类
 * @author momoc
 * @version 1.0
 * @className MessageInBoundsHandler
 * @description
 * @date 2023/6/30 17:19
 */
public class DispatchWebSocketHandler extends MessageToMessageDecoder<BinaryWebSocketFrame> {
    static Logger log = LoggerFactory.getLogger(DispatchWebSocketHandler.class);

    final
    SmartNettyProperties smartNettyProperties;

    public DispatchWebSocketHandler(SmartNettyProperties smartNettyProperties) {
        this.smartNettyProperties = smartNettyProperties;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, BinaryWebSocketFrame binaryWebSocketFrame, List<Object> list) throws Exception {

        ByteBuf byteBuf = binaryWebSocketFrame.content();
        Integer msgIdLength = smartNettyProperties.getMsgIdLength();
        // 一个字节八位，所以要除以8
        int readMsgLength = msgIdLength / 8;
        //读消息两个字节
        ByteBuf buf = byteBuf.readBytes(readMsgLength);
        Integer msgId = null;
        switch (readMsgLength) {
            case 2 -> msgId = buf.readUnsignedShort();
            case 3 -> msgId = buf.readUnsignedMedium();
            case 4 -> msgId = (int) buf.readUnsignedInt();
            default -> {
                log.error("readMsgLength error,length:{}", msgId);
                return;
            }
        }
        //读消息号，目前消息号大小是16位，最多能定义65535个消息。
        PacketMassageInfo packetMassageInfo = ProtobufHandlerMappingContext.getPacketInfoByMessageId(msgId);
        if (packetMassageInfo == null) {
            log.error(" This message does not have forwarder support, msgId:{}", msgId);
            return;
        }

        Object bean = packetMassageInfo.getBean();
        ProtobufDecoder protobufDecoder = packetMassageInfo.getProtobufDecoder();
        MessageLite decode = protobufDecoder.decode(byteBuf);
        Method method = packetMassageInfo.getMethod();
        try {
            WebsocketRequestContextHolder.setRequestChannel(channelHandlerContext);
            WebsocketFilterHandler.getWebsocketFilterHandler().doFilterChain(channelHandlerContext);
            Object invoke = method.invoke(bean, decode);
            if (invoke != null){
                channelHandlerContext.writeAndFlush(invoke);
            }
        }catch (Exception e){
            log.error("message request exception,msgId:{}", msgId, e);
        }finally {
            WebsocketRequestContextHolder.removeChannel();
        }
    }
}
