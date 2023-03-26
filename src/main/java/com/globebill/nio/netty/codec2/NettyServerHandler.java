package com.globebill.nio.netty.codec2;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;


/**
 * 自定义一个Handler，需要继承netty规定好的某个HandlerAdapter
 * 这时我们自定义的Handler才能称之为一个Handler
 *
 * @author Kavin
 * @date 2023/3/18 16:20
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<MyDataInfo.MyMessage> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyDataInfo.MyMessage msg) throws Exception {
        MyDataInfo.MyMessage.DataType dataType = msg.getDataType();
        if (dataType == MyDataInfo.MyMessage.DataType.StudentType) {
            MyDataInfo.Student student = msg.getStudent();
            log.info("收到了学生:{}", JSON.toJSONString(student));
        } else if (dataType == MyDataInfo.MyMessage.DataType.WorkerType) {
            MyDataInfo.Worker worker = msg.getWorker();
            log.info("收到了打工人:{}", JSON.toJSONString(worker));
        } else {
            log.info("传输的类型不正确");
        }
    }
}
