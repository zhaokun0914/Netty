package com.globebill.nio.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Kavin
 * @date 2023/3/18 22:20
 */
public class TestServer {

    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap s = new ServerBootstrap();
            s.group(bossGroup, workGroup)
             .channel(NioServerSocketChannel.class)// 使用NioServerSocketChannel作为服务器的通道实现
             .option(ChannelOption.SO_BACKLOG, 128)// 设置服务器可连接队列大小
             .childOption(ChannelOption.SO_KEEPALIVE, true)// 设置为保持连接
             .childHandler(new TestServerInitializer());

            ChannelFuture f = s.bind(8088).sync();
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

}
