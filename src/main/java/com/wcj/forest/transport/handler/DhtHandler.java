package com.wcj.forest.transport.handler;

import com.wcj.forest.transport.codec.Bencode;
import com.wcj.forest.transport.server.Sender;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wengchengjian
 * @date 2023/4/7-17:22
 */
@Slf4j
@AllArgsConstructor
public class DhtHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private static final String LOG = "[DHT Handler]-";

    private Sender sender;

    private Bencode bencode;

    private ProcessManager processManager;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        sender.add(ctx.channel().remoteAddress(), ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {

    }
}
