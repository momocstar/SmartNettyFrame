package com.momoc.netty.frame.event.annotation;

import io.netty.channel.Channel;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * websocket关闭时的事件
 * 方法参数固定为：{@link Channel}
 *
 * @author momoc
 * @version 1.0
 * @className WebsocketClose
 * @description
 * @date 2023/7/5 13:38
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebsocketCloseEvent {

}
