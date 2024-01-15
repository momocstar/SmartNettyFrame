package com.momoc.netty.frame.dispatch.model;

import com.momoc.netty.frame.handler.ProtobufDecoder;
import java.lang.reflect.Method;

/**
 * 转发时用到
 *
 * @author momoc
 * @version 1.0
 * @className PacketMassageInfo
 * @description
 * @date 2023/7/3 18:06
 */
public class PacketMassageInfo {
    /**
     * 解码器
     */
    ProtobufDecoder protobufDecoder;
    //对应的方法名
    Method method;

    Object bean;

    /**
     * 消息号的二进制
     */
    byte[] msgByte;


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

    public byte[] getMsgByte() {
        return msgByte;
    }

    public void setMsgByte(byte[] msgByte) {
        this.msgByte = msgByte;
    }
}
