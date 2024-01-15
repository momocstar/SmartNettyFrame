package com.momoc.netty.frame.event;

import com.momoc.netty.frame.event.annotation.WebsocketCloseEvent;
import com.momoc.netty.frame.event.annotation.WebsocketConnectEvent;
import com.momoc.netty.frame.event.annotation.WebsocketEvent;
import io.netty.channel.Channel;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 注册对应的事件类型
 *
 * @author momoc
 * @version 1.0
 * @className WebsocketEventContext
 * @description
 * @date 2023/7/5 13:42
 */
@Component
public class WebsocketEventContext implements BeanPostProcessor, ApplicationContextAware {
    static Logger log = LoggerFactory.getLogger(WebsocketEventContext.class);
    private static WebsocketEventContext websocketEventContext;

    public WebsocketEventContext() {
        websocketEventContext = this;
    }

    public static WebsocketEventContext getWebsocketEventContext() {
        return websocketEventContext;
    }

    /**
     * 支持的事件
     */
    List<Class<? extends Annotation>> eventList = new ArrayList<Class<? extends Annotation>>() {{
        add(WebsocketCloseEvent.class);
        add(WebsocketConnectEvent.class);
    }};

    /**
     * 存放扫描被注解标记的方法
     */
    private static final Map<String, List<WebsocketEventInfo>> eventMethodMap = new HashMap<>();


    ApplicationContext applicationContext;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> aClass = bean.getClass();
        WebsocketEvent websocketEvent = aClass.getAnnotation(WebsocketEvent.class);
        if (websocketEvent != null) {
            for (Method method : aClass.getMethods()) {
                for (Class<? extends Annotation> aClass1 : eventList) {
                    Annotation annotation = method.getAnnotation(aClass1);
                    if (annotation != null) {
                        Parameter[] parameters = method.getParameters();
                        if (parameters.length == 0) {
                            throw new RuntimeException(String.format("class:%s no definition one param, notice: param must be io.netty.channel.Channel.Channel", aClass.getName()));
                        }
                        if (!Channel.class.isAssignableFrom(parameters[0].getType())) {
                            throw new RuntimeException(String.format("class:%s no definition one param, notice: param must be io.netty.channel.Channel.Channel", aClass.getName()));
                        }
                        String name = aClass1.getName();
                        List<WebsocketEventInfo> methods = eventMethodMap.computeIfAbsent(name, k -> new ArrayList<>());
                        //设置bean
                        WebsocketEventInfo websocketEventInfo = new WebsocketEventInfo();
                        websocketEventInfo.setBean(bean);
                        websocketEventInfo.setMethod(method);
                        methods.add(websocketEventInfo);
                    }
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    /**
     *  调用对应的发方法类
     * @param channel
     * @param cls
     */
    public static void getEventMethodsAndInvoke(Channel channel, Class<? extends Annotation> cls) {
        String name = cls.getName();
        /**
         * 获取被下面两个注解标记的方法
         * WebsocketCloseEvent
         * WebsocketConnectEvent
         */
        List<WebsocketEventInfo> eventMethods = eventMethodMap.getOrDefault(name, null);
        if (eventMethods != null && !eventMethods.isEmpty()) {
            for (WebsocketEventInfo websocketEventInfo : eventMethods) {
                try {
                    //执行对应的事件方法
                    websocketEventInfo.getMethod().invoke(websocketEventInfo.getBean(), channel);
                } catch (Exception e) {
                    log.error("websocketEvent doInvoke exception,method:{} ", websocketEventInfo.method.getName(), e);
                }
            }
        }
    }


}
