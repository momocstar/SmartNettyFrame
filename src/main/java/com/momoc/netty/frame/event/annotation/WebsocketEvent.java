package com.momoc.netty.frame.event.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * 标记websocket一些事件处理类
 *
 * @author momoc
 * @version 1.0
 * @className WebsocketEvent
 * @description
 * @date 2023/7/5 13:39
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface WebsocketEvent {
}
