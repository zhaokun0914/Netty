package com.globebill.nio.netty.codec2;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @author Kavin
 * @date 2023/3/18 16:51
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当通道就绪就会触发该方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("client ctx = " + JSON.toJSONString(ctx));
        MyDataInfo.MyMessage myMessage = getMyMessage();
        ctx.writeAndFlush(myMessage);
    }

    private MyDataInfo.MyMessage getMyMessage() {
        int random = new Random().nextInt(3);
        MyDataInfo.MyMessage myMessage = null;
        if (random == 0) {
            MyDataInfo.Student student = MyDataInfo.Student
                    .newBuilder()
                    .setId(1)
                    .setName("李四")
                    .build();

            myMessage = MyDataInfo.MyMessage
                    .newBuilder()
                    .setDataType(MyDataInfo.MyMessage.DataType.StudentType)
                    .setStudent(student)
                    .build();
        } else if (random == 1) {
            MyDataInfo.Worker worker = MyDataInfo.Worker
                    .newBuilder()
                    .setAge(28)
                    .setName("老王")
                    .build();

            myMessage = MyDataInfo.MyMessage
                    .newBuilder()
                    .setDataType(MyDataInfo.MyMessage.DataType.WorkerType)
                    .setWorker(worker)
                    .build();
        }
        return myMessage;
    }

    /**
     * 当通道有读取事件时，会触发
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 将msg转成一个 ByteBuf
        // ByteBuf是netty提供的，不是NIO的ByteBuffer
        ByteBuf buf = (ByteBuf) msg;
        log.info("服务器回复的消息是: " + buf.toString(CharsetUtil.UTF_8));
        // log.info("服务器地址是: " + ctx.channel().remoteAddress().toString().substring(1));
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
