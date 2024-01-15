//package com.momoc.netty.frame.context;
//
//import com.google.protobuf.MessageLite;
//import com.google.protobuf.MessageLiteOrBuilder;
//import com.momoc.netty.frame.FrameMethodUtils;
//import com.momoc.netty.frame.config.NettyProperties;
//import com.momoc.netty.frame.dispatch.annotation.NettyDispatchController;
//import com.momoc.netty.frame.dispatch.annotation.NettyDispatchRequest;
//import com.momoc.netty.frame.dispatch.model.PacketMassageInfo;
//import com.momoc.netty.frame.handler.DispatchHandler;
//import com.momoc.netty.frame.handler.ProtobufDecoder;
//import java.lang.reflect.Method;
//import java.lang.reflect.Parameter;
//import java.util.HashMap;
//import java.util.Map;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.stereotype.Component;
//
///**
// * protobuf 解码中心  和 转发消息构建
// *
// * @author momoc
// * @version 1.0
// * @className ProtobufDecoderContext
// * @description
// * @date 2023/7/3 17:14
// */
//@Deprecated
//public class ProtobufDecoderOrBuildDispatchInfoContext_back implements BeanPostProcessor {
//    static Logger log = LoggerFactory.getLogger(DispatchHandler.class);
//
//
//    /**
//     * 待解决问题：消息发送出去时候的消息号编码问题
//     */
//    @Autowired
//    NettyProperties nettyProperties;
//
//    /**
//     * 消息号
//     */
//    static Map<Integer, PacketMassageInfo> packetMassageInfoMap = new HashMap<>();
//    static Map<Integer, PacketMassageInfo> requestPacketMsgIdInfoMap = new HashMap<>();
//    static Map<Integer, PacketMassageInfo> responsePacketMsgIdInfoMap = new HashMap<>();
//
//    public static Map<Integer, PacketMassageInfo> getPacketMassageInfoMap() {
//        return packetMassageInfoMap;
//    }
//
//    @Override
//    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        Class<?> beanClass = bean.getClass();
//        NettyDispatchController nettyDispatchController = beanClass.getAnnotation(NettyDispatchController.class);
//        if (nettyDispatchController != null) {
//
//            for (Method method : beanClass.getMethods()) {
//                NettyDispatchRequest nettyDispatchRequest = method.getAnnotation(NettyDispatchRequest.class);
//                if (nettyDispatchRequest != null) {
//                    //请求消息id
//                    int msgReqId = nettyDispatchRequest.packetReqId();
//                    //响应消息id
//                    int msgRespId = nettyDispatchRequest.packetRespId();
//                    boolean openChannelContext = nettyDispatchRequest.openChannelContext();
//                    if (method.getParameters().length == 0) {
//                        log.error("方法未定义入参 或 未定义入参消息:{}", msgReqId);
//                        throw new RuntimeException("方法未定义入参 或 未定义入参消息");
//                    }
//                    //取去第一个为消息参数
//                    Parameter parameter1 = method.getParameters()[0];
//                    //方法参数
//                    Class<?> type = parameter1.getType();
//                    //仅支持protobuf 3，判断是不是protobuf3 的子类
//                    if (!MessageLiteOrBuilder.class.isAssignableFrom(type)) {
//                        throw new RuntimeException(String.format("消息:%s 入参不是protobuf生成的类", msgReqId));
//                    }
//                    //响应参数
//                    Class<?> returnType = method.getReturnType();
//                    try {
//                        Method getDefaultInstanceForType = type.getMethod("getDefaultInstance");
//                        MessageLite invoke = (MessageLite) getDefaultInstanceForType.invoke(null, null);
//                        //构建消息转发必要信息
//                        PacketMassageInfo packetMassageInfo = new PacketMassageInfo();
//                        //对应的方法
//                        packetMassageInfo.setMethod(method);
//                        //对应的解码器类
//                        ProtobufDecoder protobufDecoder = new ProtobufDecoder(invoke);
//                        packetMassageInfo.setProtobufDecoder(protobufDecoder);
//                        //设置对的bean
//                        packetMassageInfo.setBean(bean);
//                        packetMassageInfo.setOpenChannelContext(openChannelContext);
//                        if (packetMassageInfoMap.containsKey(msgReqId)) {
//                            log.error("消息定义重复，请检查,msgId:{}", msgReqId);
//                            throw new RuntimeException("消息定义重复，请检查");
//                        }
//                        //放map中
//                        packetMassageInfoMap.put(msgReqId, packetMassageInfo);
//                        //如果没设置消息netty ，直接拿proto命名后面的数字
//                        if (msgReqId == 0) {
//                            String name = type.getName();
//                            String[] classNameAndMsgId = name.split("_");
//                            if (classNameAndMsgId.length != 2) {
//                                log.error("未设置消息请求id,class:{} method:{}", beanClass, method.getName());
//                                throw new RuntimeException("未设置消息请求id");
//                            }
//                            msgReqId = Integer.parseInt(classNameAndMsgId[1]);
//                        }
//                        //取返回参数proto类型
//                        if (msgRespId == 0) {
//                            String name = returnType.getName();
//                            //下划线分割
//                            String[] classNameAndMsgId = name.split("_");
//                            if (classNameAndMsgId.length != 2) {
//                                log.error("未设置消息响应id,class:{} method:{}", beanClass, method.getName());
//                                throw new RuntimeException("未设置消息响应id");
//                            }
//                            msgRespId = Integer.parseInt(classNameAndMsgId[1]);
//                        }
//                        //将对应的消息id，转换为byte[]字节, why? 因为protobuf传输是二进制传输数据。
//                        Integer msgIdLength = nettyProperties.getMsgIdLength();
//                        byte[] msgReqIdBytes = FrameMethodUtils.fillBinaryToMsgLength(msgIdLength, msgReqId);
//                        byte[] msgRespIdBytes = FrameMethodUtils.fillBinaryToMsgLength(msgIdLength, msgRespId);
//                        packetMassageInfo.setMsgReqByte(msgReqIdBytes);
//                        packetMassageInfo.setMsgRespByte(msgRespIdBytes);
//                    } catch (Exception e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        }
//
//
//        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
//    }
//}
