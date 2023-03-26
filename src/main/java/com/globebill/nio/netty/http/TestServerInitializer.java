package com.globebill.nio.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author Kavin
 * @date 2023/3/18 22:22
 */
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 向管道加入处理器
        ChannelPipeline pipeline = ch.pipeline();

        // 加入一个netty提供的HttpServerCodec codec -> [coder - decoder]
        // HttpServerCodec 说明 -> netty提供的处理http的编解码器
        pipeline.addLast("MyHttpServerCodec", new HttpServerCodec())
                .addLast("MyTestHttpServerHandler", new TestHttpServerHandler());// 增加一个自定义的Handler
    }

}
