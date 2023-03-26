package com.globebill.nio.netty.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kavin
 * @date 2023/3/19 14:57
 */
@Slf4j
public class GroupChatServer {

    private int PROT = 9099;

    public GroupChatServer(int PROT) {
        this.PROT = PROT;
    }

    /**
     * 编写run方法，处理客户端的请求
     */
    public void run() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(8);
        try {
            ServerBootstrap s = new ServerBootstrap();
            s.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .option(ChannelOption.SO_BACKLOG, 128)
             .childOption(ChannelOption.SO_KEEPALIVE, true)
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast("decoder", new StringDecoder());// 向 pipeline 加入一个解码器
                     ch.pipeline().addLast("encoder", new StringEncoder());// 再向 pipeline 加入一个编码器
                     ch.pipeline().addLast("myHandler", new GroupChatServerHandler());// 加入自己的业务处理 Handler
                 }
             });
            ChannelFuture f = s.bind(PROT).addListener(listener -> {
                if (listener.isSuccess()) {
                    log.info("netty服务器启动成功");
                }
            }).sync();
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        GroupChatServer chatServer = new GroupChatServer(9099);
        chatServer.run();
    }


}
