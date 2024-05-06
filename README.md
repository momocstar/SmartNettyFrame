

本项目技术：netty + protobuf、websocket、spring boot 开箱即用
分流地址： https://gitee.com/aurora-momoc

### 使用

1、下载克隆代码，导入idea

2、引入依赖

3、创建springboot 初始化项目，

4、启动类注解：

```java
package com.momoc.multi.chat.room;

import com.momoc.netty.frame.config.EnableMomocNettyFrame;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//启用框架
@EnableMomocNettyFrame(protoClassScanPath = {"com.momoc.multi.chat.room.common.proto"})
@MapperScan("com.momoc.multi.chat.room.common")
@SpringBootApplication
public class AppRun {
    public static void main(String[] args) {
        SpringApplication.run(AppRun.class, args);
    }
}

```



### 配置类：

```java
package com.momoc.netty.frame.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author momoc
 * @version 1.0
 * @className NettyConfig
 * @description
 * @date 2023/6/27 17:38
 */
@ConfigurationProperties(value = "momoc.netty", ignoreInvalidFields = true)
public class SmartNettyProperties {

    private static SmartNettyProperties smartNettyProperties;

    public SmartNettyProperties() {
        smartNettyProperties = this;
    }

    /**
     * netty 启动端口号,默认2300
     */
    private Integer port = 2300;

    /**
     * 处理链接线程,默认为cpu的线程数/8, 不足至少为1
     */
    private Integer bossThreadNums = Runtime.getRuntime().availableProcessors() / 8 == 0 ? 1 : Runtime.getRuntime().availableProcessors() / 8;

    /**
     * 工作线程，主要处理数据写入写出。 cpu线程数 * 2
     */
    private Integer workThreadNums = Runtime.getRuntime().availableProcessors() * 2;
    /**
     * 连接超时
     */
    private Integer connectTimeout = 15000;


    /**
     * 消息id长度，根据需要定义,默认16位
     */
    private Integer msgIdLength = 16;

    private Boolean debug = false;

    /**
     * netty访问链接：ip:端口号/nettyPath
     */
    private String websocketPath = "/ws";

   
}

```

对应的yml

```yaml
momoc:
  netty:
    port: 2300
    # 用于处理链接建立的线程池
    bossThreadNums: 1
    workThreadNums: 2
    connectTimeout: 15000
    #消息id长度，二进制 仅支持8的倍数，最大为32位，目前仅兼容16位 和 32位。可选值16 32
    msgIdLength: 16
    debug: false
    websocketPath: '/ws'

```



### 主要功能：

### 1、全局过滤器

样例

```java
package com.momoc.multi.chat.room.core.filter;

import com.momoc.netty.frame.filter.IWebSocketFilter;
import com.momoc.netty.frame.filter.WebsocketFilter;
import com.momoc.netty.frame.filter.WebsocketFilterChain;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

/**
 * Order 过滤器优先级
 * @author momoc
 * @version 1.0
 * @className FilterTest
 * @description
 * @date 2023/7/7 17:37
 */
@Slf4j
@Order(0)
@WebsocketFilter
public class FilterTest implements IWebSocketFilter {

    @Override
    public void doFilter(ChannelHandlerContext context, WebsocketFilterChain chain) {
        log.info("过滤器1");
        chain.doNextFilter(context, chain);
    }
}

```

### 2、消息转发



#### protobuf消息命名规则

下划线_后面代表消息号。

testMessageRequest_1002: 代表消息号1002，消息号不能重复

testMessageResponse_1003：向前端发送的消息体



样例

Protobuf文件

```protobuf
syntax = "proto3"; // proto3 必须加此注解

option java_package = "com.momoc.multi.chat.room.common.proto"; // 生成类的包名，注意：会在指定路径下按照该包名的定义来生成文件夹
//请求使用样例
message testMessageRequest_1002{
  optional string content = 1;
}
message testMessageResponse_1003{
  optional string content = 1;
}

//公共消息类，不作为消息号
//主要考虑一个类，可能会有多个消息实体使用
message UserInfoVO{

}
//或者
message UserInfoDTO{

}

```

java代码

```java
import com.momoc.multi.chat.room.common.proto.UserMessage;
import com.momoc.netty.frame.dispatch.annotation.NettyDispatchController;
import com.momoc.netty.frame.dispatch.annotation.NettyDispatchRequest;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author momoc
 * @version 1.0
 * @className TestController
 * @description
 * @date 2023/7/3 17:25
 */
@Slf4j
@NettyDispatchController
public class TestController {

    /**
     * 下面是样例
     * ResponseTest_1000 ，名称上有对应的消息号
     * @param message
     * @return
     */
    @NettyDispatchRequest(openChannelContext = true)
    public UserMessage.testMessageResponse_1003 test(UserMessage.testMessageRequest_1002 message) {
        String test = message.getContent();
//        log.info("接收到客户端的信息:{}", test);
        UserMessage.testMessageResponse_1003.Builder builder = UserMessage.testMessageResponse_1003.newBuilder();
        builder.setContent("hello momoc netty protobuf frame");
        return builder.build();
    }

}

```

### 3、weboscket事件

（1）WebsocketConnectEvent 与客户端建立连接事件

（2）WebsocketCloseEvent  与客户端断开事件

```java
//事件注解
@WebsocketEvent
public class SessionManagerContext {

    /**
     * 所有的会话channel
     *
     */
    static Map<String, UserSessionInfo> sessionInfoMap = new ConcurrentHashMap<>();


    @WebsocketConnectEvent
    public void connect(Channel channel){

    }
     @WebsocketCloseEvent
    public void connect(Channel channel){
	
    }
}
```

### 4、心跳检测

建议自己实现一个消息号，用来心跳检查。

2023年07月04日 ，目前已完成最基本的消息转发功能，实现思路是，消息id。

相关注解： [NettyDispatchController.java](src/main/java/com/momoc/multi/chat/room/frame/dispatch/annotation/NettyDispatchController.java) 、 [NettyDispatchRequest.java](src/main/java/com/momoc/multi/chat/room/frame/dispatch/annotation/NettyDispatchRequest.java)

转发器：  [DispatchWebSocketHandler.java]

参考ProtobufDecoder下的解码器：ProtobufDecoder





### 5、与前端交互的工具类



注：下面工具类支持16位消息号，需要变更请自己更改工具类

#### 消息枚举类

```js
/**
 *
 * 这个是消息的枚举类型，
 * 前面是消息ID，后面是查询proto.js 的请求类
 *
 */
import protoRoot from '@/proto/proto'

function ToBinary(value){
    let num = value;
    let resArry = [];
    let xresArry = [];
    let i = 0;
    //除2取余
    for (; num > 0;) {
        resArry.push(num % 2);
        num = parseInt(num / 2);
        i++;
    }
    let j = 0;
    //倒序排列
    for (j = i - 1; j >= 0; j--) {
        xresArry.push(resArry[j]);
    }
    let finalArr = []
    let pos = 0;
    for (; pos < 16 - xresArry.length; pos++) {
        finalArr[pos] = 0;
    }
    for (let k = 0; k < xresArry.length; k++) {
        finalArr[pos] = xresArry[k];
        pos++;
    }
    // return finalArr.join().replace(/,/g, "");
    return finalArr;
}

/**
 * 请求消息id枚举
 * @type {*&{computeMsgPre(*): *, getMsgTypeByMsgId(*): *}}
 */
let PROTO_MSG_ENUM = createEnum(protoRoot.nested);

/**
 * 响应消息枚举
 * @type {*&{computeMsgPre(*): *, getMsgTypeByMsgId(*): *}}
 */
// let PROTO_RESPONSE_MSG_ENUM = createEnum({
//     sessionBuildResponse_100: 100,
//     //消息名 - 消息id
//     ResponseTest_1000: 1000
// });
function createEnum(protoMessageMap) {
    let definition = {}
    for (const messageClassName of Object.keys(protoMessageMap)){
        let arr = messageClassName.split("_");
        //消息不符合规范
        if (arr.length < 2){
            continue
        }
        definition[messageClassName] = arr[1]
    }
    const MsgIdMap = {}
    const computeMsgPreMap = {}
    for (const key of Object.keys(definition)) {
        const value = definition[key];
        MsgIdMap[value] = key
        let binary = ToBinary(value)
        //消息号 的前置补充
        // console.log("二进制后", binary.slice(0, 8).join().replace(/,/g, ""), binary.slice(8, 16).join().replace(/,/g, ""))
        let first = parseInt(binary.slice(0, 8).join('').replace(/,/g, ""),2);
        let second = parseInt(binary.slice(8, 16).join('').replace(/,/g, ""),2);
        // console.log("二进制后", first, second)
        computeMsgPreMap[value] = [first, second]
    }
    return {
        ...definition,
        /**
         * 获取消息id对应的类。
         * @param MsgId
         * @returns {*}
         */
        getMsgTypeByMsgId(MsgId){
            return MsgIdMap[MsgId];
        },
        /**
         * 计算消息的前缀二进制，Uint8Array
         */
        computeMsgPre(MsgId){
            return computeMsgPreMap[MsgId];
        }
    }
}
export  default {
    PROTO_MSG_ENUM
};

```

#### 消息工具类

```js
import protoRoot from '@/proto/proto'
import protobuf from 'protobufjs'
import ProtobufMsgEnumUtils from "@/utils/proto/ProtobufMsgEnumUtils";

/**
 *  编码信息发送给后端,
 * @param msgId 入参消息id
 * @param data 消息对象
 * @returns {Uint8Array}
 */
const encode = (msgId, data) => {
    let massageType = ProtobufMsgEnumUtils.PROTO_MSG_ENUM.getMsgTypeByMsgId(msgId)
    //获取到对应的消息
    let massage = protoRoot.lookup(massageType);
    //编码对应的消息
    let msgBinData = massage.encode(data).finish();
    //编码下当前消息id,放在消息最前面
    let dataList = [];
    let msgPreArr = ProtobufMsgEnumUtils.PROTO_MSG_ENUM.computeMsgPre(msgId)
    for (let i = 0; i <ProtobufMsgEnumUtils.PROTO_MSG_ENUM.computeMsgPre(msgId).length; i++) {
        dataList.push(msgPreArr[i]);
    }
    let pos = dataList.length;
    for (let i = 0; i < msgBinData.length; i++) {
        dataList[pos] = msgBinData[i];
        pos++;
    }
    let finalData = Uint8Array.from(dataList)
    return finalData;
}
/**
 * 对接收到的信息进行解码
 * @param data 二进制数据
 * @returns {*}
 */
const decode = data => {
    let buf = protobuf.util.newBuffer(data)
    //这里是消息id，转换转换成十进制
    let binaryMsgId  = '';
    for (let i = 0; i < 2; i++) {
        binaryMsgId +=  buf[i].toString(2);
    }
    let msgId = parseInt(binaryMsgId,2);
    let protobufData = []
    for (let i = 2; i < buf.length; i++) {
        protobufData.push(buf[i]);
    }
    let massageType = ProtobufMsgEnumUtils.PROTO_MSG_ENUM.getMsgTypeByMsgId(msgId)
    let massage = protoRoot.lookup(massageType)
    return massage.decode(protobufData);
}

export default {
    encode,
    decode
}
```



后记：后续可能会做一个支持自定义协议的消息号转发。

在写一个weboscket的类似聊天室的功能，因没有前端，目前仅后端完成了部分核心功能，在线等一个前端，后续有什么功能想加上也可以，兴趣最重要。



