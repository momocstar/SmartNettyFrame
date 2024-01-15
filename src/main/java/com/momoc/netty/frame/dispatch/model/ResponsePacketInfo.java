package com.momoc.netty.frame.dispatch.model;

/**
 * 转发时用到
 * @author momoc
 * @version 1.0
 * @className PacketMassageInfo
 * @description
 * @date 2023/7/3 18:06
 */
public class ResponsePacketInfo {
    byte[] msgRespByte;

    public byte[] getMsgRespByte() {
        return msgRespByte;
    }

    public void setMsgRespByte(byte[] msgRespByte) {
        this.msgRespByte = msgRespByte;
    }
}
