package com.momoc.netty.frame.context;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.MessageLite;
import com.momoc.netty.frame.FrameMethodUtils;
import com.momoc.netty.frame.config.SmartNettyProperties;
import com.momoc.netty.frame.dispatch.annotation.NettyDispatchController;
import com.momoc.netty.frame.dispatch.annotation.NettyDispatchRequest;
import com.momoc.netty.frame.dispatch.model.PacketMassageInfo;
import com.momoc.netty.frame.handler.DispatchWebSocketHandler;
import com.momoc.netty.frame.handler.ProtobufDecoder;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * protobuf 解码中心  和 转发消息构建
 *
 * @author momoc
 * @version 1.0
 * @className ProtobufDecoderContext
 * @description
 * @date 2023/7/3 17:14
 */
public class ProtobufHandlerMappingContext implements BeanPostProcessor {
    static Logger log = LoggerFactory.getLogger(DispatchWebSocketHandler.class);


    /**
     * 待解决问题：消息发送出去时候的消息号编码问题
     */
    @Autowired
    SmartNettyProperties smartNettyProperties;

    /**
     * 消息号
     */
    static Map<Integer, PacketMassageInfo> MessagePacketInfoMap = new HashMap<>();
    static Map<Integer, byte[]> responseMessageByteMap = new HashMap<>();



    /**
     * 这里主要最用是补齐请求消息
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        NettyDispatchController nettyDispatchController = beanClass.getAnnotation(NettyDispatchController.class);
        if (nettyDispatchController != null) {
            for (Method method : beanClass.getMethods()) {
                NettyDispatchRequest nettyDispatchRequest = method.getAnnotation(NettyDispatchRequest.class);
                if (nettyDispatchRequest != null) {
                    //取去第一个为消息参数
                    Parameter parameter1 = method.getParameters()[0];
                    //方法参数
                    Class<?> type = parameter1.getType();
                    //请求消息id
                    boolean openChannelContext = nettyDispatchRequest.openChannelContext();
                    //如果没设置消息netty ，直接拿proto命名后面的数字
                    String name = type.getName();
                    String[] classNameAndMsgId = name.split("_");
                    if (classNameAndMsgId.length != 2) {
                        throw new RuntimeException(String.format("no set request msgId,class:%s method:%s",  beanClass, method.getName()));
                    }
                    int msgReqId = Integer.parseInt(classNameAndMsgId[1]);
                    //仅支持protobuf 3，判断是不是protobuf3 的子类
                    if (!GeneratedMessageV3.class.isAssignableFrom(type)) {
                        throw new RuntimeException(String.format("消息:%s 入参不是protobuf生成的类", msgReqId));
                    }
                    try {
                        Method getDefaultInstanceForType = type.getMethod("getDefaultInstance");
                        MessageLite invoke = (MessageLite) getDefaultInstanceForType.invoke(null, null);
                        //构建消息转发必要信息
                        PacketMassageInfo requestPacketInfo = new PacketMassageInfo();
                        //对应的方法
                        requestPacketInfo.setMethod(method);
                        //对应的解码器类
                        ProtobufDecoder protobufDecoder = new ProtobufDecoder(invoke);
                        requestPacketInfo.setProtobufDecoder(protobufDecoder);
                        //设置对的bean
                        requestPacketInfo.setBean(bean);
                        //将对应的消息id，转换为byte[]字节, why? 因为protobuf传输是二进制传输数据。
                        Integer msgIdLength = smartNettyProperties.getMsgIdLength();
                        byte[] msgReqIdBytes = FrameMethodUtils.fillBinaryToMsgLength(msgIdLength, msgReqId);
                        requestPacketInfo.setMsgByte(msgReqIdBytes);
                        //这里覆盖ProtobufScanningImport扫描的类信息,因为扫描信息比较全
                        MessagePacketInfoMap.put(msgReqId,requestPacketInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
    public static void registerResponseByteMap(int msgId, byte[] bytes) {
        if (responseMessageByteMap.containsKey(msgId)){
            throw new RuntimeException(String.format(" The current message number has been defined. msgId:%s ", msgId));
        }
        responseMessageByteMap.put(msgId, bytes);
    }




    public static PacketMassageInfo getPacketInfoByMessageId(Integer msgId){
        return MessagePacketInfoMap.get(msgId);
    }


    /**
     *
     * @param msgId
     * @return
     */
    public static byte[] getResponseMsgByte(Integer msgId){
        return responseMessageByteMap.get(msgId);
    }

}
