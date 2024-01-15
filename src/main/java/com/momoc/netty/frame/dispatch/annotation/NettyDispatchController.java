package com.momoc.netty.frame.dispatch.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * 标记为netty 消息转发类
 *
 * @author momoc
 * @version 1.0
 * @className NettyController
 * @description
 * @date 2023/6/27 18:22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface NettyDispatchController {

}
