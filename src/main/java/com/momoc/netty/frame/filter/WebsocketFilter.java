package com.momoc.netty.frame.filter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Indexed;

/**
 * @author momoc
 * @version 1.0
 * @className WebsocketFilter
 * @description
 * @date 2023/7/7 17:40
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface WebsocketFilter {
}
