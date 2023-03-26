package com.globebill.nio.netty.groupchat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @author Kavin
 * @date 2023/3/19 16:35
 */
@Slf4j
public class GroupChatClient {

    private String host = "127.0.0.1";

    private int port;

    public GroupChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void run() {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(eventExecutors)
             .channel(NioSocketChannel.class)
             .option(ChannelOption.SO_KEEPALIVE, true)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline().addLast("decoder", new StringDecoder());// 向 pipeline 加入一个解码器
                     ch.pipeline().addLast("encoder", new StringEncoder());// 再向 pipeline 加入一个编码器
                     ch.pipeline().addLast("myHandler", new GroupChatClientHandler());
                 }
             });

            ChannelFuture f = b.connect(new InetSocketAddress(host, port)).addListener(listener -> {
                if (listener.isSuccess()) {
                    log.info("客户端连接成功...");
                }
            }).sync();

            Channel channel = f.channel();
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                if ("exit".equals(msg)) {
                    return;
                }
                channel.writeAndFlush(msg);
            }

            f.channel().closeFuture().sync();




        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            eventExecutors.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        GroupChatClient client = new GroupChatClient("127.0.0.1", 9099);
        client.run();
    }

}
