package com.momoc.netty.frame.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author momoc
 * @version 1.0
 * @className NettyConfig
 * @description
 * @date 2023/6/27 17:38
 */
@ConfigurationProperties(value = "momoc.netty", ignoreInvalidFields = true)
public class SmartNettyProperties {

    private static SmartNettyProperties smartNettyProperties;

    public SmartNettyProperties() {
        smartNettyProperties = this;
    }

    /**
     * netty 启动端口号,默认2300
     */
    private Integer port = 2300;

    /**
     * 处理链接线程,默认为cpu的线程数/8, 不足至少为1
     */
    private Integer bossThreadNums = Runtime.getRuntime().availableProcessors() / 8 == 0 ? 1 : Runtime.getRuntime().availableProcessors() / 8;

    /**
     * 工作线程，主要处理数据写入写出。 cpu线程数 * 2
     */
    private Integer workThreadNums = Runtime.getRuntime().availableProcessors() * 2;
    /**
     * 连接超时
     */
    private Integer connectTimeout = 15000;


    /**
     * 消息id长度，根据需要定义,默认16位
     */
    private Integer msgIdLength = 16;

    private Boolean debug = false;

    /**
     * netty访问链接：ip:端口号/nettyPath
     */
    private String websocketPath = "/ws";

    public String getWebsocketPath() {
        return websocketPath;
    }

    public void setWebsocketPath(String websocketPath) {
        this.websocketPath = websocketPath;
    }

    public Boolean getDebug() {
        return debug;
    }

    public void setDebug(Boolean debug) {
        this.debug = debug;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getBossThreadNums() {
        return bossThreadNums;
    }

    public void setBossThreadNums(Integer bossThreadNums) {
        this.bossThreadNums = bossThreadNums;
    }

    public Integer getWorkThreadNums() {
        return workThreadNums;
    }

    public void setWorkThreadNums(Integer workThreadNums) {
        this.workThreadNums = workThreadNums;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Integer getMsgIdLength() {
        return msgIdLength;
    }

    public void setMsgIdLength(Integer msgIdLength) {
        this.msgIdLength = msgIdLength;
    }

    public static SmartNettyProperties getNettyProperties() {
        return smartNettyProperties;
    }
}
