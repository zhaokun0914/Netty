package org.globebill.nio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.junit.Test;

public class NettyServer {

    @Test
    public void test() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup(8);
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .childOption(ChannelOption.SO_KEEPALIVE, true)
             .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(1024 * 64))//这行配置比较重要
             // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
                 public void initChannel(SocketChannel ch)
                         throws Exception {
                     ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 64, 0, 2));
                     ch.pipeline().addLast(new SocketServerHandler());
                 }
             }); // (5)
            b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);
            b.option(ChannelOption.SO_TIMEOUT, 30000);
            b.option(ChannelOption.SO_BACKLOG, 128);
            b.option(ChannelOption.SO_RCVBUF, 1024 * 64);
            ChannelFuture f = b.bind(9099).sync(); // (7)
            //System.out.println("nio server start...");
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
