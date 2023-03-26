package com.globebill.nio.netty.codec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer {
    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建服务器启动类，配置启动参数
            ServerBootstrap s = new ServerBootstrap();
            s.group(bossGroup, workerGroup)// 设置两个线程组
             .channel(NioServerSocketChannel.class)// 使用NioServerSocketChannel作为服务器的通道实现
             .option(ChannelOption.SO_BACKLOG, 128)// 设置线的阻塞程队列大小
             .childOption(ChannelOption.SO_KEEPALIVE, true)// 设置为保持连接
             .childHandler(
                     new ChannelInitializer<SocketChannel>() {// 给workerGroup设置一个 ChannelInitializer 对象，用于初始化每个客户端连接的处理器,即 NettyServerHandler
                         // 给pipeline设置处理器
                         @Override
                         protected void initChannel(SocketChannel ch) throws Exception {
                            // 在pipeline中加入 protoBuf 的解码器
                             ch.pipeline().addLast(new ProtobufDecoder(StudentPojo.Student.getDefaultInstance()));
                             ch.pipeline().addLast(new NettyServerHandler());
                         }
                     });

            log.info("服务器已启动...");

            // 启动并绑定一个端口，并且同步处理，成生了一个 ChannelFuture 对象
            ChannelFuture f = s.bind(9099).sync();
            // 对关闭通道进行监听
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
