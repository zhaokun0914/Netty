package com.globebill.nio.netty.codec;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;


/**
 * 自定义一个Handler，需要继承netty规定好的某个HandlerAdapter
 * 这时我们自定义的Handler才能称之为一个Handler
 *
 * @author Kavin
 * @date 2023/3/18 16:20
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读取数据（这里我们可以读取客户端发送的数据）
     *
     * @param ctx 上下文对象，含有 pipeline（管道），channel（通道），address（地址）
     * @param msg 客户端发送过来的消息，默认是Object类型
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        StudentPojo.Student student = (StudentPojo.Student) msg;
        log.info("客户端发送的数据 Student:{}", JSON.toJSONString(student));
    }

    /**
     * 数据读取完毕
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将数据写入缓存并刷新
        // 一般来讲，我们队这个发送的数据进行编码
        String response = "Hello, 客户端~";
        ByteBuf respBuf = ctx.alloc().buffer(response.length());
        respBuf.writeBytes(response.getBytes());
        ctx.writeAndFlush(respBuf);
    }

    /**
     * 处理异常，一般来说就是关闭通道
     *
     * @param ctx   上线文对象
     * @param cause 异常对象
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
