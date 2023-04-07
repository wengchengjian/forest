package com.wcj.forest.transport.server;

import com.wcj.forest.config.ForestProperties;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author wengchengjian
 * @date 2023/4/7-17:06
 */
@Slf4j
@Component
public class ForestBootstrap implements Runnable {

    private Bootstrap bootstrap;

    private EventLoopGroup workGroup;

    private Integer port;

    public ForestBootstrap(ForestProperties properties) {
        this.port = properties.getPort();
        workGroup = new NioEventLoopGroup();
    }

    @PostConstruct
    public void init() {
        bootstrap.group(workGroup).channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)//是广播,也就是UDP连接
                .option(ChannelOption.SO_RCVBUF, 10000 * 1024)// 设置UDP读缓冲区为3M
                .option(ChannelOption.SO_SNDBUF, 10000 * 1024)// 设置UDP写缓冲区为3M
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler();
        new Thread(this);
    }

    @Override
    public void run() {
        try {
            bootstrap.bind(port).sync().channel().closeFuture().await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
