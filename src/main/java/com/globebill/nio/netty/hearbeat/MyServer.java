package com.globebill.nio.netty.hearbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author Kavin
 * @date 2023/3/19 18:50
 */
@Slf4j
public class MyServer {

    public static void main(String[] args) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(8);

        try {
            // 创建服务器启动类，配置启动参数
            ServerBootstrap s = new ServerBootstrap();
            s.group(bossGroup, workerGroup)// 设置两个线程组
             .channel(NioServerSocketChannel.class)// 使用NioServerSocketChannel作为服务器的通道实现
             .option(ChannelOption.SO_BACKLOG, 128)// 设置线的阻塞程队列大小
             .handler(new LoggingHandler(LogLevel.INFO))// 在 bossGroup 增加一个日志处理器
             .childOption(ChannelOption.SO_KEEPALIVE, true)// 设置为保持连接
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 // 给pipeline设置处理器
                 @Override
                 protected void initChannel(SocketChannel ch) throws Exception {
                     // IdleStateHandler 是 Netty 提供的处理空闲状态的处理器
                     // readerIdleTimeSeconds – 表示多长时间没有读，就会发送一个心跳检测包检测是否连接
                     // writerIdleTimeSeconds – 表示多长时间没有写，就会发送一个心跳检测包检测是否连接
                     // allIdleTimeSeconds - 表示多长时间没有读写，就会发送一个心跳检测包检测是否连接
                     ch.pipeline().addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS))
                       // 当 IdleStateEvent 出发后，就会传递给管道的下一个 handler 去处理，通过调用下一个 handler 的 userEventTriggered(可能是读空闲、写空闲、读写空闲)
                       .addLast(new MyServerHandler());
                 }
             });

            log.info("服务器已启动...");

            // 启动并绑定一个端口，并且同步处理，成生了一个 ChannelFuture 对象
            ChannelFuture f = s.bind(9099).addListener(future -> {
                if (future.isDone()) {
                    log.info("端口[" + 9099 + "]已绑定！");
                }
            }).addListener(future -> {
                if (future.isSuccess()) {
                    log.info("端口[" + 9099 + "]绑定成功！");
                } else {
                    log.info("端口[" + 9099 + "]绑定失败！");
                }
            }).sync();

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
