package com.momoc.netty.frame.dispatch.model;

import com.momoc.netty.frame.handler.ProtobufDecoder;
import java.lang.reflect.Method;

/**
 * @author momoc
 * @version 1.0
 * @className PacketRequestMassageInfo
 * @description
 * @date 2023/7/6 10:53
 */
public class RequestPacketInfo {
    /**
     * 解码器
     */
    ProtobufDecoder protobufDecoder;
    //对应的方法名
    Method method;

    Object bean;

    byte[] msgReqByte;

    boolean openChannelContext;

    public ProtobufDecoder getProtobufDecoder() {
        return protobufDecoder;
    }

    public void setProtobufDecoder(ProtobufDecoder protobufDecoder) {
        this.protobufDecoder = protobufDecoder;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public byte[] getMsgReqByte() {
        return msgReqByte;
    }

    public void setMsgReqByte(byte[] msgReqByte) {
        this.msgReqByte = msgReqByte;
    }

    public boolean isOpenChannelContext() {
        return openChannelContext;
    }

    public void setOpenChannelContext(boolean openChannelContext) {
        this.openChannelContext = openChannelContext;
    }
}
