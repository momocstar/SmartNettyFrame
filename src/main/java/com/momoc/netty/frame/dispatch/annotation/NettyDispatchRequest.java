package com.momoc.netty.frame.dispatch.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * 具体的方法
 *
 * @author momoc
 * @version 1.0
 * @className NettyRequest
 * @description
 * @date 2023/6/27 18:23
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface NettyDispatchRequest {

    /**
     * 是否给调用方法传入ChannelContext参数,作为第二个参数
     * {@link io.netty.channel.ChannelHandlerContext}
     */
    boolean openChannelContext() default false;

    /**
     * 消息包解码类
     * @return
     */
//    Class<?> packetClass();

}
