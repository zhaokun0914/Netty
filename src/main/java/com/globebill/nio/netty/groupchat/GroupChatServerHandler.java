package com.globebill.nio.netty.groupchat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;

/**
 * @author Kavin
 * @date 2023/3/19 15:09
 */
@Slf4j
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 定义一个Channel组，管理所有的Channel
     * GlobalEventExecutor.INSTANCE是一个全局的事件执行器，是一个单例
     */
    private static final ChannelGroup RECIPIENTS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:ss:mm");

    /**
     * 当连接建立之后第一个被执行的方法，连接一旦建立立即将其加入到channelGroup中
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        RECIPIENTS.add(ctx.channel());
        // 将该客户端加入聊天的信息推送给其他在线的客户端
        // writeAndFlush 会将 recipients 中所有的 channel 遍历，并发送消息，省去了我们自己的遍历
        RECIPIENTS.writeAndFlush("[客户端]" + ctx.channel().remoteAddress().toString().substring(1) + "加入聊天室", channel -> !ctx.channel().equals(channel));
    }

    /**
     * 表示断开连接时触发，channel被remove之后会自动从 ChannelGroup 中移除，因此用户不用关心 ChannelGroup 中 Channel 的生命周期维护
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 将 xx 的离开信息推送给当前在线的客户端
        RECIPIENTS.writeAndFlush("[客户端]" + ctx.channel().remoteAddress().toString().substring(1) + "离开聊天室");
        log.info("当前recipients大小{}", RECIPIENTS.size());
    }

    /**
     * 表示 channel 处于活动状态，提示 xx 上线
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("[服务器]{}上线了", ctx.channel().remoteAddress().toString().substring(1));
    }

    /**
     * 表示 channel 处于非活动状态，提示 xx 离线
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("[服务器]{}下线了", ctx.channel().remoteAddress().toString().substring(1));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 给所有客户端发送消息，排除自己
        String broadcastMsg = "[客户] " + ctx.channel().remoteAddress().toString().substring(1) + " 发送了消息:" + msg + "";
        RECIPIENTS.writeAndFlush(broadcastMsg, channel -> !ctx.channel().equals(channel));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("通道发生异常", cause);
        // 关闭通道
        ctx.close();
    }
}
