package com.ahuxh.netty;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel sh) throws Exception {
        ChannelPipeline p = sh.pipeline();
        p.addLast(new HttpServerCodec());
        p.addLast(new HttpObjectAggregator(1024*1024));
//        p.addLast();
        p.addLast(new HttpHandler());
    }
}
