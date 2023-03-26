package com.globebill.nio.netty.server;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


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

        // normalProcess(ctx, msg);

        // 比如我我们这里有一个非常耗费时间的业务 -> 我们希望他能异步执行 -> 提交到该 channel 对应的 NIOEventLoop 的taskQueue中
        // 解决方案1 用户程序自定义的普通任务
        // eventLoopForTaskQueue(ctx, msg);

        // 解决方案2 用户自定义定时任务
        // 该任务是提交到 scheduleTaskQueue中
        eventLoopForScheduleTaskQueue(ctx, msg);

    }

    private static void eventLoopForScheduleTaskQueue(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        log.info("客户端发送的消息是: " + buf.toString(CharsetUtil.UTF_8));
        ctx.channel().eventLoop().schedule(() -> {
            log.info("这是执行休眠 5 秒的线程");
            ctx.channel().writeAndFlush(Unpooled.copiedBuffer("服务器刚才执行了5秒的任务", CharsetUtil.UTF_8));
        }, 5, TimeUnit.SECONDS);
        log.info("go on ...");
    }

    private static void eventLoopForTaskQueue(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        log.info("客户端发送的消息是: " + buf.toString(CharsetUtil.UTF_8));

        ctx.channel().eventLoop().execute(() -> {
            try {
                log.info("这是执行休眠 10 秒的线程");
                TimeUnit.SECONDS.sleep(10);
                ctx.channel().writeAndFlush(Unpooled.copiedBuffer("服务器刚才执行了10秒的任务", CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        ctx.channel().eventLoop().execute(() -> {
            try {
                log.info("这是执行休眠 20 秒的线程");
                TimeUnit.SECONDS.sleep(20);
                ctx.channel().writeAndFlush(Unpooled.copiedBuffer("服务器刚才执行了20秒的任务", CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        log.info("go on ...");
    }

    private static void normalProcess(ChannelHandlerContext ctx, Object msg) {
        log.info("服务器读取线程 " + Thread.currentThread().getName());
        log.info("server ctx = " + JSON.toJSONString(ctx));
        log.info("看看 channel 和 pipeline 的关系");

        // 将msg转成一个 ByteBuf
        // ByteBuf是netty提供的，不是NIO的ByteBuffer
        ByteBuf buf = (ByteBuf) msg;
        log.info("客户端发送的消息是: " + buf.toString(CharsetUtil.UTF_8));
        log.info("客户端地址是: " + ctx.channel().remoteAddress().toString().substring(1));
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
