package com.momoc.netty.frame.handler;

import com.google.protobuf.MessageLite;
import com.google.protobuf.MessageLiteOrBuilder;
import com.momoc.netty.frame.context.ProtobufHandlerMappingContext;
import com.momoc.netty.frame.dispatch.model.PacketMassageInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import java.util.List;

/**
 * proto消息发送编码，发送protobuf消息出站
 * @author momoc
 * @version 1.0
 * @className ProtobufEnCoderHandler
 * @description
 * @date 2023/7/5 18:54
 */
public class ProtobufEnCoderHandler extends MessageToMessageEncoder<MessageLiteOrBuilder> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageLiteOrBuilder invoke, List<Object> out) throws Exception {
        Class<? extends MessageLiteOrBuilder> aClass = invoke.getClass();
        String name = aClass.getName();
        String[] classNameArr = name.split("_");
        int msgId = Integer.parseInt(classNameArr[1]);
        byte[] responseMsgByte = ProtobufHandlerMappingContext.getResponseMsgByte(msgId);
        ByteBuf buf = null;
        if (invoke instanceof MessageLite) {
            byte[] bytes = ((MessageLite) invoke).toByteArray();
            buf = Unpooled.wrappedBuffer(responseMsgByte, bytes);
        } else {
            if (invoke instanceof MessageLite.Builder) {
                byte[] bytes = ((MessageLite.Builder) invoke).build().toByteArray();
                buf = Unpooled.wrappedBuffer(responseMsgByte, bytes);
            }
        }
        WebSocketFrame frame = new BinaryWebSocketFrame(buf);
        ctx.writeAndFlush(frame);
    }
}
