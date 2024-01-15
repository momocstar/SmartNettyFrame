package com.momoc.netty.frame.config;

import com.momoc.netty.frame.NettyServer;
import com.momoc.netty.frame.context.ProtobufHandlerMappingContext;
import com.momoc.netty.frame.context.ProtobufScanningImport;
import com.momoc.netty.frame.event.WebsocketEventContext;
import com.momoc.netty.frame.filter.WebsocketFilterHandler;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * @author momoc
 * @version 1.0
 * @className EnableMomocNetty
 * @description
 * @date 2023/7/5 15:34
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({
        SmartNettyProperties.class,
        ProtobufScanningImport.class,
        ProtobufHandlerMappingContext.class,
        WebsocketEventContext.class,
        NettyServer.class,
        WebsocketFilterHandler.class
})
public @interface EnableMomocNettyFrame {

    /**
     * proto文件扫描路径，会扫描proto相关文件，默认扫描启动类下所有路径
     *
     * @return
     */
    String[] protoClassScanPath() default {};
}
