package com.momoc.netty.frame.event;

import java.lang.reflect.Method;

/**
 * @author momoc
 * @version 1.0
 * @className WebsocketEventINfo
 * @description
 * @date 2023/7/5 13:50
 */
public class WebsocketEventInfo {

    /**
     * 事件方法
     */
    Method method;
    /**
     *  spring 容器的bean
     */
    Object bean;

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
}
