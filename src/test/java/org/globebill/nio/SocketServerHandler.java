package org.globebill.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * @author Kavin
 * @date 2023/3/18 20:43
 */
public class SocketServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = null;
        try {
//            log.info("接收到来自客户端的信息：{}"+ ctx.channel().remoteAddress().toString().substring(1));

            buf = (ByteBuf) msg;
            //System.out.println(msg);
            int bodylenth = buf.readableBytes();
            byte[] requestMsg = new byte[bodylenth];
            buf.readBytes(requestMsg);//长度外的内容
//            //System.out.println(lof.byte2HexStr(requestMsg));
//            String s = lof.initSize(Integer.toString(bodylenth, 16));
//            byte[] lenByteHex = lof.hexStr2Bytes(s);
//            byte[] trueBody = lof.assemble(lenByteHex, requestMsg);//组装包
//             TradingService tradingService = new TradingService(requestMsg);
//             byte[] return_body = tradingService.mainService();

            // ctx.write(Unpooled.copiedBuffer(return_body));
        } catch (Exception e) {
            e.printStackTrace();
        } finally { // 释放资源
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)// 4
           .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
    }

}
