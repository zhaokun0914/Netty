package com.globebill.nio.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author Kavin
 * @date 2023/3/18 16:34
 */
@Slf4j
public class NettyClient {

    public static void main(String[] args) {
        // 客户端需要一个事件循环组
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            // 创建一个客户端启动对象
            Bootstrap s = new Bootstrap();
            s.group(group) // 设置线程组
             .channel(NioSocketChannel.class)// 设置客户端通道的实现类，将来会用反射来处理的
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast(new NettyClientHandler());
                 }
             });
            log.info("客户端已启动...");


            // 启动客户端去连接服务端
            // 关于 ChannelFuture 后面还要分析，涉及到netty的异步模型
            ChannelFuture channelFuture = s.connect(new InetSocketAddress("127.0.0.1", 9099)).sync();

            // 给关闭通道进行监听
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            group.shutdownGracefully();
        }

    }

}
