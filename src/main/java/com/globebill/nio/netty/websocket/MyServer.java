package com.globebill.nio.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kavin
 * @date 2023/3/19 19:30
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
             .childOption(ChannelOption.SO_KEEPALIVE, true)// 设置为保持连接
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 // 给pipeline设置处理器
                 @Override
                 protected void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline()
                       .addLast(new HttpServerCodec())// 因为是基于http协议，所以使用http的编码和解码器
                       .addLast(new ChunkedWriteHandler())// 文件传输等大数据流需要在ChannelHandler实现中进行复杂的状态管理。 ChunkedWriteHandler管理如此复杂的状态，因此您可以毫无困难地发送大型数据流。
                       /*
                        * 1、http 数据在传输过程中是分段，HttpObjectAggregator 就是可以将多个段聚合起来
                        * 2、这就是为什么，当浏览器发送大量数据时，就会发出多次http请求
                        */
                       .addLast(new HttpObjectAggregator(8192))
                       /*
                        * 1、对应websocket，它的数据是以 帧 形式传递
                        * 2、可以看到 WebSocketFrame 下面有六个子类
                        * 3、浏览器请求时 通过ws://localhost:9099/hello 表示请求的uri
                        * 4、WebSocketServerProtocolHandler 的核心功能是将http协议转换成 ws协议，以支持长连接
                        * 5、通过一个状态码101 从 http 升级到 websocket
                        */
                       .addLast(new WebSocketServerProtocolHandler("/hello"))
                       /*
                        * 自动定义的Handler，处理业务逻辑
                        */
                       .addLast(new MyTextWebSocketFrameHandler());
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
