package com.momoc.netty.frame;

import com.momoc.netty.frame.config.SmartNettyProperties;
import com.momoc.netty.frame.handler.WebSocketInitHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 启动 netty
 *
 * @author momoc
 * @version 1.0
 * @className NettyServer
 * @description
 * @date 2023/6/27 17:27
 */
@Component
public class NettyServer implements ApplicationRunner {
    Logger log = LoggerFactory.getLogger(NettyServer.class);
    SmartNettyProperties smartNettyProperties;

    private final EventLoopGroup bossGroup;

    private final EventLoopGroup workerGroup;
    private final ServerBootstrap bootstrap = new ServerBootstrap();//netty服务端启动类

    NettyServer(SmartNettyProperties smartNettyProperties) {
        bossGroup = new NioEventLoopGroup(smartNettyProperties.getBossThreadNums());
        workerGroup = new NioEventLoopGroup(smartNettyProperties.getWorkThreadNums());
        this.smartNettyProperties = smartNettyProperties;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new WebSocketInitHandler())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, smartNettyProperties.getConnectTimeout())
//                .option(ChannelOption.TCP_NODELAY, nettyProperties.getTcpNoDelay())
        ;
        ChannelFuture future = bootstrap.bind(smartNettyProperties.getPort()).sync();
        Channel serverChannel = future.channel();
        serverChannel.closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        });
        if (future.isSuccess()) {
            log.info("netty启动完成,port:{}", smartNettyProperties.getPort());
        }
    }

}
