package com.globebill.nio.netty.inbounthandlerandoutboundhandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * 入站和出站总结：不用关心入站、出站、自定义Handler的顺序问题，只要确保[同一类型]下[不同ChannelHandler实例]的顺序即可
 * 例如，假设我们创建了以下管道：
 *    ChannelPipeline p = ...;
 *    p.addLast("1", new InboundHandlerA());
 *    p.addLast("2", new InboundHandlerB());
 *    p.addLast("3", new OutboundHandlerA());
 *    p.addLast("4", new OutboundHandlerB());
 *    p.addLast("5", new InboundOutboundHandlerX());
 *
 *  在上面的示例中，名称以Inbound开头的类表示它是一个入站处理程序。名称以Outbound开头的类表示它是一个出站处理程序。
 *  在给定的示例配置中，当事件进入时，处理程序评估顺序为 1、2、3、4、5。当事件出站时，顺序为 5、4、3、2、1。在此原则之上， ChannelPipeline跳过某些处理程序的评估以缩短堆栈深度：
 *  3 和 4 没有实现ChannelInboundHandler ，因此入站事件的实际评估顺序将是：1、2 和 5。
 *  1 和 2 没有实现ChannelOutboundHandler ，因此出站事件的实际评估顺序将是：5、4 和 3。
 *  如果 5 同时实现ChannelInboundHandler和ChannelOutboundHandler ，则入站和出站事件的评估顺序可能分别为 125 和 543。
 *
 * @author Kavin
 * @date 2023年03月21日 16:57
 */
public class MySererInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // 入站的解码 Handler, MyByteToLongDecoder
        pipeline.addLast(new MyByteToLongDecoder());// inBound
        // 读客户端时↓
        pipeline.addLast(new MyLongToByteEncoder()); // outBound
        // 读客户端时↓  给客户端写时↑
        pipeline.addLast(new MyServerHandler());
    }
}
